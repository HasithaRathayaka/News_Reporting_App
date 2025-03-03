package com.example.admin

/**
 * Data class representing a news article.
 * This class is used to map data from Firebase Realtime Database.
 */
data class NewsData(
    val heading: String = "",       // Title of the news article
    val news: String = "",          // Content of the news article
    val location: String = "",      // Location associated with the news
    val category: String = "",      // Category of the news (e.g., Sports, Politics)
    val imageBase64: String? = null // Nullable field for Base64-encoded image
) {
    // Empty constructor required for Firebase deserialization
    constructor() : this("", "", "", "", null)
}