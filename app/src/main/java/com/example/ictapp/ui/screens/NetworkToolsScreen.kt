package com.example.ictapp.ui.screens

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ictapp.speedtest.SpeedTestManager
import com.example.ictapp.speedtest.SpeedTestPhase
import com.example.ictapp.ui.components.DetailRow
import com.example.ictapp.ui.components.FieldInput
import com.example.ictapp.ui.components.GlassCard
import com.example.ictapp.ui.theme.ICTAPPTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun NetworkToolsScreen() {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Speed Test", "IP Calc", "Tools", "Wi-Fi")
    var currentWifiSsid by remember { mutableStateOf("Not Connected") }

    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    DisposableEffect(Unit) {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            @Suppress("DEPRECATION")
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    val info = wifiManager.connectionInfo
                    currentWifiSsid = info.ssid?.replace("\"", "") ?: "Connected"
                    if (currentWifiSsid == "<unknown ssid>") currentWifiSsid = "Connected"
                }
            }
            override fun onLost(network: Network) { currentWifiSsid = "Not Connected" }
        }
        val request = NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
        onDispose { connectivityManager.unregisterNetworkCallback(networkCallback) }
    }

    Box(Modifier.fillMaxSize().statusBarsPadding()) {
        Column(Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Color.Cyan,
                    modifier = Modifier.weight(1f).padding(end = 80.dp),
                    divider = {},
                    indicator = { tabPositions ->
                        if (selectedTab < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = Color.Cyan
                            )
                        }
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontSize = 11.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            when (selectedTab) {
                0 -> SpeedTestSection()
                1 -> IpCalcSection()
                2 -> NetworkToolsList()
                3 -> WifiScreen()
            }
        }

        Surface(
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 8.dp, end = 8.dp),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(bottomStart = 12.dp, topEnd = 4.dp))
                    .background(if (currentWifiSsid == "Not Connected") Color.Red.copy(0.2f) else Color.Cyan.copy(alpha = 0.2f))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(5.dp).clip(RoundedCornerShape(2.5.dp)).background(if (currentWifiSsid == "Not Connected") Color.Red else Color.Cyan))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = currentWifiSsid.uppercase(),
                        color = if (currentWifiSsid == "Not Connected") Color.Red.copy(0.9f) else Color.Cyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SpeedTestSection() {
    val speedTestManager = remember { SpeedTestManager() }
    val state by speedTestManager.state.collectAsState()
    val scope = rememberCoroutineScope()

    val animatedProgress by animateFloatAsState(
        targetValue = state.progress,
        animationSpec = tween(durationMillis = 500),
        label = "progress"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        GlassCard(modifier = Modifier.padding(16.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(Modifier.size(220.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.size(200.dp),
                        color = Color.White.copy(0.05f),
                        strokeWidth = 12.dp,
                    )
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(200.dp),
                        color = Color.Cyan,
                        strokeWidth = 12.dp,
                        trackColor = Color.Transparent
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val displaySpeed = when (state.phase) {
                            SpeedTestPhase.UPLOAD -> state.uploadMbps
                            else -> state.downloadMbps
                        }
                        Text("${displaySpeed.toInt()}", fontSize = 64.sp, fontWeight = FontWeight.Black, color = Color.Cyan)
                        Text("Mbps", color = Color.White.copy(0.5f), fontSize = 18.sp)
                    }
                }
                
                Spacer(Modifier.height(20.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    ResultItem("PING", "${state.pingMs}ms", Icons.Default.Timer)
                    ResultItem("DOWNLOAD", "${state.downloadMbps.toInt()} Mbps", Icons.Default.Download)
                    ResultItem("UPLOAD", "${state.uploadMbps.toInt()} Mbps", Icons.Default.Upload)
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    text = when(state.phase) {
                        SpeedTestPhase.IDLE -> "READY"
                        SpeedTestPhase.PING -> "TESTING LATENCY..."
                        SpeedTestPhase.DOWNLOAD -> "TESTING DOWNLOAD..."
                        SpeedTestPhase.UPLOAD -> "TESTING UPLOAD..."
                        SpeedTestPhase.FINISHED -> "COMPLETED"
                    },
                    color = Color.Cyan.copy(0.8f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(Modifier.height(32.dp))
        Button(
            onClick = {
                if (state.isRunning) speedTestManager.cancelTest()
                else speedTestManager.startTest(scope)
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(0.7f).height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (state.isRunning) Color.Red.copy(0.7f) else Color.Cyan,
                contentColor = Color.Black
            )
        ) {
            Text(
                if (state.isRunning) "STOP TEST" else "START SPEED TEST",
                fontWeight = FontWeight.ExtraBold
            )
        }
        
        state.error?.let {
            Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun ResultItem(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = Color.Cyan.copy(0.6f), modifier = Modifier.size(16.dp))
        Text(label, color = Color.White.copy(0.4f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun IpCalcSection() {
    var ip by remember { mutableStateOf("192.168.1.1") }
    Column(Modifier.padding(8.dp)) {
        FieldInput(ip, { ip = it }, "Enter Host IP")
        Spacer(Modifier.height(20.dp))
        GlassCard {
            val prefix = if (ip.contains(".")) ip.substringBeforeLast(".") else "192.168.1"
            DetailRow("Gateway", "$prefix.1")
            DetailRow("Broadcast", "$prefix.255")
            DetailRow("Subnet", "255.255.255.0 (/24)")
            DetailRow("Network Class", if (ip.startsWith("192")) "Class C" else "Dynamic")
        }
    }
}

@Composable
fun NetworkToolsList() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var pingOutput by remember { mutableStateOf("Ready to diagnose...") }
    var isPinging by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        QuickActionRowClickable(Icons.Default.Router, "Ping Gateway (Local)", isPinging) {
            scope.launch {
                isPinging = true
                pingOutput = "Locating gateway..."
                val gateway = getGatewayIp(context)
                if (gateway != null) {
                    pingOutput = performPing(gateway)
                } else {
                    pingOutput = "Error: Gateway not found."
                }
                isPinging = false
            }
        }
        QuickActionRowClickable(Icons.Default.Public, "Ping External (Google DNS)", isPinging) {
            scope.launch {
                isPinging = true
                pingOutput = performPing("8.8.8.8")
                isPinging = false
            }
        }
        
        GlassCard(modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp)) {
            Text("NETWORK CONSOLE", color = Color.Cyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = Color.White.copy(0.1f))
            Spacer(Modifier.height(8.dp))
            Text(pingOutput, color = Color(0xFF81D4FA), fontSize = 11.sp, lineHeight = 16.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
        }
    }
}

@Composable
fun QuickActionRowClickable(icon: ImageVector, title: String, isLoading: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        enabled = !isLoading,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
    ) {
        Row(Modifier.background(Color.White.copy(0.05f)).padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color.Cyan, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Text(title, color = Color.White, modifier = Modifier.weight(1f), fontSize = 15.sp)
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Cyan, strokeWidth = 2.dp)
            else Text("RUN", color = Color.Cyan.copy(0.7f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

suspend fun performPing(host: String): String = withContext(Dispatchers.IO) {
    try {
        val process = Runtime.getRuntime().exec("ping -c 4 $host")
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val output = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) { output.append(line).append("\n") }
        process.waitFor()
        if (output.isEmpty()) "Connection timed out." else output.toString()
    } catch (e: Exception) { "Error: ${e.message}" }
}

fun getGatewayIp(context: Context): String? {
    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    @Suppress("DEPRECATION")
    val dhcp = wifiManager.dhcpInfo
    return if (dhcp != null && dhcp.gateway != 0) {
        val gateway = dhcp.gateway
        "${gateway and 0xFF}.${(gateway shr 8) and 0xFF}.${(gateway shr 16) and 0xFF}.${(gateway shr 24) and 0xFF}"
    } else null
}

@Preview
@Composable
fun NetworkToolsScreenPreview() { ICTAPPTheme { NetworkToolsScreen() } }
