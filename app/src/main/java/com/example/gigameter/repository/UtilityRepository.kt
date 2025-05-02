package com.example.gigameter.repository

import android.util.Log
import com.example.gigameter.models.UtilityData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import kotlin.random.Random

class UtilityRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
    }

    suspend fun populateDemoData() {
        val userId = getCurrentUserId()
        
        // Electricity Demo Data
        val electricityData = UtilityData(
            dailyLimit = 50,
            usageHistory = listOf(
                42, // A week ago
                38,
                45,
                39,
                41,
                37,
                43  // Today
            )
        )
        
        // Water Demo Data
        val waterData = UtilityData(
            dailyLimit = 200,
            usageHistory = listOf(
                180,
                165,
                190,
                175,
                185,
                170,
                188
            )
        )
        
        // Gas Demo Data
        val gasData = UtilityData(
            dailyLimit = 15,
            usageHistory = listOf(
                12,
                11,
                13,
                10,
                14,
                12,
                13
            )
        )

        try {
            // Save Electricity Data
            db.collection("users")
                .document(userId)
                .collection("utilities")
                .document("electricity")
                .set(electricityData)
                .await()

            // Save Water Data
            db.collection("users")
                .document(userId)
                .collection("utilities")
                .document("water")
                .set(waterData)
                .await()

            // Save Gas Data
            db.collection("users")
                .document(userId)
                .collection("utilities")
                .document("gas")
                .set(gasData)
                .await()

            Log.d("UtilityRepository", "Demo data populated successfully")
        } catch (e: Exception) {
            Log.e("UtilityRepository", "Error populating demo data", e)
            throw e
        }
    }

    suspend fun getUtilityData(utilityType: String): UtilityData {
        val userId = getCurrentUserId()
        Log.d("UtilityRepository", "Fetching data for user: $userId, utility: $utilityType")
        
        val docRef = db.collection("users")
            .document(userId)
            .collection("utilities")
            .document(utilityType)

        return try {
            val snapshot = docRef.get().await()
            Log.d("UtilityRepository", "Document exists: ${snapshot.exists()}")
            
            if (snapshot.exists()) {
                val data = snapshot.toObject(UtilityData::class.java)
                Log.d("UtilityRepository", "Retrieved data: $data")
                data ?: UtilityData()
            } else {
                Log.d("UtilityRepository", "No document found, creating default")
                // Create default document if it doesn't exist
                val defaultData = UtilityData(dailyLimit = 100) // Default daily limit
                docRef.set(defaultData).await()
                defaultData
            }
        } catch (e: Exception) {
            Log.e("UtilityRepository", "Error fetching utility data", e)
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
            Log.e("UtilityRepository", "Error updating usage", e)
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
            Log.e("UtilityRepository", "Error updating daily limit", e)
            throw Exception("Failed to update daily limit: ${e.message}")
        }
    }

    suspend fun clearDemoData() {
        val userId = getCurrentUserId()
        Log.d("UtilityRepository", "Clearing demo data for user: $userId")

        val emptyData = UtilityData(dailyLimit = 0, usageHistory = emptyList())
        
        try {
            // Clear Electricity Data
            db.collection("users")
                .document(userId)
                .collection("utilities")
                .document("electricity")
                .set(emptyData)
                .await()

            // Clear Water Data
            db.collection("users")
                .document(userId)
                .collection("utilities")
                .document("water")
                .set(emptyData)
                .await()

            // Clear Gas Data
            db.collection("users")
                .document(userId)
                .collection("utilities")
                .document("gas")
                .set(emptyData)
                .await()

            Log.d("UtilityRepository", "Demo data cleared successfully")
        } catch (e: Exception) {
            Log.e("UtilityRepository", "Error clearing demo data", e)
            throw e
        }
    }
}