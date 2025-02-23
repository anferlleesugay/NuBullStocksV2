package com.example.nubullstocksv2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nubullstocksv2.databinding.ActivityBarcodeBinding

class BarcodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBarcodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle back button click to navigate to PaymentActivity
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, PaymentActivity::class.java)
            startActivity(intent)
            finish() // Close current activity
        }
    }
}
