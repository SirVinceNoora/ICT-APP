package com.example.ictapp.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import com.example.ictapp.ui.components.GlassCard
import com.example.ictapp.ui.theme.ICTAPPTheme

data class KBItem(
    val title: String,
    val brand: String,
    val category: String,
    val detailFix: String,
    val youtubeQuery: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TroubleshootingScreen(onBack: () -> Unit = {}) {
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val kbData = remember {
        listOf(
            KBItem(
                "No Power / Black Screen", "Universal", "Laptop",
                "1. Power Reset: Disconnect AC and all peripherals. Hold power button for 60 seconds. Reconnect only AC and try again.\n" +
                "2. AC Adapter Check: Look for LED on brick. If flickering, replace. Try a different known-good outlet.\n" +
                "3. External Display Test: Connect via HDMI to see if it's just a screen failure. If image appears, LCD is dead.\n" +
                "4. CMOS Reset: If comfortable opening, disconnect the coin-cell battery for 5 mins to clear corrupted BIOS settings.\n" +
                "5. RAM Isolation: Remove all RAM sticks. Try booting with one stick at a time in different slots.",
                "laptop no power troubleshooting fix",
                Icons.Default.Laptop
            ),
            KBItem(
                "Spooler Service Failure", "HP/Universal", "Printer",
                "1. Service Check: Press Win+R, type 'services.msc'. Find 'Print Spooler'. Stop the service.\n" +
                "2. Clear Queue: Go to C:\\Windows\\System32\\spool\\PRINTERS and delete all .shd and .spl files.\n" +
                "3. Restart Service: Go back to Services and Start the Print Spooler. Set Startup Type to 'Automatic'.\n" +
                "4. Dependency Check: Ensure 'Remote Procedure Call (RPC)' is also running.\n" +
                "5. Driver Clean: If error persists, remove printer from Control Panel and reinstall using the HP Universal Print Driver.",
                "fix windows print spooler error",
                Icons.Default.Print
            ),
            KBItem(
                "Intermittent WiFi Drops", "Cisco/TP-Link", "Router",
                "1. Spectrum Analysis: Use a WiFi analyzer to check channel congestion. If on 2.4GHz, switch to Channel 1, 6, or 11.\n" +
                "2. Firmware Update: Log into Gateway (usually 192.168.1.1). Check for updates under System Tools.\n" +
                "3. DHCP Lease: Verify DHCP pool isn't full. Increase lease time to 24 hours.\n" +
                "4. Heat Check: Ensure router isn't in a confined space. Thermal throttling causes radio drops.\n" +
                "5. Factory Reset: Use a pin to hold reset for 30s. Reconfigure from scratch to clear NVRAM corruption.",
                "fix router intermittent wifi connection",
                Icons.Default.Router
            ),
            KBItem(
                "Vertical Lines on Print", "Brother/Epson", "Printer",
                "1. Corona Wire Clean: (Brother) Slide the green tab on the drum unit back and forth several times.\n" +
                "2. Nozzle Check: Run the internal 'Print Quality Check' from the printer LCD menu.\n" +
                "3. Head Cleaning: If nozzles are clogged, run 2 deep cleaning cycles (Warning: uses a lot of ink).\n" +
                "4. Roller Inspection: Check the fuser or exit rollers for toner build-up or physical scratches.\n" +
                "5. Replacement: If cleaning fails, the Drum unit or Print Head may have physical surface damage.",
                "printer vertical lines on page fix",
                Icons.Default.Print
            ),
            KBItem(
                "Blue Screen (BSOD)", "Dell/Universal", "Laptop",
                "1. Error Code ID: Note the 'Stop Code'. Common ones: CRITICAL_PROCESS_DIED or INACCESSIBLE_BOOT_DEVICE.\n" +
                "2. Safe Mode: Boot with Shift+Restart. Go to Troubleshoot > Startup Settings > Restart > 4.\n" +
                "3. SFC Scan: Open CMD as Admin. Type 'sfc /scannow' to fix corrupted system files.\n" +
                "4. CHKDSK: Type 'chkdsk c: /f /r' to find and fix drive sector errors.\n" +
                "5. Hardware Test: Press F12 on boot (Dell) to run SupportAssist hardware diagnostics.",
                "fix windows blue screen of death",
                Icons.Default.Terminal
            )
        )
    }

    val filteredKb = kbData.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
        it.brand.contains(searchQuery, ignoreCase = true) ||
        it.category.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Knowledge Base", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Devices, Brands, or Issues...", color = Color.White.copy(0.6f)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Cyan) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, null, tint = Color.White)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Cyan,
                    unfocusedBorderColor = Color.White.copy(0.3f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(Modifier.height(16.dp))

            // AI / Google Quick Search
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { 
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=${Uri.encode(searchQuery + " troubleshooting")}"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Public, null, tint = Color.Cyan, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Google", fontSize = 12.sp)
                }
                Button(
                    onClick = { 
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chatgpt.com/?q=${Uri.encode("Detailed technical troubleshooting for " + searchQuery)}"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, null, tint = Color.Magenta, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Ask AI", fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

            Text("IN-DEPTH KNOWLEDGE BASE", color = Color.Cyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            
            Spacer(Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredKb) { item ->
                    DetailedKBArticleCard(item)
                }
                if (filteredKb.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Text("No internal articles found. Use AI for custom help.", color = Color.White.copy(0.5f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailedKBArticleCard(item: KBItem) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    GlassCard(modifier = Modifier.clickable { expanded = !expanded }) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)).background(Color.Cyan.copy(0.1f)), contentAlignment = Alignment.Center) {
                    Icon(item.icon, null, tint = Color.Cyan, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(item.title, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    Text("${item.brand} • ${item.category}", color = Color.Cyan.copy(0.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    null,
                    tint = Color.White.copy(0.5f)
                )
            }

            if (expanded) {
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = Color.White.copy(0.1f))
                Spacer(Modifier.height(12.dp))
                
                Text(
                    text = item.detailFix,
                    color = Color.White.copy(0.9f),
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )

                Spacer(Modifier.height(20.dp))
                
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(item.youtubeQuery)}"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(0.8f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PlayCircle, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Watch Video Tutorial", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TroubleshootingScreenPreview() {
    ICTAPPTheme {
        Box(Modifier.background(Color(0xFF001F54))) {
            TroubleshootingScreen()
        }
    }
}
