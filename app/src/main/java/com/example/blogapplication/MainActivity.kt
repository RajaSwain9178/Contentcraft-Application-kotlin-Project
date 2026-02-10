package com.example.blogapplication

//import BlogItemModel
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blogapplication.adapter.BlogAdapter
import com.example.blogapplication.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var databaseReference: DatabaseReference
    private val blogItems = mutableListOf<BlogItemModel>()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // to go save article page

        binding.saveArticle.setOnClickListener{
            startActivity((Intent(this, SavedArticleActivity::class.java)))
        }


        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance(
            "https://blog-app-35da3-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("blogs")

        binding.imageView2.setOnClickListener {
            startActivity(Intent(this, profileActivity::class.java))
        }

        binding.cardView2.setOnClickListener {
            startActivity(Intent(this, profileActivity::class.java))
        }

        // RecyclerView setup
        val recyclerView = binding.blogRecyclerview
        val blogAdapter = BlogAdapter(blogItems)
        recyclerView.adapter = blogAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch blogs
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                blogItems.clear() // ✅ avoid duplicates
                for (child in snapshot.children) {
                    val blogItem = child.getValue(BlogItemModel::class.java)
                    if (blogItem != null) {
                        blogItems.add(blogItem)
                    }
                }

//                reverse content
                blogItems.reverse()
                blogAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Blog loading failed", Toast.LENGTH_SHORT).show()
            }
        })

        // Floating button → Add Article
        binding.floatingAddArticlButton.setOnClickListener {
            startActivity(Intent(this, AddArticleActivity::class.java))
        }
    }
}