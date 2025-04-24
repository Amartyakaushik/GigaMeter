package com.example.gigameter.fragments

import android.graphics.Color
import com.example.gigameter.R
import kotlin.random.Random

class WaterFragment : BaseUtilityFragment(R.layout.fragment_water) {

    override val usageUnit: String = "L"

    override fun generateRandomUsage(): Float {
        // Simulate water usage (e.g., 0 to 10 Liters)
        return Random.nextFloat() * 10
    }

    override fun getLineColor(): Int {
        return Color.BLUE
    }
} 