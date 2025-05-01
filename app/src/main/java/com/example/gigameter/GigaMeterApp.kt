package com.example.gigameter

import android.app.Application
import com.example.gigameter.utils.ThemeManager

class GigaMeterApp : Application() {
    private lateinit var themeManager: ThemeManager

    override fun onCreate() {
        super.onCreate()
        
        // Initialize theme
        themeManager = ThemeManager(this)
        themeManager.applyTheme(themeManager.isDarkMode())
    }
} 