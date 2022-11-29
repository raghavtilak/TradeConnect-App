package com.raghav.digitalpaymentsbook.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Retailer(
    val name: String,
    val password: String,
    val phone: String,
    val address: String,
    val shopName: String,
    val id: Int = 0
) : Parcelable
