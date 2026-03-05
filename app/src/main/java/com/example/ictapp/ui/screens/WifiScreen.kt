package com.example.ictapp.ui.screens

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ictapp.ui.components.DetailRow
import com.example.ictapp.ui.components.GlassCard
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WifiScreen() {
    val context = LocalContext.current
    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE
        )
    )

    var wifiDetails by remember { mutableStateOf<Map<String, String>?>(null) }

    val networkCallback = object : ConnectivityManager.NetworkCallback() {
        @Suppress("DEPRECATION")
        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                val wifiInfo = wifiManager.connectionInfo
                val linkDown = capabilities.linkDownstreamBandwidthKbps
                val linkUp = capabilities.linkUpstreamBandwidthKbps
                val ipAddress = getIpAddress(wifiInfo.ipAddress)
                
                wifiDetails = mapOf(
                    "SSID" to (wifiInfo.ssid?.replace("\"", "") ?: "Connected"),
                    "BSSID" to wifiInfo.bssid.toString(),
                    "IP Address" to ipAddress,
                    "Link Speed" to "${wifiInfo.linkSpeed} Mbps",
                    "Downstream" to "${linkDown / 1000} Mbps",
                    "Upstream" to "${linkUp / 1000} Mbps",
                    "Signal Strength" to "${wifiInfo.rssi} dBm"
                )
            }
        }

        override fun onLost(network: Network) {
            wifiDetails = null
        }
    }

    DisposableEffect(Unit) {
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        onDispose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

    Column(modifier = Modifier.padding(8.dp)) {
        if (wifiDetails != null) {
            GlassCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Wifi, contentDescription = null, tint = Color.Cyan)
                    Spacer(Modifier.width(12.dp))
                    Text("ACTIVE CONNECTION", color = Color.Cyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
                Spacer(Modifier.height(16.dp))
                
                wifiDetails!!.forEach { (label, value) ->
                    DetailRow(label, value)
                }
            }
        } else {
            GlassCard {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.WifiOff, contentDescription = null, tint = Color.Red.copy(0.6f), modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(16.dp))
                    Text(text = "No Wi-Fi Connection detected.", color = Color.White.copy(0.6f), fontSize = 14.sp)
                }
            }
        }
    }
}

private fun getIpAddress(ip: Int): String {
    return (ip and 0xFF).toString() + "." +
            ((ip shr 8) and 0xFF) + "." +
            ((ip shr 16) and 0xFF) + "." +
            ((ip shr 24) and 0xFF)
}
