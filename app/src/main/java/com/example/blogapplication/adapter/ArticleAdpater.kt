package com.example.blogapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blogapplication.BlogItemModel
import com.example.blogapplication.databinding.ArticleItemBinding

class ArticleAdapter(
    private val context: Context,
    private var blogList: List<BlogItemModel>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ArticleAdapter.BlogViewHolder>() {

    interface OnItemClickListener {
        fun onEdit(blogItem: BlogItemModel)
        fun onRead(blogItem: BlogItemModel)
        fun onDelete(blogItem: BlogItemModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ArticleItemBinding.inflate(inflater, parent, false)
        return BlogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blogItem = blogList[position]
        holder.bind(blogItem)
    }

    override fun getItemCount(): Int = blogList.size

    fun setData(blogSavedList: ArrayList<BlogItemModel>) {
        this.blogList = blogSavedList
        notifyDataSetChanged()
    }

    inner class BlogViewHolder(private val binding: ArticleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(blogItem: BlogItemModel) {
            binding.heading2.text = blogItem.heading2
            binding.username2.text = blogItem.username2
            binding.date2.text = blogItem.date2
            binding.post2.text = blogItem.post2

            // ✅ Delete
            binding.delete.setOnClickListener {
                itemClickListener.onDelete(blogItem)
            }

            // ✅ Edit
            binding.edit.setOnClickListener {
                itemClickListener.onEdit(blogItem)
            }

            // ✅ Read More → whole card click OR separate button if you added one
            binding.root.setOnClickListener {
                itemClickListener.onRead(blogItem)
            }
            // If you have a specific read more button:
            // binding.readMore.setOnClickListener { itemClickListener.onRead(blogItem) }
        }
    }
}
