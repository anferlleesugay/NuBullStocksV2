package com.example.nubullstocksv2

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nubullstocksv2.databinding.ActivityPreOrderBinding
import com.google.firebase.database.*

class PreOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPreOrderBinding
    private lateinit var database: DatabaseReference
    private lateinit var preOrderAdapter: PreOrderAdapter
    private val preOrderList = mutableListOf<PreOrder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Update the database reference to the correct path
        database = FirebaseDatabase.getInstance().getReference("pre_orders/pre_orders")

        setupRecyclerView()
        fetchPreOrders()
    }

    private fun setupRecyclerView() {
        preOrderAdapter = PreOrderAdapter(preOrderList)
        binding.recyclerViewPreOrders.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewPreOrders.adapter = preOrderAdapter
    }

    private fun fetchPreOrders() {
        binding.progressBar.visibility = View.VISIBLE
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                preOrderList.clear()
                for (preOrderSnapshot in snapshot.children) {
                    val preOrder = preOrderSnapshot.getValue(PreOrder::class.java)
                    preOrder?.let {
                        preOrderList.add(it)
                    }
                }

                // Check if data is being fetched correctly
                Log.d("PreOrderActivity", "Fetched PreOrders: $preOrderList")

                preOrderAdapter.notifyDataSetChanged()

                // Toggle visibility of RecyclerView and empty text
                if (preOrderList.isEmpty()) {
                    binding.tvEmptyOrders.visibility = View.VISIBLE
                    binding.recyclerViewPreOrders.visibility = View.GONE
                } else {
                    binding.tvEmptyOrders.visibility = View.GONE
                    binding.recyclerViewPreOrders.visibility = View.VISIBLE
                }

                binding.progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PreOrderActivity, "Failed to fetch pre-orders", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
        })
    }
}
