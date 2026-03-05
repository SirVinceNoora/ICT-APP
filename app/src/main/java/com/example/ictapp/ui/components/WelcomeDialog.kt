package com.example.ictapp.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun WelcomeDialog(onDismiss: () -> Unit, onDontShowAgain: () -> Unit) {
    var showToolDetails by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(28.dp))
                .background(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.05f))))
                .padding(1.dp)
                .background(Color(0xFF001F54).copy(alpha = 0.95f), RoundedCornerShape(28.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "WELCOME TO ICT FIELD OPS",
                    color = Color.Cyan,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(Modifier.height(16.dp))
                
                Text(
                    text = "Your mission-critical companion for field engineering and technical troubleshooting.",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(32.dp))

                Text(
                    text = "CORE OPERATIONS",
                    color = Color.Cyan.copy(0.6f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.height(12.dp))

                GuideItem(Icons.Default.Dashboard, "Dashboard", "Monitor live network throughput and access tools.")
                GuideItem(Icons.Default.Radar, "Vicinity Radar", "Scan for active Wi-Fi networks in real-time.")
                GuideItem(Icons.Default.MenuBook, "Knowledge Base", "In-depth troubleshooting and video guides.")
                GuideItem(Icons.Default.Assignment, "Repair Tracker", "Log and update hardware repairs.")

                Spacer(Modifier.height(32.dp))

                Text(
                    text = "NETWORK TOOLS MANUAL",
                    color = Color.Cyan.copy(0.6f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.height(12.dp))

                ToolManualItem(
                    Icons.Default.Speed, 
                    "Professional Speed Test", 
                    "Measures Latency (Ping), Download, and Upload speeds. Use this to verify ISP bandwidth delivery and detect network congestion.",
                    expanded = showToolDetails == "speed",
                    onClick = { showToolDetails = if (showToolDetails == "speed") null else "speed" }
                )
                
                ToolManualItem(
                    Icons.Default.Calculate, 
                    "IP Calculator", 
                    "Enter a Host IP to automatically calculate the Gateway, Broadcast address, and Subnet Mask. Vital for configuring static IPs on servers, printers, and network equipment.",
                    expanded = showToolDetails == "calc",
                    onClick = { showToolDetails = if (showToolDetails == "calc") null else "calc" }
                )

                ToolManualItem(
                    Icons.Default.Terminal, 
                    "Network Diagnostics (Ping)", 
                    "Test connectivity to the Local Gateway or External DNS. Helps identify if a connection issue is internal (router/switch) or external (ISP/Internet).",
                    expanded = showToolDetails == "ping",
                    onClick = { showToolDetails = if (showToolDetails == "ping") null else "ping" }
                )

                ToolManualItem(
                    Icons.Default.Wifi, 
                    "Wi-Fi Analyzer", 
                    "Scan nearby access points, see signal strength (RSSI), and identify channel interference to optimize router placement.",
                    expanded = showToolDetails == "wifi",
                    onClick = { showToolDetails = if (showToolDetails == "wifi") null else "wifi" }
                )

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan, contentColor = Color.Black),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("START OPERATIONS", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = onDontShowAgain) {
                        Text(
                            "DON'T SHOW THIS AGAIN",
                            color = Color.Cyan.copy(alpha = 0.6f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GuideItem(icon: ImageVector, title: String, desc: String) {
    Row(modifier = Modifier.padding(vertical = 12.dp), verticalAlignment = Alignment.Top) {
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.Cyan.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = Color.Cyan, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(desc, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, lineHeight = 18.sp)
        }
    }
}

@Composable
fun ToolManualItem(icon: ImageVector, title: String, manual: String, expanded: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (expanded) Color.Cyan.copy(0.05f) else Color.Transparent)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = if (expanded) Color.Cyan else Color.White.copy(0.6f), modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text(
                title, 
                color = if (expanded) Color.Cyan else Color.White, 
                fontWeight = FontWeight.Bold, 
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, 
                null, 
                tint = Color.White.copy(0.3f)
            )
        }
        AnimatedVisibility(visible = expanded) {
            Column {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = manual,
                    color = Color.White.copy(0.7f),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
