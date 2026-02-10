package com.example.blogapplication

//import BlogItemModel
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.blogapplication.databinding.ActivityMainBinding
import com.example.blogapplication.databinding.ActivityReadMoreBinding

class ReadMoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReadMoreBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backButton.setOnClickListener {
            finish()
        }
        // ✅ Get Parcelable object from intent
        val blogItem = intent.getParcelableExtra<BlogItemModel>("blogItem")

        if (blogItem != null) {
            // Example usage
            findViewById<TextView>(R.id.titleText).text = blogItem.heading2
            findViewById<TextView>(R.id.userName).text = blogItem.username2
            findViewById<TextView>(R.id.date).text = blogItem.date2
            findViewById<TextView>(R.id.blogDescriptiontextView).text = blogItem.post2
        } else {
            Toast.makeText(this, "Failed to load blogs", Toast.LENGTH_SHORT).show()
        }
    }
}