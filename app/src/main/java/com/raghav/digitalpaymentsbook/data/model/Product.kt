package com.raghav.digitalpaymentsbook.data.model

data class Product(
    val name: String,
    val price: Int,
    val paid: Int = 0
)