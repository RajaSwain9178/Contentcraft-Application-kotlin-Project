package com.example.blogapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.blogapplication.databinding.ActivityEditBinding
import com.google.firebase.database.FirebaseDatabase

class EditActivity : AppCompatActivity() {

    private val binding: ActivityEditBinding by lazy {
        ActivityEditBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.imageButton.setOnClickListener {
            finish()
        }

        // Get the blog item from intent
        val blogItemModel = intent.getParcelableExtra<BlogItemModel>("blogItem")

        // Set current values in edit texts
        binding.blogTitle.editText?.setText(blogItemModel?.heading2)
        binding.blogDescription.editText?.setText(blogItemModel?.post2)

        binding.addbutton.setOnClickListener {
            val updatedTitle = binding.blogTitle.editText?.text.toString().trim()
            val updatedDescription = binding.blogDescription.editText?.text.toString().trim()

            if (updatedTitle.isEmpty() || updatedDescription.isEmpty()) {
                Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()
            } else {
                blogItemModel?.heading2 = updatedTitle
                blogItemModel?.post2 = updatedDescription

                if (blogItemModel != null) {
                    updateDatabase(blogItemModel)
                }
            }
        }
    }

    private fun updateDatabase(blogItemModel: BlogItemModel) {
        val databaseRef = FirebaseDatabase.getInstance(
            "https://blog-app-35da3-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("blogs")
        val postId = blogItemModel.postId ?: return

        // Only update title and description, preserve other fields like likes
        val updates = mapOf(
            "heading2" to blogItemModel.heading2,
            "post2" to blogItemModel.post2
        )

        databaseRef.child(postId).updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Blog Updated Successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Blog Update Failed", Toast.LENGTH_SHORT).show()
            }
    }
}
