package com.example.yummy.data

import com.google.firebase.Timestamp

class RecipePost (
    var id: String = "",
    val userId: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val headerImageUrl: String = "",
    val name: String = "",
    val ingredients: String = "",
    val steps: String = "",
    val category: String = ""
)
