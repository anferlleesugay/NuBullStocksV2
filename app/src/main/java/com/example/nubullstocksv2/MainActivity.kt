package com.example.nubullstocksv2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.nubullstocksv2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var productAdapter: ProductAdapter
    private val productList = mutableListOf<Product>()
    private val filteredProductList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up RecyclerView
        productAdapter = ProductAdapter(filteredProductList) { product ->
            val intent = Intent(this, ProductDetailsActivity::class.java)
            intent.putExtra("PRODUCT", product)
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = productAdapter

        // Fetch products from Firebase Realtime Database
        fetchProductsFromFirebase()

        // SearchView listener for filtering products
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterProducts(newText)
                return true
            }
        })

        // Set up bottom navigation
        binding.bottomNavigation.selectedItemId = R.id.menu_home
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    binding.recyclerView.visibility = android.view.View.VISIBLE
                    binding.searchView.visibility = android.view.View.VISIBLE
                }
                R.id.menu_cart -> {
                    val intent = Intent(this, CartActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0) // Disable animation
                    finish()
                }
                R.id.menu_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0) // Disable animation
                    finish()
                }
            }
            true
        }

    }

    private fun fetchProductsFromFirebase() {
        val database = FirebaseDatabase.getInstance()
        val productsRef = database.getReference("products")

        productsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let {
                        productList.add(it)
                    }
                }
                filteredProductList.clear()
                filteredProductList.addAll(productList)
                productAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun filterProducts(query: String?) {
        filteredProductList.clear()
        if (query.isNullOrEmpty()) {
            filteredProductList.addAll(productList)
        } else {
            val queryLower = query.lowercase()
            for (product in productList) {
                if (product.name.lowercase().contains(queryLower)) {
                    filteredProductList.add(product)
                }
            }
        }
        productAdapter.notifyDataSetChanged()
    }
}
