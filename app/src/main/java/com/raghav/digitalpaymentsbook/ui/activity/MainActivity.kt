package com.raghav.digitalpaymentsbook.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.raghav.digitalpaymentsbook.data.model.enums.UserRole
import com.raghav.digitalpaymentsbook.databinding.ActivityMainBinding
import com.raghav.digitalpaymentsbook.ui.viewmodel.MainViewmodel
import com.raghav.digitalpaymentsbook.util.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
//    , CustomerAdapter.OnItemClickListener,
//    RetailerAdapter.OnItemClickListener {

    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewmodel

    val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.d(
            "TAG",
            "ERROR=${throwable.message}"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

            val fb = FirebaseAuth.getInstance()
            if (fb.currentUser == null || !PreferenceManager.getInstance(this).userExist()) {
                if (fb.currentUser != null) {
                    fb.signOut()
                    fb.currentUser!!.delete().addOnCompleteListener {
                        startActivity(Intent(this@MainActivity, SignInActivity::class.java))
                        finish()
                    }
                }else{
                    startActivity(Intent(this@MainActivity, SignInActivity::class.java))
                    finish()
                }

            } else {

//            viewModel = ViewModelProvider(this)[MainViewmodel::class.java]
                when (PreferenceManager.getInstance(this).typeOfUser()) {
                    UserRole.Retailer -> {
                        binding.businessName.text = PreferenceManager.getInstance(this).getRetailer().businessName
                    }
                    UserRole.Customer -> {
                        binding.businessName.text = "Retailers"

                    }
                    null -> {

                    }
                }

                binding.viewStore.setOnClickListener {
                    startActivity(Intent(this@MainActivity, ViewStoreActivity::class.java))

                }
                binding.myOrder.setOnClickListener {
                    startActivity(Intent(this@MainActivity, MyOrderActivity::class.java))

                }
                binding.mySells.setOnClickListener {
                    startActivity(Intent(this@MainActivity, MySellsActivity::class.java))

                }
                binding.myConnections.setOnClickListener {
                    startActivity(Intent(this@MainActivity, MyConnectionsActivity::class.java))

                }

            }

        }


/*
    private fun showForCustomer() {
        binding.store.isVisible = false
        binding.addTransaction.isVisible = false
        binding.businessName.text = "Retailers"

        val adapter = RetailerAdapter(this)
        viewModel.retailerList.observe(this) {
            adapter.submitList(it.toMutableList())
        }

        lifecycleScope.launch(handler) {
            Log.d("TAG","at least here ${prefs.getCustomer().id!!.toHexString()}")

            val result =
                RetrofitHelper.userAPI.getRetailerOfACustomers(prefs.getCustomer().id!!.toHexString())
            if (result.isSuccessful && result.body() != null) {
                val list = result.body()!!
                Log.d("TAG","custo her ${result.body()}")
                viewModel.retailerList.value = list.toMutableList()
            }else{
                Log.d("TAG","custo her ${result.body()}")
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

    }

    private fun showForRetailer() {
        Log.d("TAG", "here 2")

        binding.addTransaction.visibility = View.VISIBLE

        val r = prefs.getRetailer()
        binding.businessName.text = r.businessName

        val adapter = CustomerAdapter(this)
        viewModel.customerList.observe(this) {
            adapter.submitList(it.toMutableList())
        }

        binding.store.setOnClickListener {
            startActivity(Intent(this@MainActivity, ViewStoreActivity::class.java))
        }
        binding.addTransaction.setOnClickListener {
//            val addBottomDialogFragment = AddCustomerDialog()
//            addBottomDialogFragment.show(
//                supportFragmentManager,
//                "TAG"
//            )
            startActivity(Intent(this@MainActivity, AddProductActivity::class.java))
        }

        Log.d("TAG", "here4")

        lifecycleScope.launch(handler) {
            val result =
                RetrofitHelper.userAPI.getCustomersOfARetailer(prefs.getRetailer().id.toHexString())
            if (result.isSuccessful && result.body() != null) {
                Log.d("TAG", "reponse:${result.body()}")
                val list = result.body()!!
                viewModel.customerList.value = list.toMutableList()
            } else {
                Log.d("TAG", "reponse:${result.body()}, ${result}")
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

    }

    override fun onItemClick(customer: Customer) {
        startActivity(
            Intent(this, TransactionsActivity::class.java)
                .putExtra(UserRole.Customer.name, customer)
        )
    }

    override fun onItemClick(retailer: Retailer) {
        startActivity(
            Intent(this, TransactionsActivity::class.java)
                .putExtra(UserRole.Retailer.name, retailer)
        )
    }

*/

}