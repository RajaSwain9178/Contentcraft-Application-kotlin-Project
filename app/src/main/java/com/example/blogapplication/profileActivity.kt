package com.example.blogapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.blogapplication.databinding.ActivityProfileBinding
import com.example.blogapplication.register.WelcomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class profileActivity : AppCompatActivity() {
    private val binding: ActivityProfileBinding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }

    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.buttonAddArticle.setOnClickListener{
            startActivity((Intent(this, AddArticleActivity::class.java)))
        }
        binding.buttonYourArticles.setOnClickListener{
            startActivity(Intent(this, ArticleActivity::class.java))
        }
        binding.buttonLogout.setOnClickListener{
            auth.signOut()
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }
        // Initialize Firebase Auth and Database Reference
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance(
            "https://blog-app-35da3-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).reference.child("users")

        val userId = auth.currentUser?.uid
        if (userId != null) {
            loadUserProfileData(userId)
        } else {
            // Handle user not logged in e.g., show default UI or redirect to login
            binding.profileName.text = "Guest"
        }
    }

    private fun loadUserProfileData(userId: String) {
        val userRefer = databaseReference.child(userId)
        userRefer.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val username = snapshot.child("name").getValue(String::class.java) ?: "User"
                // Update UI with username
                binding.profileName.text = username
                // Load other profile data here if needed
            } else {
                // User data not found
                binding.profileName.text = "User"
            }
        }.addOnFailureListener {
            // Handle error
            binding.profileName.text = "User"
        }
    }
}
