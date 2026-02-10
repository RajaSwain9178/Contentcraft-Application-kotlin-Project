package com.example.blogapplication.register

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.blogapplication.MainActivity
import com.example.blogapplication.SignandregistrationActivity

import com.example.blogapplication.databinding.ActivityWelcomeBinding // ✅ Add here
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : AppCompatActivity() {
    private val binding: ActivityWelcomeBinding by lazy {
        ActivityWelcomeBinding.inflate(layoutInflater)
    }
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        binding.loginButton.setOnClickListener {
            val intent = Intent(this, SignandregistrationActivity::class.java)
            intent.putExtra("action", "login") // ✅ Correct method
            startActivity(intent)
            finish()
        }

        binding.registerButton.setOnClickListener {
            val intent = Intent(this, SignandregistrationActivity::class.java)
            intent.putExtra("action", "register") // ✅ Correct method
            startActivity(intent)
            finish()
        }

    }
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            startActivity((Intent(this, MainActivity::class.java)))
            finish()
        }
    }
}
