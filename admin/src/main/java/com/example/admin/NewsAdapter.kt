package com.example.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.admin.databinding.ItemNewsBinding

class NewsAdapter(private val newsList: List<NewsData>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemNewsBinding.bind(itemView)
        val heading: TextView = binding.heading
        val news: TextView = binding.news
        val location: TextView = binding.location
        val imageView: ImageView = binding.imageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsData = newsList[position]
        holder.heading.text = newsData.heading
        holder.news.text = newsData.news
        holder.location.text = newsData.location

        // Load image using Glide
        if (!newsData.imageBase64.isNullOrEmpty()) {
            val decodedBytes = android.util.Base64.decode(newsData.imageBase64, android.util.Base64.DEFAULT)
            Glide.with(holder.itemView.context)
                .asBitmap()
                .load(decodedBytes)
                .into(holder.imageView)
        } else {
            holder.imageView.setImageResource(android.R.color.transparent)
        }
    }

    override fun getItemCount(): Int = newsList.size
}