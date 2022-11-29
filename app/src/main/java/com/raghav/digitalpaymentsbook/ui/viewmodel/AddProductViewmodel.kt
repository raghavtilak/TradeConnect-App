package com.raghav.digitalpaymentsbook.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raghav.digitalpaymentsbook.data.model.Product

class AddProductViewmodel : ViewModel() {

    val list = MutableLiveData<MutableList<Product>>()
    val totalPrice = MutableLiveData<Int>()
    val paid= MutableLiveData<Int>()
    val due= MutableLiveData<Int>()

    init {
        list.value = mutableListOf()
        totalPrice.value=0
        paid.value=0
        due.value=0
    }

    fun addProduct(product: Product){
        list.value?.add(product)
        totalPrice.value=totalPrice.value?.plus(product.price)
        paid.value = paid.value?.plus(product.paid)
        due.value= totalPrice.value?.minus(paid.value!!)!!
        list.value=list.value
    }
    fun removeProduct(product: Product){
        list.value?.remove(product)
        totalPrice.value=totalPrice.value?.minus(product.price)
        paid.value = paid.value?.minus(product.paid)
        due.value= totalPrice.value?.minus(paid.value!!)!!
        list.value=list.value
    }
}