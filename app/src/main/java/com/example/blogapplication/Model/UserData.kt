package com.example.blogapplication.Model

data class UserData(
    var name: String,
    var email: String
) {
    // ✅ Secondary no-arg constructor (needed for Firebase sometimes)
    constructor() : this("", "")
}