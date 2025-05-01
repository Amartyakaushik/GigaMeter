package com.example.gigameter.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class ThemeManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(THEME_PREFS, AppCompatActivity.MODE_PRIVATE)

    fun setDarkMode(isDark: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, isDark).apply()
        applyTheme(isDark)
    }

    fun isDarkMode(): Boolean {
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }

    fun applyTheme(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    companion object {
        private const val THEME_PREFS = "theme_prefs"
        private const val KEY_DARK_MODE = "dark_mode"
    }
} 