package com.example.gigameter.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.gigameter.R
import com.example.gigameter.databinding.FragmentElectricityBinding
import com.example.gigameter.models.UtilityData
import com.example.gigameter.repository.UtilityRepository
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

class ElectricityFragment : Fragment() {
    private var _binding: FragmentElectricityBinding? = null
    private val binding get() = _binding!!
    private val repository = UtilityRepository()

    private val usageUnit: String = "kWh"
    private val daysToShow = 7

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentElectricityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChart()
        loadData()
    }

    private fun setupChart() {
        binding.chart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            axisRight.isEnabled = false
            xAxis.setDrawGridLines(false)
            // Clear default text
            setNoDataText("")
        }
    }

    private fun loadData() {
        // Show progress bar, hide chart and empty text initially
        binding.progressBar.isVisible = true
        binding.chart.isVisible = false
        binding.emptyChartText.isVisible = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val utilityData = repository.getUtilityData("electricity")
                updateUI(utilityData)
            } catch (e: Exception) {
                binding.progressBar.isVisible = false // Hide progress bar on error
                binding.emptyChartText.isVisible = true // Optionally show empty text on error
                binding.emptyChartText.text = "Error loading data."
                Toast.makeText(context, "Error loading data: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                 // Ensure progress bar is hidden after loading attempt (success or failure)
                 binding.progressBar.isVisible = false
            }
        }
    }

    private fun updateUI(utilityData: UtilityData) {
        // Update daily limit
        binding.dailyLimitText.text = "Daily Limit: ${utilityData.dailyLimit} kWh"

        // Update current usage
        val currentUsage = utilityData.usageHistory.lastOrNull() ?: 0
        binding.currentUsageText.text = "Current Usage: $currentUsage kWh"

        // Update chart visibility and content
        if (utilityData.usageHistory.isEmpty()) {
            binding.chart.isVisible = false
            binding.emptyChartText.isVisible = true
            binding.emptyChartText.text = "No usage history recorded yet."
            binding.chart.clear() // Clear any potential old data
        } else {
            binding.chart.isVisible = true
            binding.emptyChartText.isVisible = false

            val entries = utilityData.usageHistory.mapIndexed { index, value ->
                Entry(index.toFloat(), value.toFloat())
            }

            val dataSet = LineDataSet(entries, "Electricity Usage (kWh)").apply {
                color = Color.BLUE
                valueTextColor = Color.BLACK
                lineWidth = 2f
                setDrawCircles(true)
                setDrawValues(true)
            }

            binding.chart.data = LineData(dataSet)
            binding.chart.invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getLineColor(): Int {
        return Color.YELLOW
    }

    private fun createDummyData(): List<Entry> {
        val entries = ArrayList<Entry>()
        // Generate random usage for the last 7 days (e.g., 10 to 50 kWh)
        for (i in 0 until daysToShow) {
            val usage = Random.nextFloat() * 40f + 10f // Random value between 10.0 and 50.0
            entries.add(Entry(i.toFloat(), usage))
        }
        return entries
    }

    private fun loadDummyData() {
        val dummyEntries = createDummyData()

        if (dummyEntries.isEmpty()) {
            binding.chart.isVisible = false
            binding.emptyChartText.isVisible = true
            binding.emptyChartText.text = "No dummy data available."
            return
        }

        binding.chart.isVisible = true
        binding.emptyChartText.isVisible = false

        val dataSet = LineDataSet(dummyEntries, "Daily Electricity Usage")
        setupDataSetStyle(dataSet)

        val lineData = LineData(dataSet)
        binding.chart.data = lineData
        binding.chart.invalidate() // Refresh chart

        // Update TextViews
        val latestUsage = dummyEntries.last().y
        val averageUsage = dummyEntries.map { it.y }.average().toFloat()

        binding.currentUsageText.text = String.format(Locale.US, "Current Usage: %.1f %s", latestUsage, usageUnit)
        // This was incorrectly updating daily limit with average dummy usage, let's remove for now
        // binding.dailyLimitText.text = String.format(Locale.US, "Daily Limit: %.1f %s", averageUsage, usageUnit)
    }

    private fun setupDataSetStyle(dataSet: LineDataSet) {
        dataSet.color = getLineColor()
        dataSet.setCircleColor(getLineColor())
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 4f
        dataSet.setDrawCircleHole(false)
        dataSet.valueTextSize = 9f
        dataSet.setDrawFilled(true)
        dataSet.fillColor = getLineColor()
        dataSet.fillAlpha = 30
        dataSet.setDrawValues(false) // Hide values on points
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER // Smoothed line
    }
} 