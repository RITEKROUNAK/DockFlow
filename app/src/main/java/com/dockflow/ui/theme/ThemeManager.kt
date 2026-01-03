package com.dockflow.ui.theme

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Available theme types
 */
enum class ThemeType {
    NEON,
    MINIMAL,
    ANALOG
}

/**
 * Extension property for DataStore
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Manages theme preferences and persistence
 */
class ThemeManager(private val context: Context) {
    
    private val THEME_KEY = stringPreferencesKey("selected_theme")
    
    var currentTheme by mutableStateOf(ThemeType.NEON)
        private set
    
    /**
     * Flow of the current theme
     */
    val themeFlow: Flow<ThemeType> = context.dataStore.data.map { preferences ->
        val themeName = preferences[THEME_KEY] ?: ThemeType.NEON.name
        try {
            ThemeType.valueOf(themeName)
        } catch (e: IllegalArgumentException) {
            ThemeType.NEON
        }
    }
    
    /**
     * Switch to the next theme in the cycle
     */
    suspend fun cycleTheme() {
        val nextTheme = when (currentTheme) {
            ThemeType.NEON -> ThemeType.MINIMAL
            ThemeType.MINIMAL -> ThemeType.ANALOG
            ThemeType.ANALOG -> ThemeType.NEON
        }
        setTheme(nextTheme)
    }
    
    /**
     * Set a specific theme
     */
    suspend fun setTheme(theme: ThemeType) {
        currentTheme = theme
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }
    
    /**
     * Load the saved theme
     */
    suspend fun loadTheme() {
        themeFlow.collect { theme ->
            currentTheme = theme
        }
    }
}
