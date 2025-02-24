package com.example.nubullstocksv2

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class PaymentActivity : AppCompatActivity() {

    private var totalPrice: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val cardOverTheCounter = findViewById<MaterialCardView>(R.id.cardOverTheCounter)
        val cardQRPayment = findViewById<MaterialCardView>(R.id.cardQRPayment)

        // Get total price from CartActivity
        totalPrice = intent.getDoubleExtra("TOTAL_PRICE", 0.0)

        // Navigate back to CartActivity (Keep total price)
        btnBack.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            intent.putExtra("TOTAL_PRICE", totalPrice) // Keep total price
            startActivity(intent)
            finish()
        }

        // Navigate to BarcodeActivity for Over-the-Counter Payment
        cardOverTheCounter.setOnClickListener {
            val intent = Intent(this, BarcodeActivity::class.java)
            intent.putExtra("TOTAL_PRICE", totalPrice) // Pass total price
            startActivity(intent)
        }

        // Navigate to QRCodeActivity for QR Payment (GCash/PayMaya)
        cardQRPayment.setOnClickListener {
            val intent = Intent(this, QRCodeActivity::class.java)
            intent.putExtra("TOTAL_PRICE", totalPrice) // Pass total price
            startActivity(intent)
        }
    }
}
