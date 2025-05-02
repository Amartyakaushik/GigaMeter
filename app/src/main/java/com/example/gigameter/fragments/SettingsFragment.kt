package com.example.gigameter.fragments

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.gigameter.R
import com.example.gigameter.repository.UtilityRepository
import kotlinx.coroutines.launch

class SettingsFragment : PreferenceFragmentCompat() {

    private val repository = UtilityRepository()

    companion object {
        const val KEY_ELECTRICITY_LIMIT = "pref_electricity_limit"
        const val KEY_WATER_LIMIT = "pref_water_limit"
//        const val KEY_NOTIFICATIONS = "pref_notifications_enabled"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // Add listeners for limit changes
        findPreference<EditTextPreference>(KEY_ELECTRICITY_LIMIT)?.setOnPreferenceChangeListener {
            preference, newValue ->
            handleLimitChange("electricity", newValue)
        }

        findPreference<EditTextPreference>(KEY_WATER_LIMIT)?.setOnPreferenceChangeListener {
            preference, newValue ->
            handleLimitChange("water", newValue)
        }

        // Listener for notification toggle (handled automatically by SwitchPreferenceCompat for storage)
        // No extra listener needed unless you want to perform an immediate action.
    }

    private fun handleLimitChange(utilityType: String, newValue: Any?): Boolean {
        val newLimitString = newValue as? String
        if (newLimitString.isNullOrBlank()) {
            Toast.makeText(context, "Limit cannot be empty", Toast.LENGTH_SHORT).show()
            return false // Reject empty value
        }

        return try {
            val newLimit = newLimitString.toInt()
            if (newLimit < 0) {
                Toast.makeText(context, "Limit must be non-negative", Toast.LENGTH_SHORT).show()
                false // Reject negative value
            } else {
                lifecycleScope.launch {
                    try {
                        repository.updateDailyLimit(utilityType, newLimit)
                        Toast.makeText(context, "$utilityType limit updated", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to update limit: ${e.message}", Toast.LENGTH_LONG).show()
                         // Optionally revert preference change here if needed
                    }
                }
                true // Accept the valid change
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(context, "Invalid number format", Toast.LENGTH_SHORT).show()
            false // Reject non-integer value
        }
    }
} 