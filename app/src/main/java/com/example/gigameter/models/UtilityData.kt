package com.example.gigameter.models
 
data class UtilityData(
    val usageHistory: List<Int> = emptyList(),
    val dailyLimit: Int = 0
) 