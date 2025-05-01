package com.example.gigameter

import android.os.Bundle
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

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_container, SettingsFragment()) // Use the container ID
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Optional: Add back button
    }

    private fun setupThemeSwitch() {
        // Set initial state
        binding.themeSwitch.isChecked = themeManager.isDarkMode()

        // Handle theme changes
        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            themeManager.setDarkMode(isChecked)
        }

        // Make the entire card clickable
        binding.themeCard.setOnClickListener {
            binding.themeSwitch.toggle()
        }
    }

    // Optional: Handle back button press in action bar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
} 