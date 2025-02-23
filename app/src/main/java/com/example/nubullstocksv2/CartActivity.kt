package com.example.nubullstocksv2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartActivity : AppCompatActivity() {

    private lateinit var cartListView: ListView
    private lateinit var checkoutButton: Button
    private lateinit var totalPriceTextView: TextView
    private val cart = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Initialize views
        cartListView = findViewById(R.id.cartListView)
        checkoutButton = findViewById(R.id.checkoutButton)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)

        // Fetch cart data from Firebase
        fetchCartData()

        // Checkout button functionality
        checkoutButton.setOnClickListener {
            if (cart.isNotEmpty()) {
                // Proceed to checkout
                val intent = Intent(this, PaymentActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Bottom Navigation functionality
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.menu_cart
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                R.id.menu_cart -> true // Stay on the same screen

                R.id.menu_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                else -> false
            }
        }
    }

    // Fetch cart data from Firebase
    private fun fetchCartData() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val database = FirebaseDatabase.getInstance()
            val cartRef = database.getReference("cart/$userId")  // Reference to the user's cart in Firebase

            cartRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    cart.clear()  // Clear the cart before adding new data
                    for (cartSnapshot in snapshot.children) {
                        val cartItem = cartSnapshot.getValue(CartItem::class.java)
                        cartItem?.let {
                            cart.add(it)
                        }
                    }

                    if (cart.isNotEmpty()) {
                        // Set up the adapter for the ListView
                        val adapter = CartAdapter(this@CartActivity, cart)
                        cartListView.adapter = adapter

                        // Calculate and display total price
                        val totalPrice = calculateTotalPrice()
                        totalPriceTextView.text = "Total: ₱$totalPrice"
                    } else {
                        Toast.makeText(this@CartActivity, "Your cart is empty", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@CartActivity, "Error loading cart", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    // Calculate the total price of the items in the cart
    private fun calculateTotalPrice(): Double {
        var totalPrice = 0.0
        for (item in cart) {
            totalPrice += item.productPrice * item.quantity
        }
        return totalPrice
    }
}
