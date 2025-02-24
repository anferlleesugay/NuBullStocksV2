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
    private val cartKeys = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        cartListView = findViewById(R.id.cartListView)
        checkoutButton = findViewById(R.id.checkoutButton)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)

        fetchCartData()

        checkoutButton.setOnClickListener {
            if (cart.isNotEmpty()) {
                val totalPrice = calculateTotalPrice()
                val intent = Intent(this, PaymentActivity::class.java)
                intent.putExtra("TOTAL_PRICE", totalPrice)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
            }
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.menu_cart
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.menu_cart -> true
                R.id.menu_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun fetchCartData() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val cartRef = FirebaseDatabase.getInstance().getReference("cart/$userId")

            cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    cart.clear()
                    cartKeys.clear()

                    for (cartSnapshot in snapshot.children) {
                        val cartItem = cartSnapshot.getValue(CartItem::class.java)
                        cartItem?.let {
                            cart.add(it)
                            cartKeys.add(cartSnapshot.key!!)
                        }
                    }

                    if (cart.isNotEmpty()) {
                        val adapter = CartAdapter(this@CartActivity, cart, ::deleteCartItem)
                        cartListView.adapter = adapter
                        totalPriceTextView.text = "Total: ₱%.2f".format(calculateTotalPrice())
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

    private fun calculateTotalPrice(): Double {
        return cart.sumOf { it.productPrice * it.quantity }
    }

    private fun deleteCartItem(productId: String, position: Int) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val cartItemKey = cartKeys[position]
            val cartRef = FirebaseDatabase.getInstance().getReference("cart/$userId/$cartItemKey")

            cartRef.removeValue().addOnSuccessListener {
                Toast.makeText(this, "Item removed from cart", Toast.LENGTH_SHORT).show()
                cart.removeAt(position)
                cartKeys.removeAt(position)
                (cartListView.adapter as? CartAdapter)?.notifyDataSetChanged()
                totalPriceTextView.text = "Total: ₱%.2f".format(calculateTotalPrice())
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to remove item", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
