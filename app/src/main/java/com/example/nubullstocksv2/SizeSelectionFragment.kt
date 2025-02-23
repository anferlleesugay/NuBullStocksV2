package com.example.nubullstocksv2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SizeSelectionFragment : DialogFragment() {

    private lateinit var sizeSpinner: Spinner
    private lateinit var confirmButton: Button
    private lateinit var product: Product
    private lateinit var sizeAdapter: ArrayAdapter<String> // Adapter for sizes

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_size_selection, container, false)

        sizeSpinner = view.findViewById(R.id.sizeSpinner)
        confirmButton = view.findViewById(R.id.confirmButton)

        // Get the product passed from the activity
        product = arguments?.getParcelable("PRODUCT") ?: return view

        // Set up the size spinner
        val sizes = listOf("XS", "S", "M", "L", "XL", "XXL")
        sizeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sizes)
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sizeSpinner.adapter = sizeAdapter

        // Confirm button click
        confirmButton.setOnClickListener {
            val selectedSize = sizeSpinner.selectedItem.toString()
            addToCart(product, selectedSize)
        }

        return view
    }

    // Function to add the selected size to the cart in Firebase Realtime Database
    private fun addToCart(product: Product, selectedSize: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val cartRef = FirebaseDatabase.getInstance().getReference("cart/$userId")
            val cartItemRef = cartRef.push() // Generate a unique key for the cart item

            val cartItem = CartItem(product.id, product.name, product.price, product.imageURL, 1, selectedSize)

            cartItemRef.setValue(cartItem).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Dismiss the fragment after successfully adding to the cart
                    dismiss()

                    // Show a toast confirming the addition
                    Toast.makeText(requireContext(), "Item added to cart with size $selectedSize", Toast.LENGTH_SHORT).show()
                } else {
                    // Show an error if the task fails
                    Toast.makeText(requireContext(), "Failed to add item to cart", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Show an error if the user is not logged in
            Toast.makeText(requireContext(), "Please log in to add items to the cart", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        // Create a new instance of the fragment and pass the product as an argument
        fun newInstance(product: Product): SizeSelectionFragment {
            val fragment = SizeSelectionFragment()
            val bundle = Bundle()
            bundle.putParcelable("PRODUCT", product)
            fragment.arguments = bundle
            return fragment
        }
    }
}
