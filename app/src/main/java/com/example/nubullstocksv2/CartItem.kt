package com.example.nubullstocksv2

data class CartItem(
    val productId: String = "",
    val productName: String = "",
    val productPrice: Double = 0.0,
    val productImageUrl: String = "",
    val quantity: Int = 0,
    val selectedSize: String = ""
)
