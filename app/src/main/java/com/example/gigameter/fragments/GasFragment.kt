package com.example.gigameter.fragments

import android.graphics.Color
import com.example.gigameter.R
import kotlin.random.Random

class GasFragment : BaseUtilityFragment(R.layout.fragment_gas) {

    override val usageUnit: String = "m³"

    override fun generateRandomUsage(): Float {
        // Simulate gas usage (e.g., 0.0 to 1.0 m³)
        return Random.nextFloat() * 1.0f
    }

    override fun getLineColor(): Int {
        return Color.parseColor("#FFA500") // Orange
    }
} 