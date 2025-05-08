package com.example.gigameter.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
            setNoDataText("")
        }
    }

    private fun loadData() {
        binding.progressBar.isVisible = true
        binding.chart.isVisible = false
        binding.emptyChartText.isVisible = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                Log.d("ElectricityFragment", "Fetching utility data...")
                val utilityData = repository.getUtilityData("electricity")
                Log.d("ElectricityFragment", "Received data: $utilityData")
                
                if (utilityData.usageHistory.isEmpty()) {
                    Log.d("ElectricityFragment", "Usage history is empty")
                    binding.progressBar.isVisible = false
                    binding.chart.isVisible = false
                    binding.emptyChartText.isVisible = true
                    return@launch
                }
                
                updateUI(utilityData)
            } catch (e: Exception) {
                Log.e("ElectricityFragment", "Error loading data", e)
                binding.progressBar.isVisible = false
                binding.emptyChartText.isVisible = true
                binding.emptyChartText.text = "Error loading data."
                Toast.makeText(context, "Error loading data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(utilityData: UtilityData) {
        Log.d("ElectricityFragment", "Updating UI with data: $utilityData")
        

        binding.dailyLimitText.text = "Daily Limit: ${utilityData.dailyLimit} kWh"


        val currentUsage = utilityData.usageHistory.lastOrNull() ?: 0
        binding.currentUsageText.text = "Current Usage: $currentUsage kWh"


        if (utilityData.usageHistory.isEmpty()) {
            binding.chart.isVisible = false
            binding.emptyChartText.isVisible = true
            binding.chart.clear()
        } else {
            binding.chart.isVisible = true
            binding.emptyChartText.isVisible = false

            val entries = utilityData.usageHistory.mapIndexed { index, value ->
                Entry(index.toFloat(), value.toFloat())
            }

            val dataSet = LineDataSet(entries, "Electricity Usage (kWh)").apply {
                setDrawFilled(true)
                setDrawValues(false)
                mode = LineDataSet.Mode.CUBIC_BEZIER
            }

            binding.chart.data = LineData(dataSet)
            binding.chart.invalidate()
        }
        
        binding.progressBar.isVisible = false
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
        for (i in 0 until daysToShow) { // Generate random 7 days
            val usage = Random.nextFloat() * 40f + 10f // Random 10.0 to 50.0
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
        binding.chart.invalidate()

        val latestUsage = dummyEntries.last().y
        val averageUsage = dummyEntries.map { it.y }.average().toFloat()

        binding.currentUsageText.text = String.format(Locale.US, "Current Usage: %.1f %s", latestUsage, usageUnit)
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
        dataSet.setDrawValues(false) // ensures no value at point
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
    }
} 