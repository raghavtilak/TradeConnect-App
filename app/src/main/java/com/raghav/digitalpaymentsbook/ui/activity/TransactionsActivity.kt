package com.raghav.digitalpaymentsbook.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.raghav.digitalpaymentsbook.R
import com.raghav.digitalpaymentsbook.adapter.TabAdapter
import com.raghav.digitalpaymentsbook.data.model.Customer
import com.raghav.digitalpaymentsbook.data.model.Retailer
import com.raghav.digitalpaymentsbook.data.model.Transaction
import com.raghav.digitalpaymentsbook.databinding.ActivityTransactionsBinding
import com.raghav.digitalpaymentsbook.ui.viewmodel.TransactionViewmodel
import com.raghav.digitalpaymentsbook.util.Constants
import com.raghav.digitalpaymentsbook.util.getCustomer
import com.raghav.digitalpaymentsbook.util.getRetailer
import com.raghav.digitalpaymentsbook.util.typeOfUser

class TransactionsActivity : AppCompatActivity() {

    lateinit var binding: ActivityTransactionsBinding
    val prefs = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE)
    lateinit var viewmodel: TransactionViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = TabAdapter(this)
        binding.viewpager.adapter= adapter
        TabLayoutMediator(binding.tabLayout,binding.viewpager){
            tab, position ->
            tab.text = when(position){
                0 -> "Pending"
                else -> "Settled"
            }
        }.attach()

        viewmodel = ViewModelProvider(this)[TransactionViewmodel::class.java]

        if (prefs.typeOfUser() == Constants.CUSTOMER_STR) {
            viewmodel.customer = prefs.getCustomer()
            viewmodel.retailer = intent.getParcelableExtra(Constants.RETAILER_STR)
            showForCustomer()
        } else if (prefs.typeOfUser() == Constants.RETAILER_STR) {
            viewmodel.retailer = prefs.getRetailer()
            viewmodel.customer = intent.getParcelableExtra(Constants.CUSTOMER_STR)
            showForRetailer()
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode== RESULT_OK && it.data!=null){
            val t = it.data?.getParcelableExtra<Transaction>(Constants.TRANSACTION_STR)
            if(t!=null){
                if(t.due==0){
                    viewmodel.addSettledTransaction(t)
                }else{
                    viewmodel.addPendingTransaction(t)
                }
            }
        }
    }
    private fun showForRetailer() {

        binding.addTransaction.setOnClickListener {
            launcher.launch(Intent(this,AddProductActivity::class.java)
                .putExtra(Constants.CUSTOMER_STR,
                    intent.getParcelableExtra<Customer>(Constants.CUSTOMER_STR)))
        }
    }

    private fun showForCustomer() {
        binding.addTransaction.visibility = View.GONE

    }
}