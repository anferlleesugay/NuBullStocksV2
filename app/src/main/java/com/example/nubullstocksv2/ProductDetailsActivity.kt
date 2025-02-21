package com.example.nubullstocksv2

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var productImage: ImageView
    private lateinit var productName: TextView
    private lateinit var productStock: TextView
    private lateinit var addToCartButton: Button
    private lateinit var preOrderButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        // Initialize views
        backButton = findViewById(R.id.backButton)
        productImage = findViewById(R.id.productImage)
        productName = findViewById(R.id.productName)
        productStock = findViewById(R.id.productStock)
        addToCartButton = findViewById(R.id.addToCartButton)
        preOrderButton = findViewById(R.id.preOrderButton)

        // Get the product data passed from MainActivity
        val product = intent.getParcelableExtra<Product>("PRODUCT")

        // Check if the product is null
        if (product != null) {
            // Set the product details
            productName.text = product.name
            productStock.text = "In Stock: ${product.stock}"
            Glide.with(this).load(product.imageURL).into(productImage)

            // Toggle buttons based on stock availability
            if (product.stock > 0) {
                addToCartButton.isEnabled = true
                preOrderButton.isEnabled = false
            } else {
                addToCartButton.isEnabled = false
                preOrderButton.isEnabled = true
            }

            // Back button functionality
            backButton.setOnClickListener {
                onBackPressed()
            }

            // Add to cart functionality
            addToCartButton.setOnClickListener {
                // Handle add to cart functionality
                Toast.makeText(this, "Product added to cart", Toast.LENGTH_SHORT).show()
            }

            // Pre-order functionality
            preOrderButton.setOnClickListener {
                placePreOrder(product)
            }
        } else {
            // If product is null, show a message and close the activity
            Toast.makeText(this, "Error: No product data passed.", Toast.LENGTH_SHORT).show()
            finish() // Close the activity gracefully
        }
    }

    // Function to place a pre-order and save it to Firebase
    private fun placePreOrder(product: Product) {
        // Create a unique order ID (you can also use Firebase's push ID if needed)
        val orderId = "order" + System.currentTimeMillis()

        // Prepare the order data to be saved in Firebase
        val orderData = mapOf(
            "productId" to product.id,
            "productName" to product.name,
            "stock" to product.stock,
            "status" to "Pending"
        )

        // Reference to Firebase Realtime Database
        val preOrdersRef = FirebaseDatabase.getInstance().getReference("pre_orders")

        // Save the pre-order data under the new order ID
        preOrdersRef.child(orderId).setValue(orderData).addOnSuccessListener {
            Toast.makeText(this, "Pre-order placed for ${product.name}", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed to place pre-order: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
