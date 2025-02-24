package com.example.nubullstocksv2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nubullstocksv2.databinding.ActivityBarcodeBinding

class BarcodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBarcodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get total price from intent and display it
        val totalPrice = intent.getDoubleExtra("TOTAL_PRICE", 0.0)
        binding.totalPriceText.text = "Total Price: ₱%.2f".format(totalPrice)

        // Handle back button click to finish the activity and return to PaymentActivity
        binding.btnBack.setOnClickListener {
            finish() // Close current activity and go back
        }
    }
}
