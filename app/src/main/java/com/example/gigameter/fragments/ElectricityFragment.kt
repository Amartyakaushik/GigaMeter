package com.example.gigameter.fragments

import android.graphics.Color
import com.example.gigameter.R
import kotlin.random.Random

class ElectricityFragment : BaseUtilityFragment(R.layout.fragment_electricity) {

    override val usageUnit: String = "kWh"

    override fun generateRandomUsage(): Float {
        // Simulate electricity usage (e.g., 0.1 to 2.0 kWh)
        return Random.nextFloat() * 1.9f + 0.1f
    }

    override fun getLineColor(): Int {
        return Color.YELLOW
    }
} 