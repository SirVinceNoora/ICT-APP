package com.example.ictapp.speedtest

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {
    companion object {
        val SHOW_WELCOME = booleanPreferencesKey("show_welcome")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
    }

    val showWelcome: Flow<Boolean> = context.dataStore.data.map { it[SHOW_WELCOME] ?: true }
    val soundEnabled: Flow<Boolean> = context.dataStore.data.map { it[SOUND_ENABLED] ?: true }
    val hapticFeedback: Flow<Boolean> = context.dataStore.data.map { it[HAPTIC_FEEDBACK] ?: true }

    suspend fun setShowWelcome(show: Boolean) {
        context.dataStore.edit { it[SHOW_WELCOME] = show }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[SOUND_ENABLED] = enabled }
    }

    suspend fun setHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { it[HAPTIC_FEEDBACK] = enabled }
    }
}
