package com.example.nubullstocksv2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.example.nubullstocksv2.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var productAdapter: ProductAdapter
    private val productList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up RecyclerView
        productAdapter = ProductAdapter(productList) { product ->
            // Handle product click: Open ProductDetailsActivity
            val intent = Intent(this, ProductDetailsActivity::class.java)
            intent.putExtra("PRODUCT", product) // Pass the product to the next activity
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = productAdapter

        // Fetch products from Firebase Realtime Database
        fetchProductsFromFirebase()
    }

    private fun fetchProductsFromFirebase() {
        val database = FirebaseDatabase.getInstance()
        val productsRef = database.getReference("products")

        productsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()  // Clear the list before adding new data
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let {
                        productList.add(it)
                    }
                }
                productAdapter.notifyDataSetChanged() // Notify adapter that data has changed
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}
