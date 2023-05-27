package com.raghav.digitalpaymentsbook.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raghav.digitalpaymentsbook.data.model.AnalyticsData
import com.raghav.digitalpaymentsbook.data.model.enums.AnalyticsType

class AnalyticsViewModel : ViewModel() {

    val ordersByUserData = MutableLiveData<List<AnalyticsData>>()
    val salesByUserData= MutableLiveData<List<AnalyticsData>>()

    val ordersByOtherData= MutableLiveData<List<AnalyticsData>>()
    val salesByOtherData= MutableLiveData<List<AnalyticsData>>()

    val salesIsBarChartType = MutableLiveData<Boolean>()
    val ordersIsBarChartType = MutableLiveData<Boolean>()

    var type = AnalyticsType.week

    init {
        ordersByUserData.value = mutableListOf()
        salesByUserData.value = mutableListOf()
        ordersByOtherData.value = mutableListOf()
        salesByOtherData.value = mutableListOf()

        salesIsBarChartType.value = true
        ordersIsBarChartType.value = true

    }
}