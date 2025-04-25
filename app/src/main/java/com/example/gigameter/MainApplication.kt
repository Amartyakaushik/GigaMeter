package com.example.gigameter

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.*
import com.example.gigameter.workers.WeeklyReportWorker
import java.util.concurrent.TimeUnit

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        scheduleWeeklyReportWorker()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Weekly Reports"
            val descriptionText = "Channel for GigaMeter weekly usage reports"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(WeeklyReportWorker.NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scheduleWeeklyReportWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Only run when connected
            .build()

        val repeatingRequest = PeriodicWorkRequestBuilder<WeeklyReportWorker>(
            repeatInterval = 7, // Repeat interval
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setConstraints(constraints)
            // .setInitialDelay(1, TimeUnit.MINUTES) // Optional: Delay first run
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            WeeklyReportWorker.WORK_NAME, // Unique name for the work
            ExistingPeriodicWorkPolicy.KEEP, // Keep existing work if it's already scheduled
            repeatingRequest
        )
    }
} 