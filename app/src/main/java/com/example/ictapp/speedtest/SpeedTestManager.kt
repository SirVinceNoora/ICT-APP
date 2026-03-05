package com.example.ictapp.speedtest

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

data class SpeedTestState(
    val isRunning: Boolean = false,
    val downloadMbps: Double = 0.0,
    val uploadMbps: Double = 0.0,
    val pingMs: Long = 0,
    val progress: Float = 0f,
    val phase: SpeedTestPhase = SpeedTestPhase.IDLE,
    val error: String? = null
)

enum class SpeedTestPhase {
    IDLE, PING, DOWNLOAD, UPLOAD, FINISHED
}

class SpeedTestManager {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .connectionPool(ConnectionPool(10, 5, TimeUnit.MINUTES))
        .build()

    private val _state = MutableStateFlow(SpeedTestState())
    val state = _state.asStateFlow()

    private var testJob: Job? = null

    fun startTest(scope: CoroutineScope) {
        if (_state.value.isRunning) return
        testJob?.cancel()
        testJob = scope.launch(Dispatchers.IO) {
            try {
                _state.value = SpeedTestState(isRunning = true, phase = SpeedTestPhase.PING)
                runPingTest()

                if (!isActive) return@launch
                _state.value = _state.value.copy(phase = SpeedTestPhase.DOWNLOAD, progress = 0f)
                runDownloadTest()

                if (!isActive) return@launch
                _state.value = _state.value.copy(phase = SpeedTestPhase.UPLOAD, progress = 0f)
                runUploadTest()

                _state.value = _state.value.copy(isRunning = false, phase = SpeedTestPhase.FINISHED, progress = 1f)
            } catch (e: CancellationException) {
                _state.value = SpeedTestState()
            } catch (e: Exception) {
                _state.value = _state.value.copy(isRunning = false, error = "Test failed: ${e.localizedMessage}")
            }
        }
    }

    private suspend fun runPingTest() {
        val pings = mutableListOf<Long>()
        val request = Request.Builder().url("https://8.8.8.8").head().build()
        repeat(5) {
            val start = System.currentTimeMillis()
            try {
                client.newCall(request).execute().use { }
                pings.add(System.currentTimeMillis() - start)
            } catch (e: IOException) {}
            delay(150)
        }
        if (pings.isNotEmpty()) {
            _state.value = _state.value.copy(pingMs = pings.minOrNull() ?: 0)
        }
    }

    private suspend fun runDownloadTest() = coroutineScope {
        val duration = 8000L
        val startTime = System.currentTimeMillis()
        val totalBytes = AtomicLong(0)
        val numStreams = 4
        val url = "https://speed.cloudflare.com/__down?bytes=50000000"

        val jobs = List(numStreams) {
            launch {
                while (System.currentTimeMillis() - startTime < duration && isActive) {
                    val request = Request.Builder().url(url).build()
                    try {
                        client.newCall(request).execute().use { response ->
                            val source = response.body?.source() ?: return@use
                            val buffer = ByteArray(32768)
                            var read: Int
                            while (source.read(buffer).also { read = it } != -1 && isActive) {
                                totalBytes.addAndGet(read.toLong())
                                if (System.currentTimeMillis() - startTime >= duration) break
                                updateSpeed(startTime, totalBytes.get(), true)
                            }
                        }
                    } catch (e: IOException) {
                        delay(200)
                    }
                }
            }
        }
        jobs.joinAll()
    }

    private suspend fun runUploadTest() = coroutineScope {
        val duration = 8000L
        val startTime = System.currentTimeMillis()
        val totalBytes = AtomicLong(0)
        val numStreams = 3
        val payload = ByteArray(1024 * 512)
        val url = "https://speed.cloudflare.com/__up"

        val jobs = List(numStreams) {
            launch {
                while (System.currentTimeMillis() - startTime < duration && isActive) {
                    val requestBody = payload.toRequestBody(null)
                    val request = Request.Builder().url(url).post(requestBody).build()
                    try {
                        client.newCall(request).execute().use {
                            totalBytes.addAndGet(payload.size.toLong())
                            updateSpeed(startTime, totalBytes.get(), false)
                        }
                    } catch (e: IOException) {
                        delay(200)
                    }
                }
            }
        }
        jobs.joinAll()
    }

    private fun updateSpeed(startTime: Long, bytes: Long, isDownload: Boolean) {
        val elapsed = (System.currentTimeMillis() - startTime) / 1000.0
        if (elapsed < 0.5) return
        val mbps = (bytes * 8.0) / (1024.0 * 1024.0 * elapsed)
        val progress = (System.currentTimeMillis() - startTime).toFloat() / 8000f
        _state.value = if (isDownload) {
            _state.value.copy(downloadMbps = mbps, progress = progress.coerceIn(0f, 1f))
        } else {
            _state.value.copy(uploadMbps = mbps, progress = progress.coerceIn(0f, 1f))
        }
    }

    fun cancelTest() {
        testJob?.cancel()
        _state.value = SpeedTestState()
    }
}
