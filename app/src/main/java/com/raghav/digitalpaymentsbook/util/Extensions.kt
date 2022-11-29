package com.raghav.digitalpaymentsbook.util

import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Rect
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.gson.Gson
import com.raghav.digitalpaymentsbook.data.model.Customer
import com.raghav.digitalpaymentsbook.data.model.Retailer
import com.raghav.digitalpaymentsbook.util.Constants.CUSTOMER_STR
import com.raghav.digitalpaymentsbook.util.Constants.RETAILER_STR

/**
 * Call this method (in onActivityCreated or later) to set
 * the width of the dialog to a percentage of the current
 * screen width.
 */
fun DialogFragment.setWidthPercent(percentage: Int) {
    val percent = percentage.toFloat() / 100
    val dm = Resources.getSystem().displayMetrics
    val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
    val percentWidth = rect.width() * percent
    dialog?.window?.setLayout(percentWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
}

fun DialogFragment.setupWidthToMatchParent() {
    dialog?.window?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
}

fun SharedPreferences.add(customer: Customer){
    with(this.edit()) {
        val g = Gson()
        val json = g.toJson(customer)
        putString(CUSTOMER_STR, json)
        apply()
    }
}

fun SharedPreferences.add(retailer: Retailer){
    with(this.edit()) {
        val g = Gson()
        val json = g.toJson(retailer)
        putString(RETAILER_STR, json)
        apply()
    }
}

fun SharedPreferences.getCustomer(): Customer {
    val json = this.getString(CUSTOMER_STR, "")
    val g = Gson()
    return g.fromJson(json, Customer::class.java)
}

fun SharedPreferences.getRetailer(): Retailer {
    val json = this.getString(RETAILER_STR, "")
    val g = Gson()
    return g.fromJson(json, Retailer::class.java)
}

fun SharedPreferences.userExist(): Boolean {
    return !(this.getString(CUSTOMER_STR,"").equals("")
        && this.getString(RETAILER_STR,"").equals(""))
}

fun SharedPreferences.typeOfUser(): String {
    return if(this.getString(CUSTOMER_STR,"").equals("")) CUSTOMER_STR
    else if(this.getString(RETAILER_STR,"").equals("")) RETAILER_STR
    else ""
}