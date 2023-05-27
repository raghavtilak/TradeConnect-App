package com.raghav.digitalpaymentsbook.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.raghav.digitalpaymentsbook.data.model.AnalyticsData
import com.raghav.digitalpaymentsbook.data.model.enums.AnalyticsType
import com.raghav.digitalpaymentsbook.databinding.FragmentOrderStatBinding
import com.raghav.digitalpaymentsbook.viewmodel.AnalyticsViewModel
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class OrderStatFragment(private val isSentTypeFrag : Boolean) : Fragment() {


    var _binding: FragmentOrderStatBinding? = null
    val binding: FragmentOrderStatBinding
        get() = _binding!!


    lateinit var viewModel : AnalyticsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderStatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel =  ViewModelProvider(this)[AnalyticsViewModel::class.java]

        viewModel.ordersIsBarChartType.observe(viewLifecycleOwner){
            binding.barChartOrder.isVisible = it
            binding.pieChartOrder.isVisible = !it
        }

        if(isSentTypeFrag){
            viewModel.ordersByUserData.observe(viewLifecycleOwner){
                when(viewModel.type){
                    AnalyticsType.week -> {
                        setOrderWeeklyBarGraph(it)
                        setOrderWeeklyPieChart(it)
                    }
                    AnalyticsType.month -> {
                        setOrderMonthlyBarGraph(it)
                        setOrderMonthlyPieChart(it)
                    }
                    AnalyticsType.year -> {
                        setOrderYearlyBarGraph(it)
                        setOrderYearlyPieChart(it)
                    }
                }
            }
        }else{
            viewModel.ordersByOtherData.observe(viewLifecycleOwner){
                when(viewModel.type){
                    AnalyticsType.week -> {
                        setOrderWeeklyBarGraph(it)
                        setOrderWeeklyPieChart(it)
                    }
                    AnalyticsType.month -> {
                        setOrderMonthlyBarGraph(it)
                        setOrderMonthlyPieChart(it)
                    }
                    AnalyticsType.year -> {
                        setOrderYearlyBarGraph(it)
                        setOrderYearlyPieChart(it)
                    }
                }
            }
        }
        

        


    }

    private fun setOrderWeeklyBarGraph(data : List<AnalyticsData>) {

        binding.barChartOrder.invalidate()
//        binding.xlabel.text = "Week days"

        val entries: MutableList<BarEntry> = mutableListOf()
        val weekdaysEntries: MutableList<String> = mutableListOf()

        lifecycleScope.launch {

            val cal: Calendar = Calendar.getInstance()
            // get starting date
            cal.add(Calendar.DAY_OF_YEAR, -7)
            val sdf4: DateFormat = SimpleDateFormat("EEE")

            data.forEachIndexed { i, it ->
                cal.add(Calendar.DAY_OF_YEAR, 1)
                entries.add(BarEntry(i.toFloat(), it.count.toFloat()))
                weekdaysEntries.add(sdf4.format(cal.time))
            }

            val dataSets: MutableList<IBarDataSet?> = mutableListOf()
            val set1 = BarDataSet(entries, "Income")
            set1.valueTextSize = 10f
            set1.setColors(*ColorTemplate.PASTEL_COLORS)
            dataSets.add(set1)

//customization
            binding.barChartOrder.setTouchEnabled(true)
            //                binding.barChartOrder.setDragEnabled(true);
            binding.barChartOrder.setScaleEnabled(false)
            binding.barChartOrder.setPinchZoom(false)
            binding.barChartOrder.setDrawGridBackground(false)

////to hide background lines
            binding.barChartOrder.xAxis.setDrawGridLines(false)
            binding.barChartOrder.axisLeft.setDrawGridLines(false)
            binding.barChartOrder.axisRight.setDrawGridLines(false)

//to hide right Y and top X border
            val rightYAxis: YAxis = binding.barChartOrder.axisRight
            rightYAxis.isEnabled = false
            val xAxis: XAxis = binding.barChartOrder.xAxis
            xAxis.granularity = 1f
            xAxis.isEnabled = true
            xAxis.setDrawGridLines(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM

            //String setter in x-Axis
            binding.barChartOrder.xAxis.valueFormatter = IndexAxisValueFormatter(weekdaysEntries)
            binding.barChartOrder.axisLeft.spaceBottom = 0f
            binding.barChartOrder.axisLeft.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }
            val data = BarData(dataSets)
            data.setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            })
            binding.barChartOrder.data = data
            binding.barChartOrder.animateX(500)
            binding.barChartOrder.animateY(1000)
            binding.barChartOrder.legend.isEnabled = false
            binding.barChartOrder.description.isEnabled = false
            binding.barChartOrder.setOnChartValueSelectedListener(object :
                OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry, h: Highlight) {

                    lifecycleScope.launch {


                    }
                }

                override fun onNothingSelected() {}
            })
        }
    }

    private fun setOrderWeeklyPieChart(data : List<AnalyticsData>) {

        binding.pieChartOrder.invalidate()
//        binding.xlabel.text = "Week days"

        val entries: MutableList<PieEntry> = mutableListOf()
        lifecycleScope.launch {

            val sdf4: DateFormat = SimpleDateFormat("EEE")
            val cal: Calendar = Calendar.getInstance()
            // get starting date
            cal.add(Calendar.DAY_OF_YEAR, -7)

            // loop adding one day in each iteration
            data.forEachIndexed { i, it ->
                cal.add(Calendar.DAY_OF_YEAR, 1)
                if (it.count != 0) {
                    entries.add(PieEntry(it.count.toFloat(), sdf4.format(cal.time), i))
                }
            }

            val set = PieDataSet(entries, "Election Results")
            set.valueTextSize = 10f
            set.setColors(*ColorTemplate.PASTEL_COLORS)
            set.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }
            val data = PieData(set)
            binding.pieChartOrder.data = data


//customization
            binding.pieChartOrder.setTouchEnabled(true)
            binding.pieChartOrder.animateX(500)
            binding.pieChartOrder.animateY(1000)
            binding.pieChartOrder.legend.isEnabled = false
            binding.pieChartOrder.description.isEnabled = false
            binding.pieChartOrder.setOnChartValueSelectedListener(object :
                OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry, h: Highlight) {

                }

                override fun onNothingSelected() {}
            })
        }
    }

    private fun setOrderMonthlyBarGraph(data : List<AnalyticsData>) {

        binding.barChartOrder.invalidate()
//        binding.xlabel.text = "Week days"

        val entries: MutableList<BarEntry> = mutableListOf()
        val monthsEntries: MutableList<String> = mutableListOf()

        lifecycleScope.launch {

            val sdf2: DateFormat = SimpleDateFormat("MMMM")
            val cal: Calendar = Calendar.getInstance()
            // get starting date
            cal.add(Calendar.MONTH, -6)

            data.forEachIndexed { i, it ->
                cal.add(Calendar.MONTH, 1)
                entries.add(BarEntry(i.toFloat(), it.count.toFloat()))
                monthsEntries.add(sdf2.format(cal.time).substring(0, 3))
            }

            val dataSets: MutableList<IBarDataSet?> = mutableListOf()
            val set1 = BarDataSet(entries, "Income")
            set1.valueTextSize = 10f
            set1.setColors(*ColorTemplate.PASTEL_COLORS)
            dataSets.add(set1)

//customization
            binding.barChartOrder.setTouchEnabled(true)
            //                binding.barChartOrder.setDragEnabled(true);
            binding.barChartOrder.setScaleEnabled(false)
            binding.barChartOrder.setPinchZoom(false)
            binding.barChartOrder.setDrawGridBackground(false)

////to hide background lines
            binding.barChartOrder.xAxis.setDrawGridLines(false)
            binding.barChartOrder.axisLeft.setDrawGridLines(false)
            binding.barChartOrder.axisRight.setDrawGridLines(false)

//to hide right Y and top X border
            val rightYAxis: YAxis = binding.barChartOrder.axisRight
            rightYAxis.isEnabled = false
            val xAxis: XAxis = binding.barChartOrder.xAxis
            xAxis.granularity = 1f
            xAxis.isEnabled = true
            xAxis.setDrawGridLines(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM

            //String setter in x-Axis
            binding.barChartOrder.xAxis.valueFormatter = IndexAxisValueFormatter(monthsEntries)
            binding.barChartOrder.axisLeft.spaceBottom = 0f
            binding.barChartOrder.axisLeft.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }
            val data = BarData(dataSets)
            data.setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            })
            binding.barChartOrder.data = data
            binding.barChartOrder.animateX(500)
            binding.barChartOrder.animateY(1000)
            binding.barChartOrder.legend.isEnabled = false
            binding.barChartOrder.description.isEnabled = false
            binding.barChartOrder.setOnChartValueSelectedListener(object :
                OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry, h: Highlight) {

                    lifecycleScope.launch {


                    }
                }

                override fun onNothingSelected() {}
            })
        }
    }

    private fun setOrderMonthlyPieChart(data : List<AnalyticsData>) {

        binding.pieChartOrder.invalidate()
//        binding.xlabel.text = "Week days"

        val entries: MutableList<PieEntry> = mutableListOf()
        //index is weekday, list contains data to fetch the words of that day
        val listOfWords: MutableMap<Int, MutableList<String>> = mutableMapOf()
        lifecycleScope.launch {

            val sdf2: DateFormat = SimpleDateFormat("MMMM")
            val sdf3: DateFormat = SimpleDateFormat("yyyy")
            val cal: Calendar = Calendar.getInstance()
            // get starting date
            cal.add(Calendar.MONTH, -6)

            // loop adding one day in each iteration
            data.forEachIndexed { i, it ->
                cal.add(Calendar.MONTH, 1)
                if (it.count != 0) {
                    entries.add(PieEntry(it.count.toFloat(), sdf2.format(cal.time), i))
                }
            }

            val set = PieDataSet(entries, "Election Results")
            set.valueTextSize = 10f
            set.setColors(*ColorTemplate.PASTEL_COLORS)
            set.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }
            val data = PieData(set)
            binding.pieChartOrder.data = data


//customization
            binding.pieChartOrder.setTouchEnabled(true)
            binding.pieChartOrder.animateX(500)
            binding.pieChartOrder.animateY(1000)
            binding.pieChartOrder.legend.isEnabled = false
            binding.pieChartOrder.description.isEnabled = false
            binding.pieChartOrder.setOnChartValueSelectedListener(object :
                OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry, h: Highlight) {

                }

                override fun onNothingSelected() {}
            })
        }
    }

    private fun setOrderYearlyBarGraph(data : List<AnalyticsData>) {

        binding.barChartOrder.invalidate()
//        binding.xlabel.text = "Week days"

        //index is weekday, list contains data to fetch the words of that day
        val listOfWords: MutableMap<Int, MutableList<String>> = mutableMapOf()
        val entries: MutableList<BarEntry> = mutableListOf()
        val yearEntries: MutableList<String> = mutableListOf()

        lifecycleScope.launch {

            val sdf3: DateFormat = SimpleDateFormat("yyyy")
            val cal: Calendar = Calendar.getInstance()
            // get starting date
            cal.add(Calendar.YEAR, -5)

            data.forEachIndexed { i, it ->
                cal.add(Calendar.DAY_OF_YEAR, 1)
                entries.add(BarEntry(i.toFloat(), it.count.toFloat()))
                yearEntries.add(sdf3.format(cal.time))
            }

            val dataSets: MutableList<IBarDataSet?> = mutableListOf()
            val set1 = BarDataSet(entries, "Income")
            set1.valueTextSize = 10f
            set1.setColors(*ColorTemplate.PASTEL_COLORS)
            dataSets.add(set1)

//customization
            binding.barChartOrder.setTouchEnabled(true)
            //                binding.barChartOrder.setDragEnabled(true);
            binding.barChartOrder.setScaleEnabled(false)
            binding.barChartOrder.setPinchZoom(false)
            binding.barChartOrder.setDrawGridBackground(false)

////to hide background lines
            binding.barChartOrder.xAxis.setDrawGridLines(false)
            binding.barChartOrder.axisLeft.setDrawGridLines(false)
            binding.barChartOrder.axisRight.setDrawGridLines(false)

//to hide right Y and top X border
            val rightYAxis: YAxis = binding.barChartOrder.axisRight
            rightYAxis.isEnabled = false
            val xAxis: XAxis = binding.barChartOrder.xAxis
            xAxis.granularity = 1f
            xAxis.isEnabled = true
            xAxis.setDrawGridLines(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM

            //String setter in x-Axis
            binding.barChartOrder.xAxis.valueFormatter = IndexAxisValueFormatter(yearEntries)
            binding.barChartOrder.axisLeft.spaceBottom = 0f
            binding.barChartOrder.axisLeft.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }
            val data = BarData(dataSets)
            data.setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            })
            binding.barChartOrder.data = data
            binding.barChartOrder.animateX(500)
            binding.barChartOrder.animateY(1000)
            binding.barChartOrder.legend.isEnabled = false
            binding.barChartOrder.description.isEnabled = false
            binding.barChartOrder.setOnChartValueSelectedListener(object :
                OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry, h: Highlight) {

                    lifecycleScope.launch {


                    }
                }

                override fun onNothingSelected() {}
            })
        }
    }

    private fun setOrderYearlyPieChart(data : List<AnalyticsData>) {

        binding.pieChartOrder.invalidate()
//        binding.xlabel.text = "Week days"

        val entries: MutableList<PieEntry> = mutableListOf()
        lifecycleScope.launch {

            val sdf3: DateFormat = SimpleDateFormat("yyyy")
            val cal: Calendar = Calendar.getInstance()
            // get starting date
            cal.add(Calendar.YEAR, -3)

            // loop adding one day in each iteration
            data.forEachIndexed { i, it ->
                cal.add(Calendar.YEAR, 1)
                if (it.count != 0) {
                    entries.add(PieEntry(it.count.toFloat(), sdf3.format(cal.time), i))
                }
            }

            val set = PieDataSet(entries, "Election Results")
            set.valueTextSize = 10f
            set.setColors(*ColorTemplate.PASTEL_COLORS)
            set.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }
            val data = PieData(set)
            binding.pieChartOrder.data = data


//customization
            binding.pieChartOrder.setTouchEnabled(true)
            binding.pieChartOrder.animateX(500)
            binding.pieChartOrder.animateY(1000)
            binding.pieChartOrder.legend.isEnabled = false
            binding.pieChartOrder.description.isEnabled = false
            binding.pieChartOrder.setOnChartValueSelectedListener(object :
                OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry, h: Highlight) {

                }

                override fun onNothingSelected() {}
            })
        }
    }

}