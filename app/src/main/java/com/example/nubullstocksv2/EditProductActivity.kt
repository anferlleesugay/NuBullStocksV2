package com.example.nubullstocksv2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class EditProductActivity : AppCompatActivity() {

    private lateinit var etProductName: EditText
    private lateinit var etProductPrice: EditText
    private lateinit var etProductStock: EditText
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button

    private var productId: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        val btnBack: ImageButton = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, ProductListActivity::class.java)
            startActivity(intent)
            finish()
        }


        etProductName = findViewById(R.id.etProductName)
        etProductPrice = findViewById(R.id.etProductPrice)
        etProductStock = findViewById(R.id.etProductStock)
        btnUpdate = findViewById(R.id.btnUpdateProduct)
        btnDelete = findViewById(R.id.btnDeleteProduct)

        productId = intent.getStringExtra("PRODUCT_ID")

        if (productId.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid Product", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnUpdate.setOnClickListener { updateProduct() }
        btnDelete.setOnClickListener { deleteProduct() }
    }

    private fun updateProduct() {
        val name = etProductName.text.toString()
        val price = etProductPrice.text.toString().toDoubleOrNull()
        val stock = etProductStock.text.toString().toIntOrNull()

        if (name.isEmpty() || price == null || stock == null) {
            Toast.makeText(this, "Please enter valid details", Toast.LENGTH_SHORT).show()
            return
        }

        val productRef = FirebaseDatabase.getInstance().getReference("products").child(productId!!)
        val updatedProduct = mapOf(
            "name" to name,
            "price" to price,
            "stock" to stock
        )

        productRef.updateChildren(updatedProduct).addOnSuccessListener {
            Toast.makeText(this, "Product updated!", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener { e ->
            Log.e("EditProductActivity", "Failed to update product", e)
        }
    }

    private fun deleteProduct() {
        val productRef = FirebaseDatabase.getInstance().getReference("products").child(productId!!)
        productRef.removeValue().addOnSuccessListener {
            Toast.makeText(this, "Product deleted!", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener { e ->
            Log.e("EditProductActivity", "Failed to delete product", e)
        }

    }

}
