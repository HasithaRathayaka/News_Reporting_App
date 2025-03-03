package com.example.admin

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.admin.databinding.ActivityUploadBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory
import android.graphics.Bitmap

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private lateinit var databaseReference: DatabaseReference
    private var imageUri: Uri? = null // Nullable to handle cases where no image is selected

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Handle Save Button Click
        binding.save.setOnClickListener {
            val heading = binding.heading.text.toString().trim()
            val news = binding.news.text.toString().trim()
            val location = binding.location.text.toString().trim()
            val category = binding.category.text.toString().trim()

            // Validate input fields
            if (heading.isEmpty() || news.isEmpty() || location.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ensure the category is valid (not empty or invalid characters)
            if (category.isBlank()) {
                Toast.makeText(this, "Category cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Convert the image to Base64 if an image is selected
            if (imageUri == null) {
                // Save data without an image
                saveDataToDatabase(heading, news, location, category, null)
            } else {
                // Convert the image to Base64 and save data
                val base64String = convertImageToBase64(imageUri!!)
                if (base64String != null) {
                    saveDataToDatabase(heading, news, location, category, base64String)
                } else {
                    Toast.makeText(this, "Failed to convert image", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Handle Back Button Click
        binding.backButton.setOnClickListener {
            finish() // Close the current activity and return to MainActivity
        }

        // Handle Image Upload Button Click
        binding.uploadImageButton.setOnClickListener {
            selectImage()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.save)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

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

    private fun convertImageToBase64(imageUri: Uri): String? {
        return try {
            // Decode the image URI into a Bitmap
            val inputStream = contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // Compress the Bitmap to reduce size
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream) // 80% quality
            val imageBytes = byteArrayOutputStream.toByteArray()

            // Convert the byte array to Base64
            Base64.encodeToString(imageBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveDataToDatabase(
        heading: String,
        news: String,
        location: String,
        category: String,
        imageBase64: String?
    ) {
        // Get a reference to Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("newsapp000")
        val newsData = mapOf(
            "heading" to heading,
            "news" to news,
            "location" to location,
            "category" to category,
            "imageBase64" to imageBase64 // Store the Base64 string
        )

        // Save the data to Firebase Realtime Database
        databaseReference.push().setValue(newsData)
            .addOnSuccessListener {
                Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()
                clearInputFields()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to save data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearInputFields() {
        binding.heading.text.clear()
        binding.news.text.clear()
        binding.location.text.clear()
        binding.category.text.clear()
        binding.newsImage.setImageResource(android.R.color.transparent) // Clear the ImageView
        imageUri = null // Reset the image URI
    }
}