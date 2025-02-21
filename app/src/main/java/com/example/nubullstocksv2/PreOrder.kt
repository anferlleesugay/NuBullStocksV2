package com.example.nubullstocksv2

data class PreOrder(
    val orderId: String = "",
    val customerName: String = "",
    val productName: String = "",
    val quantity: Int = 0,
    var status: String = "Pending"
)