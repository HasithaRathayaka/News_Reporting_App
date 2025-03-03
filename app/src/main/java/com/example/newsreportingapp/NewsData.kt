package com.example.newsreportingapp

data class NewsData(
    val heading: String = "",
    val news: String = "",
    val location: String = "",
    val category: String = "",
    val imageBase64: String? = null // Add this field for Base64 image
)