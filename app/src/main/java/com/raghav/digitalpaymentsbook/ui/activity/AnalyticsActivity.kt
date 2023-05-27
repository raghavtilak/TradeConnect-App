package com.raghav.digitalpaymentsbook.ui.activity

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.raghav.digitalpaymentsbook.R
import com.raghav.digitalpaymentsbook.data.model.AnalyticsData
import com.raghav.digitalpaymentsbook.data.model.enums.AnalyticsType
import com.raghav.digitalpaymentsbook.data.network.RetrofitHelper
import com.raghav.digitalpaymentsbook.databinding.ActivityAnalyticsBinding
import com.raghav.digitalpaymentsbook.databinding.ActivityProfileBinding
import com.raghav.digitalpaymentsbook.ui.dialog.LoadingDialog
import com.raghav.digitalpaymentsbook.ui.fragment.DatePickerFragment
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate

class AnalyticsActivity : AppCompatActivity() {

    lateinit var binding : ActivityAnalyticsBinding

    val ordersData :MutableList<AnalyticsData> = mutableListOf()
    val salesData :MutableList<AnalyticsData> = mutableListOf()

    val loading = LoadingDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.chooseType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if(position == 1){
                    binding.chooseType.updateLayoutParams<LinearLayout.LayoutParams> {
                        this.marginStart = -50
                    }
                }else{
                    binding.chooseType.updateLayoutParams<LinearLayout.LayoutParams> {
                        this.marginStart = -80
                    }
                }
                when(position){
                    0->{
                    }
                    1->{

                    }
                    2->{

                    }
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }


        binding.calendarOrder.setOnClickListener {
            val newFragment: DialogFragment = DatePickerFragment { _, year, month, day ->
                    "$year-" +
                            (if (month.toString().length < 2) "0${month + 1}" else "${month + 1}") +
                            "-" + (if (day.toString().length < 2) "0${day}" else "$day")
            }
            newFragment.show(supportFragmentManager, "datePicker")
        }

        binding.calendarSales.setOnClickListener {
            val newFragment: DialogFragment = DatePickerFragment { _, year, month, day ->
                    "$year-" +
                            (if (month.toString().length < 2) "0${month + 1}" else "${month + 1}") +
                            "-" + (if (day.toString().length < 2) "0${day}" else "$day")
            }
            newFragment.show(supportFragmentManager, "datePicker")
        }

        binding.barGraphChoiceOrder.setOnClickListener {
            binding.barGraphChoiceOrder.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.colorSecondary))
            binding.circleGraphChoiceOrder.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.disabled))

        }

        binding.circleGraphChoiceOrder.setOnClickListener {
            binding.circleGraphChoiceOrder.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.colorSecondary))
            binding.barGraphChoiceOrder.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.disabled))

        }

        binding.barGraphChoiceSales.setOnClickListener {
            binding.barGraphChoiceSales.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.colorSecondary))
            binding.circleGraphChoiceSales.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.disabled))

        }

        binding.circleGraphChoiceSales.setOnClickListener {
            binding.circleGraphChoiceSales.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.colorSecondary))
            binding.barGraphChoiceSales.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.disabled))

        }




    }

    private fun onWeeklySelect(){
        loading.show(supportFragmentManager,"loading")

        lifecycleScope.launch {
            val job1 =
                async { RetrofitHelper.getInstance(this@AnalyticsActivity)
                    .getOrderAnalytics(
                        SimpleDateFormat("yyyy-MM-dd").format(LocalDate.now()),
                        AnalyticsType.week, 7,
                        true
                    ) }
            val job2 =
                async { RetrofitHelper.getInstance(this@AnalyticsActivity)
                    .getOrderAnalytics(
                        SimpleDateFormat("yyyy-MM-dd").format(LocalDate.now()),
                        AnalyticsType.week, 7,
                        false
                    ) }
            val (res1,res2) = awaitAll(job1,job2)
            if (res1.isSuccessful && res1.body() != null &&
                res2.isSuccessful && res2.body() != null) {

                ordersData.clear()
                ordersData.addAll(res1.body()!! + res2.body()!!)

                setOrderWeeklyBarGraph()
                setOrderWeeklyPieChart()

            } else {
                Log.d("TAG", "Couldn't load businness types")
            }
        }

    }

    private fun setOrderWeeklyBarGraph(){

    }
    private fun setOrderWeeklyPieChart(){

    }
}