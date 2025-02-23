package com.example.nubullstocksv2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nubullstocksv2.databinding.ActivityAdminDashboardBinding

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Navigate to Add Product screen
        binding.btnAddProduct.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }

        // Navigate to Product List screen
        binding.btnViewProducts.setOnClickListener {
            startActivity(Intent(this, ProductListActivity::class.java))
        }

        // Navigate to Pre-Orders screen
        binding.btnViewOrders.setOnClickListener {
            startActivity(Intent(this, PreOrderActivity::class.java))
        }

        // Logout functionality
        binding.btnLogout.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
