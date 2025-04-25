package com.example.gigameter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gigameter.fragments.SettingsFragment

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings) // Create this layout next
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_container, SettingsFragment()) // Use the container ID
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Optional: Add back button
    }

    // Optional: Handle back button press in action bar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
} 