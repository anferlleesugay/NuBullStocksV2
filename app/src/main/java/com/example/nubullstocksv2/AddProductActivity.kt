package com.example.nubullstocksv2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.PutObjectRequest
import com.example.nubullstocksv2.databinding.ActivityAddProductBinding
import com.google.firebase.database.FirebaseDatabase
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.UUID
import java.util.concurrent.Executors

class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private var imageUri: Uri? = null
    private lateinit var s3Client: AmazonS3Client
    private val s3BucketName = "nubullstocks"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize AWS S3 Client
        initializeAWS()

        // Back Button (Redirects to AdminDashboardActivity)
        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
            finish()
        }

        // Click ImageView to Pick Image
        binding.ivProductImage.setOnClickListener {
            openImagePicker()
        }

        // Add Product Button
        binding.btnAddProduct.setOnClickListener {
            addProduct()
        }
    }

    private fun initializeAWS() {
        try {
            val credentialsProvider = CognitoCachingCredentialsProvider(
                applicationContext,
                "ap-southeast-2:d1dd8673-bf97-4bbc-b2c8-807a4d0ea6d0",
                Regions.AP_SOUTHEAST_2
            )
            s3Client = AmazonS3Client(credentialsProvider)
        } catch (e: Exception) {
            Log.e("AWS_INIT", "Error initializing AWS: ${e.message}", e)
            Toast.makeText(this, "AWS Initialization Failed!", Toast.LENGTH_LONG).show()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICKER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            binding.ivProductImage.setImageURI(imageUri) // 🔹 Display selected image
        }
    }

    private fun addProduct() {
        val name = binding.etProductName.text.toString().trim()
        val price = binding.etProductPrice.text.toString().trim()
        val stock = binding.etProductStock.text.toString().trim()

        if (name.isEmpty() || price.isEmpty() || stock.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and choose an image", Toast.LENGTH_SHORT).show()
            return
        }

        val productId = UUID.randomUUID().toString()
        val product = Product(productId, name, price.toDouble(), stock.toInt(), "")

        // Upload image first, then save to Firebase
        uploadImageToS3(productId, product)
    }

    private fun uploadImageToS3(productId: String, product: Product) {
        imageUri?.let { uri ->
            try {
                val file = File(cacheDir, "$productId.jpg")
                val inputStream: InputStream? = contentResolver.openInputStream(uri)

                inputStream?.use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }

                val request = PutObjectRequest(s3BucketName, "$productId.jpg", file)
                Log.d("AWS_UPLOAD", "Uploading file: ${file.absolutePath}")

                Executors.newSingleThreadExecutor().execute {
                    try {
                        s3Client.putObject(request)
                        val imageUrl = "https://$s3BucketName.s3.amazonaws.com/$productId.jpg"
                        Log.d("AWS_UPLOAD", "Upload Success: $imageUrl")

                        saveProductToFirebase(productId, product.copy(imageURL = imageUrl))

                    } catch (e: Exception) {
                        runOnUiThread {
                            Log.e("AWS_S3", "Failed to upload image: ${e.message}", e)
                            Toast.makeText(this, "Image Upload Failed: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: IOException) {
                Log.e("IMAGE_PROCESS", "Error processing image: ${e.message}", e)
                Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProductToFirebase(productId: String, product: Product) {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("products")
        databaseRef.child(productId).setValue(product)
            .addOnCompleteListener { task ->
                runOnUiThread {
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Product Added Successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Log.e("FIREBASE_DB", "Failed to add product: ${task.exception?.message}", task.exception)
                        Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    companion object {
        const val IMAGE_PICKER_REQUEST_CODE = 1000
    }
}
