package com.raghav.digitalpaymentsbook.ui.activity

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.data.*
import com.google.android.material.tabs.TabLayoutMediator
import com.raghav.digitalpaymentsbook.R
import com.raghav.digitalpaymentsbook.adapter.OrdersStatTabAdapterTabAdapter
import com.raghav.digitalpaymentsbook.adapter.SalesStatTabAdapter
import com.raghav.digitalpaymentsbook.data.model.AnalyticsData
import com.raghav.digitalpaymentsbook.data.model.enums.AnalyticsType
import com.raghav.digitalpaymentsbook.data.network.RetrofitHelper
import com.raghav.digitalpaymentsbook.databinding.ActivityAnalyticsBinding
import com.raghav.digitalpaymentsbook.ui.dialog.LoadingDialog
import com.raghav.digitalpaymentsbook.ui.fragment.DatePickerFragment
import com.raghav.digitalpaymentsbook.viewmodel.AnalyticsViewModel
import kotlinx.coroutines.*
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class AnalyticsActivity : AppCompatActivity() {

    lateinit var binding: ActivityAnalyticsBinding

    val loading = LoadingDialog()

    val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.d(
            "TAG",
            "ERROR=${throwable.message} , ${throwable.printStackTrace()}"
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel =  ViewModelProvider(this)[AnalyticsViewModel::class.java]

        binding.chooseType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("SimpleDateFormat")
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if (position == 1) {
                    binding.chooseType.updateLayoutParams<LinearLayout.LayoutParams> {
                        this.marginStart = -50
                    }
                } else {
                    binding.chooseType.updateLayoutParams<LinearLayout.LayoutParams> {
                        this.marginStart = -80
                    }
                }
                loading.show(supportFragmentManager, "loading")
                lifecycleScope.launch(handler) {


                    val job1: Deferred<Response<List<AnalyticsData>>> = async {
                        RetrofitHelper.getInstance(this@AnalyticsActivity)
                            .getOrderAnalytics(
                                SimpleDateFormat("yyyy-MM-dd").format(Date()),
                                when (position) {
                                    0 -> {
                                        AnalyticsType.week
                                    }
                                    1 -> {
                                        AnalyticsType.month
                                    }
                                    else -> {
                                        AnalyticsType.year
                                    }
                                },
                                when (position) {
                                    0 -> {
                                        7
                                    }
                                    1 -> {
                                        186
                                    }
                                    else -> {
                                        1068
                                    }
                                },
                                true
                            )
                    }
                    val job2: Deferred<Response<List<AnalyticsData>>> = async {
                        RetrofitHelper.getInstance(this@AnalyticsActivity)
                            .getOrderAnalytics(
                                SimpleDateFormat("yyyy-MM-dd").format(Date()),
                                when (position) {
                                    0 -> {
                                        AnalyticsType.week
                                    }
                                    1 -> {
                                        AnalyticsType.month
                                    }
                                    else -> {
                                        AnalyticsType.year
                                    }
                                },
                                when (position) {
                                    0 -> {
                                        7
                                    }
                                    1 -> {
                                        186
                                    }
                                    else -> {
                                        1068
                                    }
                                },
                                false
                            )
                    }


                    val (res1, res2) = awaitAll(job1, job2)
                    if (res1.isSuccessful && res1.body() != null &&
                        res2.isSuccessful && res2.body() != null
                    ) {

                        viewModel.ordersByUserData.value = res1.body()!!
                        viewModel.ordersByOtherData.value = res2.body()!!

                        loading.dismiss()
                    } else {
                        Log.d("TAG", "Couldn't load businness types")
                    }
                }


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        binding.calendarOrder.setOnClickListener{
            val newFragment: DialogFragment = DatePickerFragment { _, year, month, day ->
                "$year-" +
                        (if (month.toString().length < 2) "0${month + 1}" else "${month + 1}") +
                        "-" + (if (day.toString().length < 2) "0${day}" else "$day")
            }
            newFragment.show(supportFragmentManager, "datePicker")
        }

        binding.calendarSales.setOnClickListener{
            val newFragment: DialogFragment = DatePickerFragment { _, year, month, day ->
                "$year-" +
                        (if (month.toString().length < 2) "0${month + 1}" else "${month + 1}") +
                        "-" + (if (day.toString().length < 2) "0${day}" else "$day")
            }
            newFragment.show(supportFragmentManager, "datePicker")
        }

        binding.barGraphChoiceSales.setOnClickListener{
            binding.barGraphChoiceSales.imageTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorSecondary))
            binding.circleGraphChoiceSales.imageTintList =
                ColorStateList.valueOf(resources.getColor(R.color.disabled))
            binding.barGraphChoiceSales.isVisible = true
            binding.barGraphChoiceSales.isVisible = false
        }

        binding.circleGraphChoiceSales.setOnClickListener{
            binding.circleGraphChoiceSales.imageTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorSecondary))
            binding.barGraphChoiceSales.imageTintList =
                ColorStateList.valueOf(resources.getColor(R.color.disabled))
            binding.barGraphChoiceSales.isVisible = false
            binding.barGraphChoiceSales.isVisible = true
        }

        val adapterOrder = OrdersStatTabAdapterTabAdapter(this@AnalyticsActivity)
        binding.viewpagerOrder.adapter= adapterOrder
        TabLayoutMediator(binding.tabLayoutOrder,binding.viewpagerOrder){
                tab, position ->
            tab.text = when(position){
                0 -> "Received"
                else-> "Sent"
            }
        }.attach()

        val adapterSales = SalesStatTabAdapter(this@AnalyticsActivity)
        binding.viewpagerSales.adapter= adapterSales
        TabLayoutMediator(binding.tabLayoutSales,binding.viewpagerSales){
                tab, position ->
            tab.text = when(position){
                0 -> "Received"
                else-> "Created"
            }
        }.attach()

        binding.barGraphChoiceOrder.setOnClickListener{
            binding.barGraphChoiceOrder.imageTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorSecondary))
            binding.circleGraphChoiceOrder.imageTintList =
                ColorStateList.valueOf(resources.getColor(R.color.disabled))
            viewModel.ordersIsBarChartType.value = true
        }

        binding.circleGraphChoiceOrder.setOnClickListener{
            binding.circleGraphChoiceOrder.imageTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorSecondary))
            binding.barGraphChoiceOrder.imageTintList =
                ColorStateList.valueOf(resources.getColor(R.color.disabled))
            viewModel.ordersIsBarChartType.value = false
        }


    }

}