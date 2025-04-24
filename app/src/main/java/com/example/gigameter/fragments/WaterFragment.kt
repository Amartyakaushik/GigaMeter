package com.example.gigameter.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.gigameter.MainActivity
import com.example.gigameter.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class WaterFragment : BaseUtilityFragment(R.layout.fragment_water) {

    override val usageUnit: String = "L"
    private val waterUsageLimit = 7.0f // Lowered limit for easier testing (was 100L)
    private val waterNotificationId = 1 // Unique ID for water notifications
    private var limitCrossed = false // Flag to prevent spamming notifications
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateRunnable: Runnable

    override fun generateRandomUsage(): Float {
        // Simulate water usage (e.g., 0 to 10 Liters per update)
        return Random.nextFloat() * 10
    }

    override fun getLineColor(): Int {
        return Color.BLUE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Override the base simulation start to add notification logic
        startWaterSimulationUpdates()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Ensure the specific handler for this fragment is stopped
        handler.removeCallbacks(updateRunnable)
    }

    private fun startWaterSimulationUpdates() {
        updateRunnable = object : Runnable {
            override fun run() {
                val newUsage = generateRandomUsage()
                val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val currentTime = timeFormat.format(Date())

                // Update UI (Assuming these are handled in BaseUtilityFragment or here)
                currentUsageTextView.text = String.format(Locale.US, "%.1f %s", newUsage, usageUnit)
                currentTimeTextView.text = currentTime
                updateChart(newUsage) // Assuming updateChart is accessible or overridden
                val avgUsage = calculateAverageUsage() // Assuming accessible or overridden
                averageUsageTextView.text = String.format(Locale.US, "%.1f %s", avgUsage, usageUnit)

                // Check for limit crossing
                if (newUsage > waterUsageLimit && !limitCrossed) {
                    sendWaterUsageNotification(newUsage)
                    limitCrossed = true // Set flag after sending notification
                } else if (newUsage <= waterUsageLimit) {
                    limitCrossed = false // Reset flag if usage drops below limit
                }

                handler.postDelayed(this, 3000) // Update every 3 seconds
            }
        }
        handler.post(updateRunnable) // Start the first update
    }

    // Helper method to send the notification
    private fun sendWaterUsageNotification(usage: Float) {
        if (context == null) return // Avoid issues if fragment is detached

        // Check permission before sending
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
           // Optional: Log or inform the user that permission is needed
           return
        }

        val builder = NotificationCompat.Builder(requireContext(), MainActivity.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water) // Use your water icon
            .setContentTitle("Water Usage Alert")
            .setContentText("Water usage exceeded limit: %.1f L".format(usage))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Dismiss notification when tapped

        with(NotificationManagerCompat.from(requireContext())) {
            notify(waterNotificationId, builder.build())
        }
    }

    // --- Need access to these methods from Base or implement here --- 
    // Add dummy implementations or ensure they are accessible from BaseUtilityFragment
    private fun updateChart(usage: Float) {
        // Call super.updateChart(usage) or implement chart update logic here
        // This needs to be properly handled depending on BaseUtilityFragment's visibility
        // For now, assuming BaseUtilityFragment's methods are protected/public
         try {
             val method = BaseUtilityFragment::class.java.getDeclaredMethod("updateChart", Float::class.java)
             method.isAccessible = true
             method.invoke(this, usage)
         } catch (e: Exception) { /* Handle exception if method not found/accessible */ }
    }

    private fun calculateAverageUsage(): Float {
        // Call super.calculateAverageUsage() or implement logic here
         return try {
             val method = BaseUtilityFragment::class.java.getDeclaredMethod("calculateAverageUsage")
             method.isAccessible = true
             method.invoke(this) as Float
         } catch (e: Exception) { 
             0f // Default value
         }
    }
} 