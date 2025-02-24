package com.example.nubullstocksv2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class CartAdapter(
    private val context: Context,
    private val cartItems: List<CartItem>,
    private val onDelete: (String, Int) -> Unit // Function to handle item deletion
) : BaseAdapter() {

    override fun getCount(): Int {
        return cartItems.size
    }

    override fun getItem(position: Int): Any {
        return cartItems[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemView: View
        val holder: ViewHolder

        if (convertView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false)
            holder = ViewHolder()
            holder.productName = itemView.findViewById(R.id.productName)
            holder.productPrice = itemView.findViewById(R.id.productPrice)
            holder.productQuantity = itemView.findViewById(R.id.productQuantity)
            holder.productImage = itemView.findViewById(R.id.productImage)
            holder.deleteButton = itemView.findViewById(R.id.deleteProduct)

            itemView.tag = holder
        } else {
            itemView = convertView
            holder = itemView.tag as ViewHolder
        }

        val cartItem = cartItems[position]
        holder.productName.text = cartItem.productName
        holder.productPrice.text = "₱%.2f".format(cartItem.productPrice)
        holder.productQuantity.text = "Quantity: ${cartItem.quantity}"

        Glide.with(context)
            .load(cartItem.productImageUrl)
            .into(holder.productImage)

        // Handle item deletion
        holder.deleteButton.setOnClickListener {
            onDelete(cartItem.productId, position) // Pass productId and position
        }

        return itemView
    }

    private class ViewHolder {
        lateinit var productName: TextView
        lateinit var productPrice: TextView
        lateinit var productQuantity: TextView
        lateinit var productImage: ImageView
        lateinit var deleteButton: ImageButton
    }
}
