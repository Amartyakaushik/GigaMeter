package com.example.gigameter

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.gigameter.databinding.ActivitySettingsBinding
import com.example.gigameter.fragments.SettingsFragment
import com.example.gigameter.utils.ThemeManager

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        themeManager = ThemeManager(this)
        setupThemeSwitch()
        setupNotificationToggles()
        setupDataManagement()

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_container, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupThemeSwitch() {
        binding.themeSwitch.isChecked = themeManager.isDarkMode()

        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            themeManager.setDarkMode(isChecked)
        }

        binding.themeCard.setOnClickListener {
            binding.themeSwitch.toggle()
        }
    }

    private fun setupNotificationToggles() {
        binding.usageAlertSwitch.isChecked = getPreferences(MODE_PRIVATE)
            .getBoolean("usage_alerts", true)
        
        binding.usageAlertSwitch.setOnCheckedChangeListener { _, isChecked ->
            getPreferences(MODE_PRIVATE).edit()
                .putBoolean("usage_alerts", isChecked)
                .apply()
            
            Toast.makeText(
                this,
                if (isChecked) "Usage alerts enabled" else "Usage alerts disabled",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Weekly Reports
        binding.reportSwitch.isChecked = getPreferences(MODE_PRIVATE)
            .getBoolean("weekly_reports", false)
        
        binding.reportSwitch.setOnCheckedChangeListener { _, isChecked ->
            getPreferences(MODE_PRIVATE).edit()
                .putBoolean("weekly_reports", isChecked)
                .apply()
            
            Toast.makeText(
                this,
                if (isChecked) "Weekly reports enabled" else "Weekly reports disabled",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupDataManagement() {
        // Export Data
        binding.exportDataButton.setOnClickListener {
            Toast.makeText(this, "Exporting data...", Toast.LENGTH_SHORT).show()
            // TODO: Implement data export functionality
        }

        // Clear Data
        binding.clearDataButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Clear All Data")
                .setMessage("Are you sure you want to clear all your usage data? This action cannot be undone.")
                .setPositiveButton("Clear") { _, _ ->
                    // TODO: Implement data clearing functionality
                    Toast.makeText(this, "All data cleared", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
} 