package com.example.newsreportingapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NewsAdapter(private val newsList: List<NewsData>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headingTextView: TextView = itemView.findViewById(R.id.headingTextView)
        val newsTextView: TextView = itemView.findViewById(R.id.newsTextView)
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        val categoryTextView: TextView = itemView.findViewById(R.id.categoryTextView)
        val newsImageView: ImageView = itemView.findViewById(R.id.newsImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem = newsList[position]
        holder.headingTextView.text = currentItem.heading
        holder.newsTextView.text = currentItem.news
        holder.locationTextView.text = "Location: ${currentItem.location}"
        holder.categoryTextView.text = "Category: ${currentItem.category}"

        // Decode Base64 string and set the image
        if (currentItem.imageBase64 != null) {
            val bitmap = decodeBase64ToBitmap(currentItem.imageBase64)
            holder.newsImageView.setImageBitmap(bitmap)
        } else {
            holder.newsImageView.setImageResource(android.R.color.transparent) // Placeholder
        }
    }

    private fun decodeBase64ToBitmap(base64String: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getItemCount(): Int {
        return newsList.size
    }
}