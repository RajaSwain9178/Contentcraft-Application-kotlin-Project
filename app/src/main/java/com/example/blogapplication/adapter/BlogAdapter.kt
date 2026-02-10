package com.example.blogapplication.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.blogapplication.BlogItemModel
import com.example.blogapplication.R
import com.example.blogapplication.ReadMoreActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class BlogAdapter(private val blogList: List<BlogItemModel>) :
    RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    private val databaseReference = FirebaseDatabase.getInstance(
        "https://blog-application-568a1-default-rtdb.asia-southeast1.firebasedatabase.app/"
    ).reference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.blog_item, parent, false)
        return BlogViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blog = blogList[position]
        val postId = blog.postId
        val currUser = FirebaseAuth.getInstance().currentUser

        holder.title.text = blog.heading2 ?: ""
        holder.username.text = blog.username2 ?: ""
        holder.date.text = blog.date2 ?: ""
        holder.description.text = blog.post2 ?: ""

        // Show like count and like button state
        if (postId != null) {
            val postRef = databaseReference.child("blogs").child(postId)
            val likesRef = postRef.child("likes")
            val likeCountRef = postRef.child("likeCounts2")

            likeCountRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val count = snapshot.getValue(Int::class.java) ?: 0
                    holder.likeCount.text = count.toString()
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            if (currUser != null) {
                likesRef.child(currUser.uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            updateLikeButton(holder, snapshot.exists())
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            } else {
                updateLikeButton(holder, false)
            }
        } else {
            holder.likeCount.text = "0"
            updateLikeButton(holder, false)
        }

        // Show save button state based on saved posts in database
        if (currUser != null && postId != null) {
            val savePostRef =
                databaseReference.child("users").child(currUser.uid).child("savePosts")
                    .child(postId)
            savePostRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    updateSaveButtonUI(holder, snapshot.exists())
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        } else {
            updateSaveButtonUI(holder, false)
        }

        // Like button click listener
        holder.likeButton.setOnClickListener {
            if (currUser != null && postId != null) {
                handleLikeButton(postId, holder, currUser.uid)
            } else {
                Toast.makeText(
                    holder.itemView.context,
                    "You have to login first",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Save button click listener
        holder.postSaveButton.setOnClickListener {
            if (currUser != null && postId != null) {
                handleSaveButton(postId, holder, currUser.uid)
            } else {
                Toast.makeText(
                    holder.itemView.context,
                    "You have to login first",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Read more on item click listener
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ReadMoreActivity::class.java)
            intent.putExtra("blogItem", blog)  // Pass BlogItemModel as Parcelable
            context.startActivity(intent)
        }
    }

    // Save post toggle function
    private fun handleSaveButton(postId: String, holder: BlogViewHolder, uid: String) {
        val userReferSave = databaseReference.child("users").child(uid)
        val savePostRef = userReferSave.child("savePosts").child(postId)

        savePostRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Remove saved post
                    savePostRef.removeValue().addOnSuccessListener {
                        updateSaveButtonUI(holder, false)
                        Toast.makeText(
                            holder.itemView.context,
                            "Post removed from saved",
                            Toast.LENGTH_SHORT
                        ).show()
                    }.addOnFailureListener { e ->
                        Toast.makeText(
                            holder.itemView.context,
                            "Failed to remove saved post",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("BlogAdapter", "Failed to remove saved post: $e")
                    }
                } else {
                    // Save post
                    savePostRef.setValue(true).addOnSuccessListener {
                        updateSaveButtonUI(holder, true)
                        Toast.makeText(holder.itemView.context, "Post saved", Toast.LENGTH_SHORT)
                            .show()
                    }.addOnFailureListener { e ->
                        Toast.makeText(
                            holder.itemView.context,
                            "Failed to save post",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("BlogAdapter", "Failed to save post: $e")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    holder.itemView.context,
                    "Failed to update saved posts",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("BlogAdapter", "Save post cancelled: ${error.message}")
            }
        })
    }

    // Like button toggle function
    private fun handleLikeButton(postId: String, holder: BlogViewHolder, currUserId: String) {
        val postRef = databaseReference.child("blogs").child(postId)
        val likesRef = postRef.child("likes")
        val likeCountRef = postRef.child("likeCounts2")

        likesRef.child(currUserId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    likesRef.child(currUserId).removeValue().addOnSuccessListener {
                        likeCountRef.runTransaction(object : Transaction.Handler {
                            override fun doTransaction(currentData: MutableData): Transaction.Result {
                                val currentCount = currentData.getValue(Int::class.java) ?: 0
                                if (currentCount > 0) {
                                    currentData.value = currentCount - 1
                                }
                                return Transaction.success(currentData)
                            }

                            override fun onComplete(
                                error: DatabaseError?,
                                committed: Boolean,
                                snapshot: DataSnapshot?
                            ) {
                                updateLikeButton(holder, false)
                            }
                        })
                    }.addOnFailureListener { e ->
                        Log.e("BlogAdapter", "Failed to unlike post: $e")
                    }
                } else {
                    likesRef.child(currUserId).setValue(true).addOnSuccessListener {
                        likeCountRef.runTransaction(object : Transaction.Handler {
                            override fun doTransaction(currentData: MutableData): Transaction.Result {
                                val currentCount = currentData.getValue(Int::class.java) ?: 0
                                currentData.value = currentCount + 1
                                return Transaction.success(currentData)
                            }

                            override fun onComplete(
                                error: DatabaseError?,
                                committed: Boolean,
                                snapshot: DataSnapshot?
                            ) {
                                updateLikeButton(holder, true)
                            }
                        })
                    }.addOnFailureListener { e ->
                        Log.e("BlogAdapter", "Failed to like post: $e")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Update save button UI
    private fun updateSaveButtonUI(holder: BlogViewHolder, isSaved: Boolean) {
        if (isSaved) {
            holder.postSaveButton.setImageResource(R.drawable.red_full_save)
        } else {
            holder.postSaveButton.setImageResource(R.drawable.black_save)
        }
    }

    // Update like button UI
    private fun updateLikeButton(holder: BlogViewHolder, liked: Boolean) {
        if (liked) {
            holder.likeButton.setImageResource(R.drawable.red_heart)
        } else {
            holder.likeButton.setImageResource(R.drawable.black_heart)
        }
    }

    override fun getItemCount() = blogList.size

    class BlogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.heading2)
        val username: TextView = itemView.findViewById(R.id.username2)
        val date: TextView = itemView.findViewById(R.id.date2)
        val description: TextView = itemView.findViewById(R.id.post2)
        val likeCount: TextView = itemView.findViewById(R.id.likeCount2)
        val likeButton: ImageButton = itemView.findViewById(R.id.like2)
        val postSaveButton: ImageButton = itemView.findViewById(R.id.likesave2)
    }
}
