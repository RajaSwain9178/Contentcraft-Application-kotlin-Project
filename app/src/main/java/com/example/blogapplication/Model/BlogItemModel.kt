package com.example.blogapplication


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BlogItemModel(
    var heading2: String? = null,
    val username2: String? = null,
    val date2: String? = null,
    var post2: String? = null,
    val userId: String? = null,
    var isSaved: Boolean = false,
    var likeCounts2: Int = 0,
    var postId: String? = null
) : Parcelable


