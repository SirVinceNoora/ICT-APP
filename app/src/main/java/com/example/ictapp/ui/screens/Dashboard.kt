package com.example.ictapp.ui.screens

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.TrafficStats
import android.net.wifi.WifiManager
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ictapp.ui.components.GlassCard
import com.example.ictapp.ui.theme.ICTAPPTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.*

@Composable
fun DashboardContent(logs: Int, deploys: Int, onNavigate: (String) -> Unit = {}) {
    var downloadSpeed by remember { mutableStateOf(0f) }
    var uploadSpeed by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        var lastRxBytes = TrafficStats.getTotalRxBytes()
        var lastTxBytes = TrafficStats.getTotalTxBytes()
        while (isActive) {
            delay(1000)
            val currentRxBytes = TrafficStats.getTotalRxBytes()
            val currentTxBytes = TrafficStats.getTotalTxBytes()
            downloadSpeed = ((currentRxBytes - lastRxBytes) * 8f) / (1024 * 1024)
            uploadSpeed = ((currentTxBytes - lastTxBytes) * 8f) / (1024 * 1024)
            lastRxBytes = currentRxBytes
            lastTxBytes = currentTxBytes
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            Text("FIELD OPERATIONS", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
        }

        item(span = { GridItemSpan(2) }) {
            GlassCard {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("LIVE NETWORK TRAFFIC", color = Color.Cyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Download, null, tint = Color.Green, modifier = Modifier.size(16.dp))
                            Text(" ${String.format(Locale.US, "%.1f", downloadSpeed)} Mbps", color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(16.dp))
                            Icon(Icons.Default.Upload, null, tint = Color.Yellow, modifier = Modifier.size(16.dp))
                            Text(" ${String.format(Locale.US, "%.1f", uploadSpeed)} Mbps", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Box(Modifier.size(8.dp).clip(CircleShape).background(Color.Green))
                }
            }
        }

        item { StatCard("Repair Logs", "$logs", Icons.Default.Assignment, Color.Cyan) { onNavigate("logs") } }
        item { StatCard("Active Units", "$deploys", Icons.Default.Computer, Color.Magenta) { onNavigate("deploy") } }

        item(span = { GridItemSpan(2) }) {
            Text("QUICK TOOLS", color = Color.White.copy(0.6f), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
        }

        item { ToolButton("Speed Test", Icons.Default.Speed) { onNavigate("network?tab=0") } }
        item { ToolButton("Vicinity Radar", Icons.Default.Radar) { onNavigate("radar") } }
        item { ToolButton("IP Calc", Icons.Default.Calculate) { onNavigate("network?tab=1") } }
        item { ToolButton("Ping Tools", Icons.Default.SettingsEthernet) { onNavigate("network?tab=2") } }
        item { ToolButton("Knowledge", Icons.Default.MenuBook) { onNavigate("kb") } }
        item { ToolButton("Inventory", Icons.Default.Inventory) { onNavigate("deploy") } }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, accentColor: Color, onClick: () -> Unit) {
    Surface(onClick = onClick, color = Color.Transparent, modifier = Modifier.fillMaxWidth()) {
        GlassCard {
            Icon(icon, null, tint = accentColor, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(12.dp))
            Text(value, fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text(label, fontSize = 12.sp, color = Color.White.copy(0.6f), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ToolButton(name: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(onClick = onClick, color = Color.White.copy(0.05f), shape = RoundedCornerShape(16.dp), modifier = Modifier.height(100.dp)) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(8.dp))
            Text(name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VicinityRadarScreen() {
    val context = LocalContext.current
    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val permissionsState = rememberMultiplePermissionsState(listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))

    if (permissionsState.allPermissionsGranted) {
        RadarContent(wifiManager)
    } else {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Location Permission Required", color = Color.White)
                Spacer(Modifier.height(16.dp))
                Button(onClick = { permissionsState.launchMultiplePermissionRequest() }, colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan)) {
                    Text("Grant Permission", color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun RadarContent(wifiManager: WifiManager) {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val radius by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(2500, easing = LinearEasing), repeatMode = RepeatMode.Restart), label = "radius"
    )

    var wifiList by remember { mutableStateOf<List<android.net.wifi.ScanResult>>(emptyList()) }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context, intent: Intent) {
                @Suppress("DEPRECATION")
                wifiList = wifiManager.scanResults.sortedByDescending { it.level }
            }
        }
        context.registerReceiver(receiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        @Suppress("DEPRECATION")
        wifiManager.startScan()
        onDispose { context.unregisterReceiver(receiver) }
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            @Suppress("DEPRECATION")
            wifiManager.startScan()
            delay(8000) // Trigger new scan every 8s (respecting Android throttling)
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("VICINITY RADAR", color = Color.Cyan, fontSize = 20.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
        Spacer(Modifier.height(24.dp))
        Box(Modifier.size(240.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(color = Color.Cyan.copy(alpha = 0.1f), style = Stroke(width = 1.dp.toPx()))
                drawCircle(color = Color.Cyan.copy(alpha = 0.1f), radius = size.minDimension / 4, style = Stroke(width = 1.dp.toPx()))
                drawCircle(color = Color.Cyan.copy(alpha = 0.1f), radius = size.minDimension / 2.5f, style = Stroke(width = 1.dp.toPx()))
                drawCircle(color = Color.Cyan.copy(alpha = 0.4f * (1f - radius)), radius = size.minDimension / 2 * radius, style = Stroke(width = 3.dp.toPx()))
            }
            Icon(Icons.Default.Radar, null, tint = Color.Cyan, modifier = Modifier.size(40.dp))
        }
        Spacer(Modifier.height(24.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(1), modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(wifiList) { result ->
                WifiRadarCard(result)
            }
        }
    }
}

@Composable
fun WifiRadarCard(result: android.net.wifi.ScanResult) {
    var expanded by remember { mutableStateOf(false) }
    @Suppress("DEPRECATION")
    GlassCard(modifier = Modifier.clickable { expanded = !expanded }) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Wifi, null, tint = if (result.level > -60) Color.Green else Color.Yellow)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(result.SSID.ifEmpty { "Hidden Network" }, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(result.BSSID, color = Color.White.copy(0.5f), fontSize = 10.sp)
                }
                Text("${result.level} dBm", color = Color.Cyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            if (expanded) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = Color.White.copy(0.1f))
                Spacer(Modifier.height(8.dp))
                DetailRow("Frequency", "${result.frequency} MHz")
                DetailRow("Channel", "${getChannel(result.frequency)}")
                DetailRow("Capabilities", result.capabilities)
                val estSpeed = when {
                    result.capabilities.contains("ax") -> "1200+ Mbps (Wi-Fi 6)"
                    result.capabilities.contains("ac") -> "866 Mbps (Wi-Fi 5)"
                    else -> "150-300 Mbps"
                }
                DetailRow("Max Capacity", estSpeed)
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.White.copy(0.5f), fontSize = 11.sp)
        Text(value, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

fun getChannel(freq: Int): Int = if (freq >= 2412 && freq <= 2484) (freq - 2412) / 5 + 1 else if (freq >= 5170 && freq <= 5825) (freq - 5170) / 5 + 34 else 0

@Preview
@Composable
fun DashboardContentPreview() { ICTAPPTheme { DashboardContent(logs = 12, deploys = 45) } }
