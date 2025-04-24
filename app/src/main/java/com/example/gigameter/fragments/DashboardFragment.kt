package com.example.gigameter.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.gigameter.R
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {
    private lateinit var electricityUsage: TextView
    private lateinit var electricityTime: TextView
    private lateinit var electricityProgress: ProgressBar
    private lateinit var electricityProgressText: TextView
    private lateinit var waterUsage: TextView
    private lateinit var waterTime: TextView
    private lateinit var waterProgress: ProgressBar
    private lateinit var waterProgressText: TextView
    private lateinit var gasUsage: TextView
    private lateinit var gasTime: TextView
    private lateinit var gasProgress: ProgressBar
    private lateinit var gasProgressText: TextView

    // Sample daily limits (in real app, these would come from settings or API)
    private val electricityDailyLimit = 50.0 // kWh
    private val waterDailyLimit = 200.0 // L
    private val gasDailyLimit = 5.0 // m³

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        electricityUsage = view.findViewById(R.id.electricityUsage)
        electricityTime = view.findViewById(R.id.electricityTime)
        electricityProgress = view.findViewById(R.id.electricityProgress)
        electricityProgressText = view.findViewById(R.id.electricityProgressText)
        waterUsage = view.findViewById(R.id.waterUsage)
        waterTime = view.findViewById(R.id.waterTime)
        waterProgress = view.findViewById(R.id.waterProgress)
        waterProgressText = view.findViewById(R.id.waterProgressText)
        gasUsage = view.findViewById(R.id.gasUsage)
        gasTime = view.findViewById(R.id.gasTime)
        gasProgress = view.findViewById(R.id.gasProgress)
        gasProgressText = view.findViewById(R.id.gasProgressText)

        // Set up click listeners for cards
        view.findViewById<CardView>(R.id.electricityCard).setOnClickListener {
            // TODO: Navigate to electricity detail screen
        }

        view.findViewById<CardView>(R.id.waterCard).setOnClickListener {
            // TODO: Navigate to water detail screen
        }

        view.findViewById<CardView>(R.id.gasCard).setOnClickListener {
            // TODO: Navigate to gas detail screen
        }

        // Initialize with sample data
        updateUtilityData()

        // Simulate real-time updates (in a real app, this would come from a service or API)
        startSimulatedUpdates()
    }

    private fun updateUtilityData() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = timeFormat.format(Date())

        // Sample data - in a real app, this would come from a data source
        val electricityUsageValue = 15.5
        val waterUsageValue = 120.0
        val gasUsageValue = 2.3

        // Update electricity data
        electricityUsage.text = "$electricityUsageValue kWh"
        electricityTime.text = "Last updated: $currentTime"
        val electricityProgressValue = (electricityUsageValue / electricityDailyLimit * 100).toInt()
        electricityProgress.progress = electricityProgressValue
        electricityProgressText.text = "$electricityProgressValue% of daily limit"

        // Update water data
        waterUsage.text = "$waterUsageValue L"
        waterTime.text = "Last updated: $currentTime"
        val waterProgressValue = (waterUsageValue / waterDailyLimit * 100).toInt()
        waterProgress.progress = waterProgressValue
        waterProgressText.text = "$waterProgressValue% of daily limit"

        // Update gas data
        gasUsage.text = "$gasUsageValue m³"
        gasTime.text = "Last updated: $currentTime"
        val gasProgressValue = (gasUsageValue / gasDailyLimit * 100).toInt()
        gasProgress.progress = gasProgressValue
        gasProgressText.text = "$gasProgressValue% of daily limit"
    }

    private fun startSimulatedUpdates() {
        // In a real app, this would be replaced with actual data updates
        // This is just for demonstration purposes
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        val updateRunnable = object : Runnable {
            override fun run() {
                updateUtilityData()
                handler.postDelayed(this, 5000) // Update every 5 seconds
            }
        }
        handler.post(updateRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up the handler to prevent memory leaks
        android.os.Handler(android.os.Looper.getMainLooper()).removeCallbacksAndMessages(null)
    }
} 