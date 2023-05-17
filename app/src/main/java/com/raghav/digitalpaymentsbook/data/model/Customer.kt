package com.raghav.digitalpaymentsbook.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import org.bson.types.ObjectId

@Parcelize
data class Customer(
    val customerName: String,
//    val password: String?,
    val customerPhone: String,
    val customerAddress: String?,
    @SerializedName("_id")
    val id: ObjectId?
) : Parcelable
