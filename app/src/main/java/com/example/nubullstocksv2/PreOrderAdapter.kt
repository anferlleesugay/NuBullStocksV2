package com.example.nubullstocksv2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class PreOrderAdapter(private val preOrders: List<PreOrder>) :
    RecyclerView.Adapter<PreOrderAdapter.PreOrderViewHolder>() {

    class PreOrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCustomerName: TextView = view.findViewById(R.id.tvCustomerName)
        val tvProductName: TextView = view.findViewById(R.id.tvProductName)
        val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreOrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pre_order, parent, false)
        return PreOrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: PreOrderViewHolder, position: Int) {
        val preOrder = preOrders[position]
        holder.tvCustomerName.text = preOrder.customerName
        holder.tvProductName.text = preOrder.productName
        holder.tvQuantity.text = "Quantity: ${preOrder.quantity}"
        holder.tvStatus.text = "Status: ${preOrder.status}"

        // Handle item click
        holder.itemView.setOnClickListener {
            // Show a toast instead of navigating to PreOrderDetailActivity
            Toast.makeText(holder.itemView.context, "Order ID: ${preOrder.orderId}", Toast.LENGTH_SHORT).show()
            // Or perform another action
        }
    }

    override fun getItemCount(): Int = preOrders.size
}
