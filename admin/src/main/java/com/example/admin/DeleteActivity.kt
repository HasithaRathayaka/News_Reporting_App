package com.example.admin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.admin.databinding.ActivityDeleteBinding
import com.google.firebase.database.*

class DeleteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeleteBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Enable back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Handle delete button click
        binding.button3.setOnClickListener {
            val categoryNumber = binding.deletenews.text.toString().trim()

            if (categoryNumber.isNotEmpty()) {
                deleteNewsByCategory(categoryNumber)
            } else {
                Toast.makeText(this, "Please enter a category number", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle back button click
        binding.backButton.setOnClickListener {
            finish() // Close the current activity and return to the previous one
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Deletes all news items matching the given category number.
     */
    private fun deleteNewsByCategory(categoryNumber: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("newsapp000")

        // Query the database for news items with the specified category
        databaseReference.orderByChild("category").equalTo(categoryNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        // No data found for the given category
                        Toast.makeText(
                            this@DeleteActivity,
                            "No news found for category $categoryNumber",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    // Iterate through all matching news items and delete them
                    var deletedCount = 0
                    for (child in snapshot.children) {
                        child.ref.removeValue()
                            .addOnSuccessListener {
                                deletedCount++
                                if (deletedCount == snapshot.childrenCount.toInt()) {
                                    // All items deleted successfully
                                    binding.deletenews.text.clear()
                                    Toast.makeText(
                                        this@DeleteActivity,
                                        "All news for category $categoryNumber deleted successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(
                                    this@DeleteActivity,
                                    "Failed to delete news: ${exception.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@DeleteActivity,
                        "Database error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // Handle action bar back button click
    override fun onSupportNavigateUp(): Boolean {
        finish() // Close the current activity and return to the previous one
        return true
    }
}