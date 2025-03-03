package com.example.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.admin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Set click listener for Upload CardView
        binding.gridLayout.getChildAt(0).setOnClickListener {
            val intent = Intent(this@MainActivity, UploadActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for Update CardView
        binding.gridLayout.getChildAt(1).setOnClickListener {
            val intent = Intent(this@MainActivity, UpdateActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for Delete CardView
        binding.gridLayout.getChildAt(2).setOnClickListener {
            val intent = Intent(this@MainActivity, DeleteActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for View News CardView
        binding.gridLayout.getChildAt(3).setOnClickListener {
            val intent = Intent(this@MainActivity, ViewNewsActivity::class.java) // Replace with your ViewNewsActivity
            startActivity(intent)
        }
        binding.gridLayout.getChildAt(3).setOnClickListener {
            val intent = Intent(this@MainActivity, ViewNewsActivity::class.java)
            startActivity(intent)
        }

        // Handle system insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.save)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}