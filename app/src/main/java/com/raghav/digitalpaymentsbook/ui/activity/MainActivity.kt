package com.raghav.digitalpaymentsbook.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.raghav.digitalpaymentsbook.adapter.CustomerAdapter
import com.raghav.digitalpaymentsbook.adapter.RetailerAdapter
import com.raghav.digitalpaymentsbook.data.model.Customer
import com.raghav.digitalpaymentsbook.data.model.Retailer
import com.raghav.digitalpaymentsbook.data.network.RetrofitHelper
import com.raghav.digitalpaymentsbook.databinding.ActivityMainBinding
import com.raghav.digitalpaymentsbook.ui.dialog.AddCustomerDialog
import com.raghav.digitalpaymentsbook.ui.viewmodel.MainViewmodel
import com.raghav.digitalpaymentsbook.util.*
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(),CustomerAdapter.OnItemClickListener,RetailerAdapter.OnItemClickListener {

    lateinit var binding: ActivityMainBinding
    lateinit var prefs : SharedPreferences
    lateinit var viewModel: MainViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE)

        if (FirebaseAuth.getInstance().currentUser == null
            || prefs.userExist()
        ) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            viewModel = ViewModelProvider(this)[MainViewmodel::class.java]

            if (prefs.typeOfUser() == Constants.CUSTOMER_STR) {
                showForCustomer()
            } else if (prefs.typeOfUser() == Constants.RETAILER_STR) {
                showForRetailer()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun showForCustomer() {
        binding.addCustomer.visibility = View.GONE
        binding.shopName.text = "Retailers"

        val adapter = RetailerAdapter(this)
        viewModel.retailerList.observe(this){
            adapter.submitList(it.toMutableList())
        }

        lifecycleScope.launch{
            val result = RetrofitHelper.userAPI.getAllRetailers(prefs.getCustomer().id)
            if(result.isSuccessful && result.body()!=null){
                val list = result.body()!!
                viewModel.retailerList.value = list.toMutableList()
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

    }

    private fun showForRetailer() {
        binding.addCustomer.visibility = View.VISIBLE

        val r = prefs.getRetailer()
        binding.shopName.text = r.shopName

        val adapter = CustomerAdapter(this)
        viewModel.customerList.observe(this){
            adapter.submitList(it.toMutableList())
        }

        binding.addCustomer.setOnClickListener {
            val addBottomDialogFragment = AddCustomerDialog()
            addBottomDialogFragment.show(
                supportFragmentManager,
                "TAG"
            )
        }

        lifecycleScope.launch{
            val result = RetrofitHelper.userAPI.getAllCustomers(prefs.getRetailer())
            if(result.isSuccessful && result.body()!=null){
                val list = result.body()!!
                viewModel.customerList.value = list.toMutableList()
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

    }

    override fun onItemClick(customer: Customer) {
        startActivity(Intent(this,TransactionsActivity::class.java)
            .putExtra(Constants.CUSTOMER_STR,customer))
    }

    override fun onItemClick(retailer: Retailer) {
        startActivity(Intent(this,TransactionsActivity::class.java)
            .putExtra(Constants.RETAILER_STR,retailer))
    }


}