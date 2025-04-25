package com.example.gigameter.workers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.gigameter.R // Assuming you have a basic notification icon
import com.example.gigameter.repository.UtilityRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeeklyReportWorker(appContext: Context, workerParams: WorkerParameters):
    CoroutineWorker(appContext, workerParams) {

    private val repository = UtilityRepository()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        const val WORK_NAME = "WeeklyReportWorker"
        const val NOTIFICATION_CHANNEL_ID = "WeeklyReportChannel"
        const val NOTIFICATION_ID = 1001 // Unique ID for the report notification
    }

    override suspend fun doWork(): Result {
        // Run in IO dispatcher for network/db operations
        return withContext(Dispatchers.IO) {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                // Cannot generate report if user is not logged in
                return@withContext Result.failure()
            }

            try {
                // Create notification for foreground service
                val notification = createNotification("Generating weekly report...")
                val foregroundInfo = ForegroundInfo(NOTIFICATION_ID, notification)
                setForeground(foregroundInfo)

                // Fetch data (modify this logic based on your exact report needs)
                val utilityData = repository.getUtilityData("electricity")
                val weeklyUsage = utilityData.usageHistory.takeLast(7).sum()

                // Create and show the summary notification
                val summaryText = "Your total electricity usage for the last 7 days was $weeklyUsage kWh."
                showSummaryNotification(summaryText)

                Result.success()
            } catch (e: Exception) {
                // Handle errors, maybe retry later
                Result.retry() // Or Result.failure()
            }
        }
    }

    private fun createNotification(contentText: String): android.app.Notification {
        // Create channel if needed (should be done elsewhere ideally, but added here for completeness)
        createNotificationChannel()

        return NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("GigaMeter Weekly Report")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your actual icon
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun showSummaryNotification(summaryText: String) {
        val notification = createNotification(summaryText)

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
             // Permission is required to post notifications on Android 13+
            // The worker might fail silently here if permission is not granted.
            // Proper permission handling should be implemented in the UI.
            return
        }

        NotificationManagerCompat.from(applicationContext).notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Weekly Reports"
            val descriptionText = "Channel for GigaMeter weekly usage reports"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
} 