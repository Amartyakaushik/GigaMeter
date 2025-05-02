package com.example.gigameter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gigameter.databinding.ActivityAuthBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var auth: FirebaseAuth
    private val TAG = "AuthActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            FirebaseApp.initializeApp(this)
            auth = FirebaseAuth.getInstance()

            if (auth.currentUser != null) {
                navigateToMain()
                return
            }

            binding.loginButton.setOnClickListener {
                val email = binding.emailEditText.text.toString().trim()
                val password = binding.passwordEditText.text.toString().trim()

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Please enter email and password.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                setLoading(true)
                signInUser(email, password)
            }

            binding.signupButton.setOnClickListener {
                startActivity(Intent(this, SignupActivity::class.java))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Firebase initialization failed", e)
            Toast.makeText(this, "Firebase initialization failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun signInUser(email: String, password: String) {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    setLoading(false)
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithEmail:success")
                        navigateToMain()
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        val errorMessage = when (task.exception?.message) {
                            "The password is invalid or the user does not have a password." -> "Invalid email or password"
                            "There is no user record corresponding to this identifier. The user may have been deleted." -> "No account found with this email"
                            "A network error (such as timeout, interrupted connection or unreachable host) has occurred." -> "Network error. Please check your connection"
                            else -> "Authentication failed: ${task.exception?.message}"
                        }
                        Toast.makeText(
                            baseContext,
                            errorMessage,
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error during sign in", e)
            setLoading(false)
            Toast.makeText(
                baseContext,
                "Error during sign in: ${e.message}",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.loginButton.isEnabled = !isLoading
        binding.signupButton.isEnabled = !isLoading
        binding.emailEditText.isEnabled = !isLoading
        binding.passwordEditText.isEnabled = !isLoading
    }
} 