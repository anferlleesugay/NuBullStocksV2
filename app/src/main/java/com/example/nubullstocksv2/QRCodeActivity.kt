package com.example.nubullstocksv2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nubullstocksv2.databinding.ActivityQrCodeBinding

class QRCodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQrCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get total price from Intent
        val totalPrice = intent.getDoubleExtra("TOTAL_PRICE", 0.0)

        // Set the total price text
        binding.totalPriceText.text = "Total Price: ₱%.2f".format(totalPrice)

        // Back button click listener
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, PaymentActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
