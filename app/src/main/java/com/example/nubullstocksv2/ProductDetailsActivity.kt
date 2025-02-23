package com.example.nubullstocksv2

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var productImage: ImageView
    private lateinit var productName: TextView
    private lateinit var productStock: TextView
    private lateinit var addToCartButton: Button
    private lateinit var preOrderButton: Button
    private lateinit var sizeSpinner: Spinner // Size spinner
    private lateinit var product: Product // Assuming Product is a data class
    private lateinit var sizeAdapter: ArrayAdapter<String> // Adapter for sizes
    private lateinit var productPrice: TextView // Price TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        // Initialize views
        backButton = findViewById(R.id.btnBack)
        productImage = findViewById(R.id.productImage)
        productName = findViewById(R.id.productName)
        productStock = findViewById(R.id.productStock)
        addToCartButton = findViewById(R.id.addToCartButton)
        preOrderButton = findViewById(R.id.preOrderButton)
        sizeSpinner = findViewById(R.id.sizeSpinner)
        productPrice = findViewById(R.id.productPrice) // Initialize product price

        // Get the product data passed from the previous activity
        product = intent.getParcelableExtra<Product>("PRODUCT") ?: return finish()

        // Set the product details
        productName.text = product.name
        productStock.text = "In Stock: ${product.stock}"
        productPrice.text = "Price: ₱${product.price}" // Set the price
        Glide.with(this).load(product.imageURL).into(productImage)

        // Manage button states based on stock availability
        updateButtonStates()

        // Back button functionality
        backButton.setOnClickListener {
            onBackPressed()
        }

        // Add to cart functionality (navigate to SizeSelectionFragment)
        addToCartButton.setOnClickListener {
            if (product.stock > 0) {
                val sizeSelectionFragment = SizeSelectionFragment.newInstance(product)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, sizeSelectionFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        // Pre-order functionality
        preOrderButton.setOnClickListener {
            if (product.stock == 0) {
                placePreOrder(product)
            }
        }
    }

    private fun updateButtonStates() {
        if (product.stock > 0) {
            addToCartButton.isEnabled = true
            addToCartButton.alpha = 1f
            preOrderButton.isEnabled = false
            preOrderButton.alpha = 0.5f
        } else {
            addToCartButton.isEnabled = false
            addToCartButton.alpha = 0.5f
            preOrderButton.isEnabled = true
            preOrderButton.alpha = 1f
        }
    }

    // Function to place a pre-order in Firebase Realtime Database
    private fun placePreOrder(product: Product) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val orderId = "order_${System.currentTimeMillis()}"
            val preOrdersRef = FirebaseDatabase.getInstance().getReference("pre_orders/$userId/$orderId")

            // Fetch the user data from Firebase Realtime Database
            val userRef = FirebaseDatabase.getInstance().getReference("users/$userId")
            userRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // Retrieve user information from Firebase
                    val user = snapshot.getValue(User::class.java)

                    // Check if the user data is available
                    val firstName = user?.firstName ?: ""
                    val middleName = user?.middleName ?: "" // Optional middle name
                    val lastName = user?.lastName ?: ""

                    // Admin will set pickup date and time (removed from this message)
                    val orderData = mapOf(
                        "productId" to product.id,
                        "productName" to product.name,
                        "stock" to product.stock,
                        "status" to "Pending",
                        "firstName" to firstName,
                        "middleName" to middleName,
                        "lastName" to lastName
                    )

                    // Save the pre-order to Firebase Realtime Database
                    preOrdersRef.setValue(orderData).addOnSuccessListener {
                        Toast.makeText(this, "Pre-order placed. We will just email you when it is available.", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to place pre-order: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please log in to place a pre-order", Toast.LENGTH_SHORT).show()
        }
    }
}
