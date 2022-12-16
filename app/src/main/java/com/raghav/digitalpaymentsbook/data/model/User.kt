package com.raghav.digitalpaymentsbook.data.model

import com.raghav.digitalpaymentsbook.util.Constants

data class User(
    val name: String,
    val password: String,
    val phone: String,
    val address: String,
    val role:String,
    val shopName: String="",
    val id: Int = 0
)