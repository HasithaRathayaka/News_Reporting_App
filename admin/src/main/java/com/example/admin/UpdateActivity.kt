package com.example.admin

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.admin.databinding.ActivityUpdateBinding
import com.google.firebase.database.*
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.view.View

class UpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateBinding
    private lateinit var databaseReference: DatabaseReference
    private var imageUri: Uri? = null
    private lateinit var categoryAdapter: ArrayAdapter<String>
    private val categories = mutableListOf<String>()
    private var selectedCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Initialize Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("newsapp000")

        // Initialize Spinner Adapter
        categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = categoryAdapter

        // Fetch Categories from Firebase
        fetchCategories()

        // Handle Category Selection
        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedCategory = categories[position]
                fetchDataForCategory(selectedCategory!!)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Handle Image Upload Button Click
        binding.uploadImageButton.setOnClickListener {
            selectImage()
        }

        // Handle Update Button Click
        binding.updatebtn.setOnClickListener {
            val news = binding.updatenews.text.toString().trim()
            val location = binding.updatelocation.text.toString().trim()

            if (news.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Convert the image to Base64 if an image is selected
            if (imageUri == null) {
                updateData(news, location, null)
            } else {
                val base64String = convertImageToBase64(imageUri!!)
                if (base64String != null) {
                    updateData(news, location, base64String)
                } else {
                    Toast.makeText(this, "Failed to convert image", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Handle Back Button Click
        binding.backButton.setOnClickListener {
            finish() // Close the current activity and return to MainActivity
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Fetches categories from Firebase.
     */
    private fun fetchCategories() {
        databaseReference.orderByChild("category").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val uniqueCategories = mutableSetOf<String>()
                for (childSnapshot in snapshot.children) {
                    val category = childSnapshot.child("category").value?.toString()
                    if (!category.isNullOrEmpty()) {
                        uniqueCategories.add(category)
                    }
                }
                categories.clear()
                categories.addAll(uniqueCategories.sorted())
                categoryAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UpdateActivity, "Failed to fetch categories: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Fetches data for the selected category.
     */
    private fun fetchDataForCategory(category: String) {
        databaseReference.orderByChild("category").equalTo(category)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (childSnapshot in snapshot.children) {
                            val news = childSnapshot.child("news").value.toString()
                            val location = childSnapshot.child("location").value.toString()
                            val imageBase64 = childSnapshot.child("imageBase64").value.toString()

                            // Populate the fields with fetched data
                            binding.updatenews.setText(news)
                            binding.updatelocation.setText(location)

                            // Set the image if it exists
                            if (imageBase64.isNotEmpty()) {
                                val decodedBytes = Base64.decode(imageBase64, Base64.DEFAULT)
                                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                                binding.newsImage.setImageBitmap(bitmap)
                            }

                            Toast.makeText(this@UpdateActivity, "Data Fetched", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@UpdateActivity, "No data found for Category: $category", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@UpdateActivity, "Failed to fetch data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    /**
     * Updates the data in Firebase Realtime Database.
     */
    private fun updateData(news: String, location: String, imageBase64: String?) {
        databaseReference.orderByChild("category").equalTo(selectedCategory)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (childSnapshot in snapshot.children) {
                            val updates = mutableMapOf<String, Any>(
                                "news" to news,
                                "location" to location
                            )

                            // Update the imageBase64 only if a new image is selected
                            if (imageBase64 != null) {
                                updates["imageBase64"] = imageBase64
                            }

                            childSnapshot.ref.updateChildren(updates as Map<String, Any>)
                                .addOnSuccessListener {
                                    Toast.makeText(this@UpdateActivity, "Data Updated Successfully", Toast.LENGTH_SHORT).show()
                                    clearInputFields()
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(this@UpdateActivity, "Failed to update data: ${exception.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Toast.makeText(this@UpdateActivity, "No data found for Category: $selectedCategory", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@UpdateActivity, "Failed to update data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    /**
     * Clears all input fields after a successful update.
     */
    private fun clearInputFields() {
        binding.updatenews.text.clear()
        binding.updatelocation.text.clear()
        binding.newsImage.setImageResource(android.R.color.transparent) // Clear the ImageView
        imageUri = null // Reset the image URI
    }

    /**
     * Opens the gallery to select an image.
     */
    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageUri = data?.data
            imageUri?.let {
                binding.newsImage.setImageURI(it) // Set the selected image to ImageView
                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Failed to select image", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Converts the selected image to a Base64 string.
     */
    private fun convertImageToBase64(imageUri: Uri): String? {
        return try {
            // Decode the image URI into a Bitmap
            val inputStream = contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            // Compress the Bitmap to reduce size
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream) // 80% quality
            val imageBytes = byteArrayOutputStream.toByteArray()
            // Convert the byte array to Base64
            Base64.encodeToString(imageBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}