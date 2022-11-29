package com.raghav.digitalpaymentsbook.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    val customerId: Int,
    val retailerId: Int,
    val due: Int,
    val paid: Int,
    val amount: Int,
    val id: Int = 0
) : Parcelable