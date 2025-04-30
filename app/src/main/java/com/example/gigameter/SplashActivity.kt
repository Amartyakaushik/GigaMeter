package com.example.gigameter

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate // Import for theme setting
import com.example.gigameter.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen") // Suppress warning as we use a custom implementation
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var auth: FirebaseAuth
    private val SPLASH_DELAY: Long = 1700 // Delay in milliseconds (e.g., 2.5 seconds)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

//        // --- Theme Buttons (Optional - Requires more setup for persistence) ---
//        // For now, these buttons just demonstrate the layout.
//        // Implementing theme switching requires SharedPreferences and applying the theme before setContentView.
//        binding.buttonLightTheme.setOnClickListener {
//            // Apply Light Theme (Example - not persistent)
//            // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            proceedToNextScreen() // Example: proceed after selection
//        }
//        binding.buttonDarkTheme.setOnClickListener {
//            // Apply Dark Theme (Example - not persistent)
//            // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//             proceedToNextScreen() // Example: proceed after selection
//         }
//
//        // --- Automatic Proceed After Delay (Alternative/Default) ---
//        // If you don't want theme selection here, use this Handler approach
//        // Remove the button listeners above if using this automatic delay.
        Handler(Looper.getMainLooper()).postDelayed({
            proceedToNextScreen()
        }, SPLASH_DELAY)
    }

    private fun proceedToNextScreen() {
        // Check if user is logged in
        if (auth.currentUser != null) {
            // User is logged in, go to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // User is not logged in, go to AuthActivity
            startActivity(Intent(this, AuthActivity::class.java))
        }
        finish() // Close this activity
    }
} 