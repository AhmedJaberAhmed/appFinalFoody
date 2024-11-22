package com.example.finalcooking.activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finalcooking.databinding.ActivitySignupBinding
import com.example.finalcooking.user_data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("users")

        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            navigateToMainActivity()
            return
        }


        checkNetworkAndUpdateUI()

        binding.loginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun checkNetworkAndUpdateUI() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        val isConnected = networkCapabilities != null &&
                (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))

        if (isConnected) {
            binding.signupButton.setOnClickListener {
                val email = binding.signupEmail.text.toString().trim()
                val password = binding.signupPassword.text.toString().trim()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    signUpWithEmail(email, password)
                } else {
                    Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
           binding.signupButton.setOnClickListener{
               Toast.makeText(this, "No internet connection. Please check your network settings.", Toast.LENGTH_LONG).show()
           }
        }
    }

    private fun signUpWithEmail(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = firebaseAuth.currentUser
                    val userId = firebaseUser?.uid

                    if (userId != null) {
                        val userData = UserData(userId, email, password)
                        databaseReference.child(userId).setValue(userData)
                            .addOnCompleteListener {
                                saveUserLoggedInState(true)
                                Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Database Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Sign up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserLoggedInState(isLoggedIn: Boolean) {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", isLoggedIn)
        editor.apply()
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
