package com.example.gigameter.fragments

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.example.gigameter.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

abstract class BaseUtilityFragment(@LayoutRes private val layoutResId: Int) : Fragment() {

    protected lateinit var currentUsageTextView: TextView
    protected lateinit var currentTimeTextView: TextView
    protected lateinit var averageUsageTextView: TextView
    protected lateinit var usageChart: LineChart

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateRunnable: Runnable
    private val historicalData = ArrayList<Entry>()
    private var timeOffset = 0L
    private val maxDataPoints = 10

    protected abstract val usageUnit: String
    protected abstract fun generateRandomUsage(): Float
    protected abstract fun getLineColor(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(layoutResId, container, false)
        initializeViews(view)
        setupChart()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startSimulatedUpdates()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopSimulatedUpdates()
    }

    protected open fun initializeViews(view: View) {
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
        xAxis.valueFormatter = object : ValueFormatter() {
            private val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            override fun getFormattedValue(value: Float): String {
                return format.format(Date(System.currentTimeMillis() - (maxDataPoints - 1 - value.toLong()) * 3000))
            }
        }

        usageChart.axisRight.isEnabled = false
        usageChart.axisLeft.setDrawGridLines(true)
        usageChart.axisLeft.axisMinimum = 0f // Y-axis at 0

        val dataSet = LineDataSet(ArrayList<Entry>(), "Usage")
        setupDataSetStyle(dataSet)

        val lineData = LineData(dataSet)
        usageChart.data = lineData
        usageChart.invalidate() // Refresh
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
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
    }


    private fun updateChart(newUsage: Float) {
        val data = usageChart.data
        if (data != null) {
            var set = data.getDataSetByIndex(0) as LineDataSet?

            if (set == null) {
                set = LineDataSet(null, "Usage")
                setupDataSetStyle(set)
                data.addDataSet(set)
            }

            val entry = Entry(timeOffset.toFloat(), newUsage)
            data.addEntry(entry, 0)
            historicalData.add(entry)

            if (set.entryCount > maxDataPoints) {
                val oldestEntry = set.getEntryForIndex(0)
                data.removeEntry(oldestEntry, 0)
                if (historicalData.isNotEmpty()) historicalData.removeAt(0)
            }

            for (i in 0 until set.entryCount) {
                 set.getEntryForIndex(i).x = i.toFloat()
            }


            data.notifyDataChanged()
            usageChart.notifyDataSetChanged()
            usageChart.invalidate()
            timeOffset++
        }
    }

    private fun calculateAverageUsage(): Float {
        if (historicalData.isEmpty()) return 0f
        return historicalData.map { it.y }.average().toFloat()
    }


    private fun startSimulatedUpdates() {
        updateRunnable = object : Runnable {
            override fun run() {
                val newUsage = generateRandomUsage()
                val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val currentTime = timeFormat.format(Date())

                currentUsageTextView.text = String.format(Locale.US, "%.1f %s", newUsage, usageUnit)
                currentTimeTextView.text = currentTime

                updateChart(newUsage)

                val avgUsage = calculateAverageUsage()
                averageUsageTextView.text = String.format(Locale.US, "%.1f %s", avgUsage, usageUnit)


                handler.postDelayed(this, 3000) // Update every 3s
            }
        }
        handler.post(updateRunnable)
    }

    private fun stopSimulatedUpdates() {
        handler.removeCallbacks(updateRunnable)
    }
} 