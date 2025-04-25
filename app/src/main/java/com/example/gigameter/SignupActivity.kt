package com.example.gigameter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gigameter.databinding.ActivitySignupBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val TAG = "SignupActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            // Initialize Firebase
            FirebaseApp.initializeApp(this)
            auth = FirebaseAuth.getInstance()
            db = FirebaseFirestore.getInstance()

            binding.signupButton.setOnClickListener {
                val name = binding.nameEditText.text.toString().trim()
                val email = binding.emailEditText.text.toString().trim()
                val password = binding.passwordEditText.text.toString().trim()
                val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()

                if (validateInputs(name, email, password, confirmPassword)) {
                    setLoading(true)
                    createUser(name, email, password)
                }
            }

            binding.loginButton.setOnClickListener {
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Firebase initialization failed", e)
            Toast.makeText(this, "Firebase initialization failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun validateInputs(name: String, email: String, password: String, confirmPassword: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.nameInputLayout.error = "Name is required"
            isValid = false
        } else {
            binding.nameInputLayout.error = null
        }

        if (email.isEmpty()) {
            binding.emailInputLayout.error = "Email is required"
            isValid = false
        } else {
            binding.emailInputLayout.error = null
        }

        if (password.isEmpty()) {
            binding.passwordInputLayout.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            binding.passwordInputLayout.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            binding.passwordInputLayout.error = null
        }

        if (confirmPassword.isEmpty()) {
            binding.confirmPasswordInputLayout.error = "Please confirm your password"
            isValid = false
        } else if (password != confirmPassword) {
            binding.confirmPasswordInputLayout.error = "Passwords do not match"
            isValid = false
        } else {
            binding.confirmPasswordInputLayout.error = null
        }

        return isValid
    }

    private fun createUser(name: String, email: String, password: String) {
        try {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign up success, create user profile in Firestore
                        val user = auth.currentUser
                        val userData = hashMapOf(
                            "name" to name,
                            "email" to email,
                            "createdAt" to System.currentTimeMillis()
                        )

                        db.collection("users")
                            .document(user?.uid ?: "")
                            .set(userData)
                            .addOnSuccessListener {
                                Log.d(TAG, "User profile created successfully")
                                setLoading(false) // Make sure to set loading to false before navigation
                                navigateToMain()
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error creating user profile", e)
                                setLoading(false)
                                Toast.makeText(
                                    baseContext,
                                    "Error creating user profile: ${e.message}",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                    } else {
                        // If sign up fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        setLoading(false)
                        val errorMessage = when (task.exception?.message) {
                            "The email address is already in use by another account." -> "Email already registered"
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
            Log.e(TAG, "Error during user creation", e)
            setLoading(false)
            Toast.makeText(
                baseContext,
                "Error during signup: ${e.message}",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Close SignupActivity so user can't go back to it
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.signupButton.isEnabled = !isLoading
        binding.loginButton.isEnabled = !isLoading
        binding.nameEditText.isEnabled = !isLoading
        binding.emailEditText.isEnabled = !isLoading
        binding.passwordEditText.isEnabled = !isLoading
        binding.confirmPasswordEditText.isEnabled = !isLoading
    }
} 