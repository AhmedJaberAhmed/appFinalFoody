package com.example.finalcooking.activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.finalcooking.databinding.ActivityLoginBinding
import com.example.finalcooking.user_data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        installSplashScreen()


        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("users")

        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        val currentUser = firebaseAuth.currentUser


        if (isLoggedIn || currentUser != null) {
            navigateToMainActivity()
            return
        }

        checkNetworkAndUpdateUI()


        binding.signupText.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }




    }

    fun checkNetworkAndUpdateUI() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        val isConnected = networkCapabilities != null &&
                (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))

        if (isConnected) {

            binding.loginButton.setOnClickListener {
                val loginUsername = binding.loginEmail.text.toString().trim()
                val loginPassword = binding.loginPassword.text.toString().trim()
                if (loginUsername.isNotEmpty() && loginPassword.isNotEmpty()) {
                    loginUser(loginUsername, loginPassword)
                } else {
                    Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show()
                }
            }
        } else { binding.loginButton.setOnClickListener {    Toast.makeText(this, "No internet connection. Please check your network settings.", Toast.LENGTH_SHORT).show()}


        }
    }


    private fun loginUser(username: String, password: String) {
        databaseReference.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (userSnapshot in dataSnapshot.children) {
                            val userData = userSnapshot.getValue(UserData::class.java)
                            if (userData != null) {
                                if (userData.password == password) {
                                    saveUserLoggedInState(true)
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Login Successful",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            MainActivity::class.java
                                        )
                                    )
                                    finish()
                                    return
                                } else {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Incorrect password",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return
                                }
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Username does not exist",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Database Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
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
