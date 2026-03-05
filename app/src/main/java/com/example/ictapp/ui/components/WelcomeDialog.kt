package com.example.ictapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun WelcomeDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
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

                GuideItem(
                    icon = Icons.Default.Dashboard,
                    title = "Dashboard",
                    desc = "Monitor live network throughput (DL/UL) and access critical tools instantly."
                )
                
                GuideItem(
                    icon = Icons.Default.Radar,
                    title = "Vicinity Radar",
                    desc = "Scan your environment for active Wi-Fi networks. Watch signals, channels, and technical details in real-time."
                )

                GuideItem(
                    icon = Icons.Default.MenuBook,
                    title = "Knowledge Base",
                    desc = "In-depth troubleshooting for Laptops, Printers, and Routers with integrated video guides and AI assistance."
                )

                GuideItem(
                    icon = Icons.Default.Assignment,
                    title = "Repair Tracker",
                    desc = "Log and update hardware repairs with detailed technical diagnostics and status tracking."
                )

                GuideItem(
                    icon = Icons.Default.Router,
                    title = "Network Tools",
                    desc = "Professional-grade Speed Test, IP Calculator, and System Ping diagnostics."
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
            }
        }
    }
}

@Composable
fun GuideItem(icon: ImageVector, title: String, desc: String) {
    Row(
        modifier = Modifier.padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Cyan.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Color.Cyan, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(desc, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, lineHeight = 18.sp)
        }
    }
}
