package com.raghav.digitalpaymentsbook.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import org.bson.types.ObjectId
import java.util.*

@Parcelize
data class SellItem(
    @SerializedName("_id")
    val id: ObjectId,
    val toRetailer: Retailer,
    val isCustomerSale: Boolean,
    val totalPrice: Int,
    val paid: Int,
    val due: Int,
    val date: Date,
    val batches: List<Batch>
) : Parcelable
