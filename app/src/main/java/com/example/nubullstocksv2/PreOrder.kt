package com.example.nubullstocksv2

data class PreOrder(
    val orderId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val middleName: String = "",
    val productId: String = "",
    val productName: String = "",
    val status: String = "",
    val stock: Int = 0,
    val userEmail: String = "",
    val pickupDateTime: String = ""
)
