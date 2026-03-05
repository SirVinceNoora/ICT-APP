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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ictapp.speedtest.SpeedTestManager
import com.example.ictapp.speedtest.SpeedTestPhase
import com.example.ictapp.ui.components.DetailRow
import com.example.ictapp.ui.components.FieldInput
import com.example.ictapp.ui.components.GlassCard
import com.example.ictapp.ui.theme.ICTAPPTheme
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.pow
import com.example.ictapp.speedtest.SettingsManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkToolsScreen(initialTab: Int = 0, onBack: () -> Unit = {}, settingsManager: SettingsManager? = null) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(initialTab) }
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

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Network Tools", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    Surface(
                        modifier = Modifier.padding(end = 8.dp),
                        color = Color.Transparent
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (currentWifiSsid == "Not Connected") Color.Red.copy(0.2f) else Color.Cyan.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
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
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color.Cyan,
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
            
            Spacer(Modifier.height(24.dp))

            when (selectedTab) {
                0 -> SpeedTestSection()
                1 -> IpCalcSection(settingsManager)
                2 -> NetworkToolsList()
                3 -> WifiScreen()
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
        GlassCard(modifier = Modifier.padding(vertical = 16.dp)) {
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
fun IpCalcSection(settingsManager: SettingsManager? = null) {
    var ipInput by remember { mutableStateOf("192.168.1.1") }
    var maskInput by remember { mutableStateOf("24") }
    
    // Result State
    var networkAddress by remember { mutableStateOf("-") }
    var broadcastAddress by remember { mutableStateOf("-") }
    var usableRange by remember { mutableStateOf("-") }
    var totalHosts by remember { mutableStateOf("-") }
    var netmask by remember { mutableStateOf("-") }

    var showTutorial by remember { mutableStateOf(false) }
    val isTutorialEnabled by settingsManager?.showWelcome?.collectAsState(initial = true) ?: remember { mutableStateOf(true) }

    fun calculate() {
        try {
            val parts = ipInput.split(".")
            if (parts.size != 4) return
            
            val ipInt = parts.map { it.toInt() }.fold(0) { acc, i -> (acc shl 8) + i }
            val mask = maskInput.toInt()
            if (mask !in 0..32) return

            val maskInt = if (mask == 0) 0 else ((-1).toLong() shl (32 - mask)).toInt()
            val netInt = ipInt and maskInt
            val broadcastInt = netInt or maskInt.inv()

            networkAddress = intToIp(netInt)
            broadcastAddress = intToIp(broadcastInt)
            netmask = intToIp(maskInt)
            
            if (mask <= 30) {
                usableRange = "${intToIp(netInt + 1)} - ${intToIp(broadcastInt - 1)}"
                totalHosts = "${2.0.pow(32 - mask).toLong() - 2}"
            } else {
                usableRange = "N/A"
                totalHosts = if (mask == 32) "1" else "2"
            }
        } catch (e: Exception) {
            // Ignore invalid input
        }
    }

    LaunchedEffect(Unit) { 
        calculate() 
        if (isTutorialEnabled) {
            showTutorial = true
        }
    }

    Column(Modifier.padding(vertical = 8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.weight(3f)) {
                OutlinedTextField(
                    value = ipInput,
                    onValueChange = { ipInput = it },
                    label = { Text("Host IP", color = Color.White.copy(0.6f)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Cyan, unfocusedBorderColor = Color.White, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Box(Modifier.weight(1f)) {
                OutlinedTextField(
                    value = maskInput,
                    onValueChange = { maskInput = it },
                    label = { Text("CIDR", color = Color.White.copy(0.6f)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Cyan, unfocusedBorderColor = Color.White, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        Button(
            onClick = { calculate() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Calculate, null)
            Spacer(Modifier.width(8.dp))
            Text("CALCULATE SUBNET", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(24.dp))
        
        Text("CALCULATION RESULTS", color = Color.Cyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Spacer(Modifier.height(12.dp))
        
        GlassCard {
            DetailRow("Network Address", networkAddress)
            DetailRow("Broadcast Address", broadcastAddress)
            DetailRow("Subnet Mask", netmask)
            DetailRow("Usable Host Range", usableRange)
            DetailRow("Total Usable Hosts", totalHosts)
        }
    }

    if (showTutorial) {
        AlertDialog(
            onDismissRequest = { showTutorial = false },
            title = { Text("IP Calculator Guide") },
            text = {
                Text("1. Enter the Host IP address.\n2. Enter the CIDR prefix (e.g., 24 for 255.255.255.0).\n3. Tap 'CALCULATE SUBNET' to see network details like usable host range.")
            },
            confirmButton = {
                TextButton(onClick = { showTutorial = false }) {
                    Text("Got it")
                }
            },
            containerColor = Color(0xFF001F54),
            titleContentColor = Color.Cyan,
            textContentColor = Color.White
        )
    }
}

fun intToIp(ip: Int): String {
    return "${(ip shr 24) and 0xFF}.${(ip shr 16) and 0xFF}.${(ip shr 8) and 0xFF}.${ip and 0xFF}"
}

@Composable
fun NetworkToolsList() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var consoleOutput by remember { mutableStateOf("Ready to diagnose...") }
    var isRunning by remember { mutableStateOf(false) }
    var isContinuous by remember { mutableStateOf(false) }
    var customTarget by remember { mutableStateOf("") }
    var pingJob by remember { mutableStateOf<Job?>(null) }
    val scrollState = rememberScrollState()

    fun stopPing() {
        pingJob?.cancel()
        pingJob = null
        isRunning = false
        consoleOutput += "\n--- Terminated ---"
    }

    fun startPing(host: String) {
        stopPing()
        isRunning = true
        consoleOutput = "PINGING $host...\n"
        pingJob = scope.launch(Dispatchers.IO) {
            try {
                val command = if (isContinuous) "ping $host" else "ping -c 4 $host"
                val process = Runtime.getRuntime().exec(command)
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                var line: String? = reader.readLine()
                while (isActive && line != null) {
                    val currentLine = line!!
                    withContext(Dispatchers.Main) {
                        val currentLines = consoleOutput.split("\n")
                        // Limit lines to last 20 for performance
                        val updatedLines = (currentLines + currentLine).takeLast(20)
                        consoleOutput = updatedLines.joinToString("\n")
                        // Auto-scroll to bottom
                        scrollState.animateScrollTo(scrollState.maxValue)
                    }
                    line = reader.readLine()
                }
                process.destroy()
            } catch (e: Exception) {
                if (isActive) {
                    withContext(Dispatchers.Main) { consoleOutput += "\nError: ${e.message}" }
                }
            } finally {
                withContext(Dispatchers.Main) { isRunning = false }
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Continuous Mode", color = Color.White.copy(0.7f), fontSize = 12.sp, modifier = Modifier.weight(1f))
            Switch(
                checked = isContinuous,
                onCheckedChange = { isContinuous = it },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.Cyan)
            )
        }

        OutlinedTextField(
            value = customTarget,
            onValueChange = { customTarget = it },
            placeholder = { Text("Custom IP / Hostname", color = Color.White.copy(0.3f), fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Cyan, unfocusedBorderColor = Color.White.copy(0.1f), focusedTextColor = Color.White),
            trailingIcon = {
                IconButton(onClick = { if (customTarget.isNotEmpty()) startPing(customTarget) }) {
                    Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color.Cyan)
                }
            }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.weight(1f)) {
                QuickActionRowClickable(Icons.Default.Router, "Local Gateway", isRunning) {
                    if (isRunning) stopPing()
                    else {
                        val gateway = getGatewayIp(context)
                        if (gateway != null) startPing(gateway)
                        else consoleOutput = "Error: Gateway not found."
                    }
                }
            }
            Box(Modifier.weight(1f)) {
                QuickActionRowClickable(Icons.Default.Public, "Google DNS", isRunning) {
                    if (isRunning) stopPing()
                    else startPing("8.8.8.8")
                }
            }
        }
        
        GlassCard(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            Text("LIVE DIAGNOSTIC CONSOLE", color = Color.Cyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = Color.White.copy(0.1f))
            Spacer(Modifier.height(8.dp))
            Column(Modifier.fillMaxSize().verticalScroll(scrollState)) {
                Text(
                    text = consoleOutput, 
                    color = Color(0xFF81D4FA), 
                    fontSize = 11.sp, 
                    lineHeight = 16.sp, 
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun QuickActionRowClickable(icon: ImageVector, title: String, isRunning: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
    ) {
        Row(Modifier.background(Color.White.copy(0.05f)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color.Cyan, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(title, color = Color.White, modifier = Modifier.weight(1f), fontSize = 12.sp)
            Text(if (isRunning) "STOP" else "RUN", color = if (isRunning) Color.Red else Color.Cyan.copy(0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
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

@Preview(showBackground = true)
@Composable
fun NetworkToolsScreenPreview() { 
    ICTAPPTheme { 
        Box(Modifier.background(Color(0xFF001F54))) {
            NetworkToolsScreen() 
        }
    } 
}
