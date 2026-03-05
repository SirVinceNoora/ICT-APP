package com.example.ictapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ictapp.speedtest.SettingsManager
import com.example.ictapp.ui.components.FieldInput
import com.example.ictapp.ui.components.GlassCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(settingsManager: SettingsManager, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val soundEnabled by settingsManager.soundEnabled.collectAsState(initial = true)
    val hapticFeedback by settingsManager.hapticFeedback.collectAsState(initial = true)
    val showWelcome by settingsManager.showWelcome.collectAsState(initial = true)
    val accentColorInt by settingsManager.accentColor.collectAsState(initial = 0xFF00FFFF.toInt())
    val textColorInt by settingsManager.textColor.collectAsState(initial = 0xFFFFFFFF.toInt())
    val techName by settingsManager.technicianName.collectAsState(initial = "V1NC3")
    val autoScan by settingsManager.autoScanWifi.collectAsState(initial = false)
    val darkMode by settingsManager.darkMode.collectAsState(initial = true)

    val accentColor = Color(accentColorInt)
    val textColor = Color(textColorInt)

    val accentPalette = listOf(
        Color(0xFF00FFFF), Color(0xFF00FF00), Color(0xFFFF00FF), 
        Color(0xFFFFFF00), Color(0xFFFF4500), Color(0xFF8A2BE2), Color(0xFFFFFFFF)
    )
    
    val textPalette = listOf(
        Color(0xFFFFFFFF), Color(0xFFE0E0E0), Color(0xFF00FFFF),
        Color(0xFFFFD700), Color(0xFFAFAFAF), Color(0xFF00FF00)
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            
            Text("THEME CUSTOMIZATION", color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            
            GlassCard {
                Column(Modifier.padding(4.dp)) {
                    Text("App Accent Color", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(accentPalette) { color ->
                            ColorCircle(color, accentColor == color) {
                                scope.launch { settingsManager.setAccentColor(color.toArgb()) }
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(20.dp))
                    
                    Text("UI Text Color", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(textPalette) { color ->
                            ColorCircle(color, textColor == color) {
                                scope.launch { settingsManager.setTextColor(color.toArgb()) }
                            }
                        }
                    }
                }
            }

            Text("USER PROFILE", color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            GlassCard {
                FieldInput(
                    value = techName,
                    onValueChange = { scope.launch { settingsManager.setTechnicianName(it) } },
                    label = "Technician ID"
                )
            }

            Text("SYSTEM PREFERENCES", color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SettingsToggle(Icons.Default.VolumeUp, "Sound Effects", "Audio feedback for actions", soundEnabled, accentColor, textColor) { 
                    scope.launch { settingsManager.setSoundEnabled(it) } 
                }
                SettingsToggle(Icons.Default.Vibration, "Haptic Feedback", "Vibration on interaction", hapticFeedback, accentColor, textColor) { 
                    scope.launch { settingsManager.setHapticFeedback(it) } 
                }
                SettingsToggle(Icons.Default.WifiTethering, "Auto-Scan Vicinity", "Refresh radar automatically", autoScan, accentColor, textColor) { 
                    scope.launch { settingsManager.setAutoScanWifi(it) } 
                }
                SettingsToggle(Icons.Default.DarkMode, "High Contrast Mode", "Optimized dark theme", darkMode, accentColor, textColor) { 
                    scope.launch { settingsManager.setDarkMode(it) } 
                }
                SettingsToggle(Icons.Default.Help, "Show Welcome Guide", "Intro guide on next start", showWelcome, accentColor, textColor) { 
                    scope.launch { settingsManager.setShowWelcome(it) } 
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun ColorCircle(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .border(width = if (isSelected) 3.dp else 0.dp, color = Color.White, shape = CircleShape)
            .clickable(onClick = onClick)
    )
}

@Composable
fun SettingsToggle(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    accentColor: Color,
    textColor: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    GlassCard {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = accentColor, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = textColor, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(subtitle, color = textColor.copy(0.5f), fontSize = 11.sp)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = accentColor,
                    uncheckedThumbColor = Color.White.copy(0.6f),
                    uncheckedTrackColor = Color.White.copy(0.1f)
                )
            )
        }
    }
}
