package com.example.blogapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.blogapplication.databinding.ActivitySignandregistrationBinding
import com.example.blogapplication.Model.UserData
import com.example.blogapplication.register.WelcomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignandregistrationActivity : AppCompatActivity() {

    private val binding: ActivitySignandregistrationBinding by lazy {
        ActivitySignandregistrationBinding.inflate(layoutInflater)
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // ✅ Initialize Firebase Auth + Realtime Database with your region URL
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance(
            "https://blog-app-35da3-default-rtdb.asia-southeast1.firebasedatabase.app/"
        )
        userRef = database.getReference("users")

        val action = intent.getStringExtra("action")

        if (action == "login") {
            showLoginUI()
        } else if (action == "register") {
            showRegisterUI()
        }
    }

    // ================== LOGIN ==================
    private fun showLoginUI() {
        // Show login views
        binding.loginEmailAddress.visibility = View.VISIBLE
        binding.loginPasword.visibility = View.VISIBLE
        binding.loginButton.visibility = View.VISIBLE

        // Hide register views
        binding.cardView.visibility = View.GONE
        binding.registerName.visibility = View.GONE
        binding.registerEmail.visibility = View.GONE
        binding.registerPassword.visibility = View.GONE
        binding.registerButton.visibility = View.GONE
        binding.registerNewhere.visibility = View.GONE

        binding.loginButton.setOnClickListener {
            val email = binding.loginEmailAddress.text.toString().trim()
            val password = binding.loginPasword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "✅ Login Successful", Toast.LENGTH_SHORT).show()
                        Log.d("LOGIN", "User logged in: ${auth.currentUser?.uid}")
                        startActivity(Intent(this,MainActivity::class.java)); // navigate to main activity layout
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "❌ Login Failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("LOGIN", "Error: ${task.exception?.message}")
                    }
                }
        }
    }

    // ================== REGISTER ==================
    private fun showRegisterUI() {
        // Show register views
        binding.cardView.visibility = View.VISIBLE
        binding.registerName.visibility = View.VISIBLE
        binding.registerEmail.visibility = View.VISIBLE
        binding.registerPassword.visibility = View.VISIBLE
        binding.registerButton.visibility = View.VISIBLE
        binding.registerNewhere.visibility = View.VISIBLE

        // Hide login views
        binding.loginEmailAddress.visibility = View.GONE
        binding.loginPasword.visibility = View.GONE
        binding.loginButton.visibility = View.GONE

        binding.registerButton.setOnClickListener {
            val name = binding.registerName.text.toString().trim()
            val email = binding.registerEmail.text.toString().trim()
            val password = binding.registerPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result?.user
                        auth.signOut()
                        if (user != null) {
                            val userId = user.uid
                            val userData = UserData(name, email)

                            // ✅ Save user details in Realtime Database
                            userRef.child(userId).setValue(userData)
                                .addOnSuccessListener {
                                    Log.d("REGISTER", "User saved successfully")
                                    Toast.makeText(this, "✅ Registered Successfully", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this,WelcomeActivity::class.java)) // navigate to welcome page
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Log.e("REGISTER", "Error saving data: ${e.message}", e)
                                    Toast.makeText(this, "❌ Failed to save data: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                        }
                    } else {
                        Log.e("REGISTER", "Registration failed: ${task.exception?.message}")
                        Toast.makeText(
                            this,
                            "❌ Registration failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}
