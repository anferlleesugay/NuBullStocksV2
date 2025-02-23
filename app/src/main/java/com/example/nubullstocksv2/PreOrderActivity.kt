package com.example.nubullstocksv2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PreOrderActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var preOrderAdapter: PreOrderAdapter
    private lateinit var preOrderList: MutableList<PreOrder>
    private lateinit var emptyOrdersTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_order)

        // Initialize views
        recyclerView = findViewById(R.id.preOrderRecyclerView)
        emptyOrdersTextView = findViewById(R.id.tvEmptyOrders)

        // Initialize the preOrderList and adapter
        preOrderList = mutableListOf() // Initialize an empty list
        preOrderAdapter = PreOrderAdapter(preOrderList)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = preOrderAdapter

        // Check for empty state
        checkEmptyState()

        // Optionally, populate with dummy data for testing
        populateDummyData()

        // Handle the back button click
        val btnBack: ImageButton = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            // Navigate back to AdminDashboardActivity
            val intent = Intent(this, AdminDashboardActivity::class.java)
            startActivity(intent)
            finish() // Finish current activity to prevent returning here
        }

        // Notify adapter of changes
        preOrderAdapter.notifyDataSetChanged()
    }

    private fun populateDummyData() {
        preOrderList.add(
            PreOrder(
                orderId = "1",
                firstName = "John",
                lastName = "Doe",
                middleName = "",
                productId = "123",
                productName = "NU Sweater",
                status = "Pending",
                stock = 25,
                userEmail = "johndoe@example.com",
                pickupDateTime = ""
            )
        )
        // Add more dummy pre-orders as needed
        checkEmptyState()
    }

    private fun checkEmptyState() {
        if (preOrderList.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyOrdersTextView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyOrdersTextView.visibility = View.GONE
        }
    }
}
