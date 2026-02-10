package com.example.blogapplication

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blogapplication.adapter.BlogAdapter
import com.example.blogapplication.databinding.ActivitySavedArticleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SavedArticleActivity : AppCompatActivity() {
    private val binding: ActivitySavedArticleBinding by lazy {
        ActivitySavedArticleBinding.inflate(layoutInflater)
    }
    private val savedBlogArticle = mutableListOf<BlogItemModel>()
    private lateinit var blogAdapter: BlogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        blogAdapter = BlogAdapter(savedBlogArticle)

        val recyclerView = binding.savedArticleRecycleView
        recyclerView.adapter = blogAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val databaseUrl =
                "https://blog-app-35da3-default-rtdb.asia-southeast1.firebasedatabase.app/"
            val userSavePostsRef = FirebaseDatabase.getInstance(databaseUrl)
                .getReference("users").child(userId).child("savePosts")
            val blogsRef = FirebaseDatabase.getInstance(databaseUrl)
                .getReference("blogs")

            userSavePostsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    savedBlogArticle.clear()
                    if (snapshot.exists()) {
                        val postIds = snapshot.children.mapNotNull { it.key }
                        if (postIds.isEmpty()) {
                            blogAdapter.notifyDataSetChanged()
                            return
                        }
                        var loadedCount = 0
                        for (postId in postIds) {
                            blogsRef.child(postId)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(postSnapshot: DataSnapshot) {
                                        val blogItem =
                                            postSnapshot.getValue(BlogItemModel::class.java)
                                        if (blogItem != null) {
                                            blogItem.isSaved = true
                                            savedBlogArticle.add(blogItem)
                                        }
                                        loadedCount++
                                        if (loadedCount == postIds.size) {
                                            blogAdapter.notifyDataSetChanged()
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        loadedCount++
                                        if (loadedCount == postIds.size) {
                                            blogAdapter.notifyDataSetChanged()
                                        }
                                    }
                                })
                        }
                    } else {
                        blogAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error appropriately, e.g., show a message to user or log
                }
            })
        }

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }
}
