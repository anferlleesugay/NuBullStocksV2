package com.example.nubullstocksv2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nubullstocksv2.databinding.ItemProductBinding

class ProductAdapter(
    private var productList: MutableList<Product>, // Changed to MutableList
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product, onProductClick: (Product) -> Unit) {
            binding.tvProductName.text = product.name
            binding.tvProductPrice.text = "₱${product.price}"

            // Load product image using Glide
            Glide.with(binding.root.context)
                .load(product.imageURL)
                .placeholder(R.drawable.placeholder_image) // Add a placeholder image
                .error(R.drawable.error_image) // Add an error image if loading fails
                .into(binding.productImageView)

            binding.root.setOnClickListener { onProductClick(product) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productList[position], onProductClick)
    }

    override fun getItemCount(): Int = productList.size

    // Update the list for search functionality
    fun updateList(newList: List<Product>) {
        productList.clear() // Clears the current list
        productList.addAll(newList) // Adds the new list
        notifyDataSetChanged() // Notify the adapter of the changes
    }
}
