package com.example.gigameter.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gigameter.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.util.ArrayList // Use Java's ArrayList for MPAndroidChart Entries
import java.util.Locale
import java.util.Date // Needed if using Date, otherwise remove
import kotlin.random.Random

class ElectricityFragment : Fragment() {

    private lateinit var currentUsageTextView: TextView
    private lateinit var currentTimeTextView: TextView
    private lateinit var averageUsageTextView: TextView
    private lateinit var usageChart: LineChart

    private val usageUnit: String = "kWh"
    private val daysToShow = 7

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_electricity, container, false)
        initializeViews(view)
        setupChart()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadDummyData()
    }

    private fun initializeViews(view: View) {
        currentUsageTextView = view.findViewById(R.id.currentUsage)
        currentTimeTextView = view.findViewById(R.id.currentTime)
        averageUsageTextView = view.findViewById(R.id.averageUsage)
        usageChart = view.findViewById(R.id.usageChart)
    }

    private fun setupChart() {
        usageChart.description.isEnabled = false
        usageChart.setTouchEnabled(true)
        usageChart.isDragEnabled = true
        usageChart.setScaleEnabled(true)
        usageChart.setPinchZoom(true)
        usageChart.legend.isEnabled = false

        val xAxis = usageChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = daysToShow
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                // Display Day number (1-based index)
                return "Day ${value.toInt() + 1}"
            }
        }

        usageChart.axisRight.isEnabled = false
        usageChart.axisLeft.setDrawGridLines(true)
        usageChart.axisLeft.axisMinimum = 0f // Start Y-axis at 0

        usageChart.data = LineData() // Initialize with empty data
        usageChart.invalidate()
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

        if (dummyEntries.isEmpty()) return

        val dataSet = LineDataSet(dummyEntries, "Daily Electricity Usage")
        setupDataSetStyle(dataSet)

        val lineData = LineData(dataSet)
        usageChart.data = lineData
        usageChart.invalidate() // Refresh chart

        // Update TextViews
        val latestUsage = dummyEntries.last().y
        val averageUsage = dummyEntries.map { it.y }.average().toFloat()

        currentUsageTextView.text = String.format(Locale.US, "%.1f %s", latestUsage, usageUnit)
        currentTimeTextView.text = "Last 7 Days"
        averageUsageTextView.text = String.format(Locale.US, "%.1f %s", averageUsage, usageUnit)

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