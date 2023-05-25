package com.raghav.digitalpaymentsbook.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
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
    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            Toast.makeText(
                this,
                "This permission is required to get notifications for orders and connection requests",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl")
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl")
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl")

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
                binding.profile.setOnClickListener {
                    startActivity(Intent(this@MainActivity, ProfileActivity::class.java))

                }

                askNotificationPermission()
                getFcmToken()
            }

        }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                Toast.makeText(
                    this,
                    "This permission is required to get notifications for orders and connection requests",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
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


    private fun getFcmToken():String?{
        var token :String? = null

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)

                return@addOnCompleteListener
            }

            // Get new FCM registration token
            token = task.result

            // Log and toast
            Log.d("TAG", "Got token : $token")

        }
        return token
    }


}