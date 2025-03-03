package com.example.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.databinding.ActivityViewNewsBinding
import com.google.firebase.database.*

class ViewNewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewNewsBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var newsAdapter: NewsAdapter
    private val newsList = mutableListOf<NewsData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize RecyclerView
        newsAdapter = NewsAdapter(newsList)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ViewNewsActivity) // Use LinearLayoutManager
            adapter = newsAdapter
        }

        // Fetch data from Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("newsapp000")
        fetchNewsData()
    }

    private fun fetchNewsData() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                newsList.clear()
                for (childSnapshot in snapshot.children) {
                    val newsData = childSnapshot.getValue(NewsData::class.java)
                    newsData?.let { newsList.add(it) }
                }
                newsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}