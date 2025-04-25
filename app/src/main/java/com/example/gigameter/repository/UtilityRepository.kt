package com.example.gigameter.repository

import com.example.gigameter.models.UtilityData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

class UtilityRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
    }

    suspend fun getUtilityData(utilityType: String): UtilityData {
        val userId = getCurrentUserId()
        val docRef = db.collection("users")
            .document(userId)
            .collection("utilities")
            .document(utilityType)

        return try {
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                snapshot.toObject(UtilityData::class.java) ?: UtilityData()
            } else {
                // Create default document if it doesn't exist
                val defaultData = UtilityData(dailyLimit = 100) // Default daily limit
                docRef.set(defaultData).await()
                defaultData
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch utility data: ${e.message}")
        }
    }

    suspend fun updateUsage(utilityType: String, newUsage: Int) {
        val userId = getCurrentUserId()
        val docRef = db.collection("users")
            .document(userId)
            .collection("utilities")
            .document(utilityType)

        try {
            val currentData = getUtilityData(utilityType)
            val updatedHistory = currentData.usageHistory.toMutableList().apply {
                add(newUsage)
                // Keep only last 30 days of history
                if (size > 30) removeAt(0)
            }

            docRef.update(
                "usageHistory", updatedHistory,
                "lastUpdated", Date()
            ).await()
        } catch (e: Exception) {
            throw Exception("Failed to update usage: ${e.message}")
        }
    }

    suspend fun updateDailyLimit(utilityType: String, newLimit: Int) {
        val userId = getCurrentUserId()
        val docRef = db.collection("users")
            .document(userId)
            .collection("utilities")
            .document(utilityType)

        try {
            docRef.update("dailyLimit", newLimit).await()
        } catch (e: Exception) {
            throw Exception("Failed to update daily limit: ${e.message}")
        }
    }
} 