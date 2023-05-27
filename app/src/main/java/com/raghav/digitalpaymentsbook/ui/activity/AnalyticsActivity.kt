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
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.raghav.digitalpaymentsbook.R
import com.raghav.digitalpaymentsbook.data.model.AnalyticsData
import com.raghav.digitalpaymentsbook.data.model.enums.AnalyticsType
import com.raghav.digitalpaymentsbook.data.network.RetrofitHelper
import com.raghav.digitalpaymentsbook.databinding.ActivityAnalyticsBinding
import com.raghav.digitalpaymentsbook.databinding.ActivityProfileBinding
import com.raghav.digitalpaymentsbook.ui.dialog.LoadingDialog
import com.raghav.digitalpaymentsbook.ui.fragment.DatePickerFragment
import kotlinx.coroutines.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

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

//                setOrderWeeklyBarGraph()
                setOrderWeeklyPieChart()

            } else {
                Log.d("TAG", "Couldn't load businness types")
            }
        }

    }

//    private fun setOrderWeeklyBarGraph(){
//
//        binding.barChartOrder.invalidate()
////        binding.xlabel.text = "Week days"
//
//        //index is weekday, list contains data to fetch the words of that day
//        val listOfWords: MutableMap<Int, MutableList<String>> = mutableMapOf()
//        val entries: MutableList<BarEntry> = mutableListOf()
//        val weekdaysEntries: MutableList<String> = mutableListOf()
//
//        lifecycleScope.launch {
//
//            ordersData.forEachIndexed { i, it ->
//                entries.add(BarEntry(i.toFloat(), it.count.toFloat()))
//                weekdaysEntries.add(sdf4.format(cal.time))
//            }
//
//            val dataSets: MutableList<IBarDataSet?> = mutableListOf()
//            val set1 = BarDataSet(entries, "Income")
//            set1.valueTextSize = 10f
//            set1.setColors(*ColorTemplate.PASTEL_COLORS)
//            dataSets.add(set1)
//
////customization
//            binding.barChartOrder.setTouchEnabled(true)
//            //                binding.barChartOrder.setDragEnabled(true);
//            binding.barChartOrder.setScaleEnabled(false)
//            binding.barChartOrder.setPinchZoom(false)
//            binding.barChartOrder.setDrawGridBackground(false)
//
//////to hide background lines
//            binding.barChartOrder.xAxis.setDrawGridLines(false)
//            binding.barChartOrder.axisLeft.setDrawGridLines(false)
//            binding.barChartOrder.axisRight.setDrawGridLines(false)
//
////to hide right Y and top X border
//            val rightYAxis: YAxis = binding.barChartOrder.axisRight
//            rightYAxis.isEnabled = false
//            val xAxis: XAxis = binding.barChartOrder.xAxis
//            xAxis.granularity = 1f
//            xAxis.isEnabled = true
//            xAxis.setDrawGridLines(false)
//            xAxis.position = XAxis.XAxisPosition.BOTTOM
//
//            //String setter in x-Axis
//            binding.barChartOrder.xAxis.valueFormatter = IndexAxisValueFormatter(weekdaysEntries)
//            binding.barChartOrder.axisLeft.spaceBottom = 0f
//            binding.barChartOrder.axisLeft.valueFormatter = object : ValueFormatter() {
//                override fun getFormattedValue(value: Float): String {
//                    return value.toInt().toString()
//                }
//            }
//            val data = BarData(dataSets)
//            data.setValueFormatter(object : ValueFormatter() {
//                override fun getFormattedValue(value: Float): String {
//                    return value.toInt().toString()
//                }
//            })
//            binding.barChartOrder.data = data
//            binding.barChartOrder.animateX(500)
//            binding.barChartOrder.animateY(1000)
//            binding.barChartOrder.legend.isEnabled = false
//            binding.barChartOrder.description.isEnabled = false
//            binding.barChartOrder.setOnChartValueSelectedListener(object :
//                OnChartValueSelectedListener {
//                override fun onValueSelected(e: Entry, h: Highlight) {
//
//                    lifecycleScope.launch {
//
//                        var list: List<DataModel> = mutableListOf()
//                        val dao: DataDao = getInstance(requireActivity()).dataDao()
//                        val values: MutableList<String>? = listOfWords[e.x.toInt()]
//
//                        var job: Deferred<Unit>? = null
//                        if (values != null) {
//                            job = async(Dispatchers.IO) {
//                                list = dao.getListOfWords(
//                                    values[0],
//                                    values[1],
//                                    values[2]
//                                )
//                            }
//                        }
//                        job?.await()
//                        val addBottomDialogFragment =
//                            KeywordsListBottomSheetFragment(list)
//                        addBottomDialogFragment.show(
//                            requireActivity().supportFragmentManager,
//                            "TAG"
//                        )
//                    }
//                }
//
//                override fun onNothingSelected() {}
//            })
//        }
//    }
    private fun setOrderWeeklyPieChart(){

    }
}