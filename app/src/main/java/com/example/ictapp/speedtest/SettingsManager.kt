package com.example.ictapp.speedtest

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {
    companion object {
        val SHOW_WELCOME = booleanPreferencesKey("show_welcome")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val ACCENT_COLOR = intPreferencesKey("accent_color")
        val TEXT_COLOR = intPreferencesKey("text_color")
        val TECHNICIAN_NAME = stringPreferencesKey("technician_name")
        val AUTO_SCAN_WIFI = booleanPreferencesKey("auto_scan_wifi")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val showWelcome: Flow<Boolean> = context.dataStore.data.map { it[SHOW_WELCOME] ?: true }
    val soundEnabled: Flow<Boolean> = context.dataStore.data.map { it[SOUND_ENABLED] ?: true }
    val hapticFeedback: Flow<Boolean> = context.dataStore.data.map { it[HAPTIC_FEEDBACK] ?: true }
    val accentColor: Flow<Int> = context.dataStore.data.map { it[ACCENT_COLOR] ?: 0xFF00FFFF.toInt() } // Default Cyan
    val textColor: Flow<Int> = context.dataStore.data.map { it[TEXT_COLOR] ?: 0xFFFFFFFF.toInt() } // Default White
    val technicianName: Flow<String> = context.dataStore.data.map { it[TECHNICIAN_NAME] ?: "V1NC3" }
    val autoScanWifi: Flow<Boolean> = context.dataStore.data.map { it[AUTO_SCAN_WIFI] ?: false }
    val darkMode: Flow<Boolean> = context.dataStore.data.map { it[DARK_MODE] ?: true }

    suspend fun setShowWelcome(show: Boolean) {
        context.dataStore.edit { it[SHOW_WELCOME] = show }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[SOUND_ENABLED] = enabled }
    }

    suspend fun setHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { it[HAPTIC_FEEDBACK] = enabled }
    }

    suspend fun setAccentColor(color: Int) {
        context.dataStore.edit { it[ACCENT_COLOR] = color }
    }

    suspend fun setTextColor(color: Int) {
        context.dataStore.edit { it[TEXT_COLOR] = color }
    }

    suspend fun setTechnicianName(name: String) {
        context.dataStore.edit { it[TECHNICIAN_NAME] = name }
    }

    suspend fun setAutoScanWifi(enabled: Boolean) {
        context.dataStore.edit { it[AUTO_SCAN_WIFI] = enabled }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[DARK_MODE] = enabled }
    }
}
