package com.example.blogapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.blogapplication.Model.UserData
import com.example.blogapplication.databinding.ActivityAddArticleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date

class AddArticleActivity : AppCompatActivity() {

    private val binding: ActivityAddArticleBinding by lazy {
        ActivityAddArticleBinding.inflate(layoutInflater)
    }

    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance("https://blog-app-35da3-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("blogs")

    private val userReference: DatabaseReference =
        FirebaseDatabase.getInstance("https://blog-app-35da3-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("users")

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.imageButton.setOnClickListener {
            finish()
        }

        binding.addbutton.setOnClickListener {
            val title = binding.blogTitle.editText?.text.toString().trim()
            val description = binding.blogDescription.editText?.text.toString().trim()

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user: FirebaseUser? = auth.currentUser
            if (user != null) {
                val userId = user.uid

                // ✅ fetch user name from "users" node
                userReference.child(userId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        @SuppressLint("SimpleDateFormat")
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userData = snapshot.getValue(UserData::class.java)
                            val userNameFromDb = userData?.name ?: "Anonymous"

                            val currDate = SimpleDateFormat("yyyy-MM-dd").format(Date())

                            // ✅ create blog with userId
                            val blogItem = BlogItemModel(
                                heading2 = title,
                                username2 = userNameFromDb,
                                date2 = currDate,
                                userId = userId,   // ✅ store userId
                                post2 = description,
                                likeCounts2 = 0,
                                postId = null
                            )

                            // generate unique blog post id
                            val key = databaseReference.push().key
                            if (key != null) {
                                blogItem.postId = key
                                val blogReference = databaseReference.child(key)
                                blogReference.setValue(blogItem).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        Toast.makeText(
                                            this@AddArticleActivity,
                                            "Blog Added Successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this@AddArticleActivity,
                                            "Failed to add blog",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(
                                this@AddArticleActivity,
                                "Error fetching user data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }
    }
}
