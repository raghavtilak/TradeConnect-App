package com.raghav.digitalpaymentsbook.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.raghav.digitalpaymentsbook.R
import com.raghav.digitalpaymentsbook.data.model.Customer
import com.raghav.digitalpaymentsbook.data.model.Retailer
import com.raghav.digitalpaymentsbook.data.model.User
import com.raghav.digitalpaymentsbook.data.model.enums.BusinessTypes
import com.raghav.digitalpaymentsbook.data.model.enums.UserRole
import com.raghav.digitalpaymentsbook.data.network.RetrofitHelper
import com.raghav.digitalpaymentsbook.databinding.ActivityCreateUserBinding
import com.raghav.digitalpaymentsbook.ui.dialog.LoadingDialog
import com.raghav.digitalpaymentsbook.util.PreferenceManager
import com.raghav.digitalpaymentsbook.util.add
import com.raghav.digitalpaymentsbook.util.saveAuthToken
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.bson.types.ObjectId

class CreateUserActivity : AppCompatActivity() {

    lateinit var binding: ActivityCreateUserBinding
    val user = FirebaseAuth.getInstance().currentUser
    lateinit var gso: GoogleSignInOptions

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)!!
            Log.d("TAG", "firebaseAuthWithGoogle:" + account.id)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.linkWithCredential(credential)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Google account linked successfully
                        Log.d("TAG", "Google account linked successfully")
                        binding.retEmail.text = task.result.user!!.email
                    } else {
                        // Google account link failed
                        Log.d("TAG", "Google account link failed")
                        Toast.makeText(
                            this@CreateUserActivity,
                            "Google account link failed",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.w("TAG", "Google sign in failed", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        when (intent.getSerializableExtra("role") as UserRole) {
            UserRole.Retailer -> {
                showForRetailer()
            }
            UserRole.Customer -> {
                showForCustomer()
            }
        }
    }

    fun showForCustomer() {
        binding.customerLayout.visibility = View.VISIBLE
        binding.retailerLayout.visibility = View.GONE

        binding.phoneNo.text = Editable.Factory.getInstance().newEditable(user!!.phoneNumber)
        binding.customerEmailCard.setOnClickListener {
            launcher.launch(GoogleSignIn.getClient(this, gso).signInIntent)
        }
        binding.createBtn.setOnClickListener {
            if (validateCustomerFields()) {

                val dialog = LoadingDialog()
                dialog.show(supportFragmentManager, "loading")
                dialog.isCancelable = false

                val name = binding.editTextCustomerName.text!!.toString()
                val address = binding.editTextAddress.text!!.toString()
                val password = user.uid
                val phone = user.phoneNumber!!.substring(3)
                val email = user.email
                val c = User(
                    name,
                    email!!,
                    getFcmToken(),
                    password,
                    phone,
                    address,
                    UserRole.Customer,
                    null,
                    null,
                    0,
                    ObjectId.get()
                )

                val handler = CoroutineExceptionHandler { a, b ->
                    Log.d("TAG", "${b.message}")
                }
                lifecycleScope.launch(handler) {
                    val job1 =
                        async {
                            RetrofitHelper.getInstance(this@CreateUserActivity).createCustomer(c)
                        }
                    val result1 = job1.await()

                    if (result1.isSuccessful && result1.body() != null) {

                        val job2 =
                            async {
                                RetrofitHelper.getInstance(this@CreateUserActivity)
                                    .getUser(null, user.phoneNumber!!.substring(3))
                            }
                        val result2 = job2.await()
                        if (result2.isSuccessful && result2.body() != null) {
                            dialog.dismiss()
                            PreferenceManager.getInstance(this@CreateUserActivity)
                                .saveAuthToken(result1.body()!!)
                            PreferenceManager.getInstance(this@CreateUserActivity).add(
                                Customer(
                                    result2.body()!!.name,
                                    result2.body()!!.phone,
                                    result2.body()!!.address,
                                    getFcmToken(),
                                    result2.body()!!.id
                                )
                            )
                            startActivity(
                                Intent(
                                    this@CreateUserActivity,
                                    MainActivity::class.java
                                )
                            )
                            finish()
                        }

                    } else {
                        Log.d("TAG", "here null")
                        dialog.dismiss()
                        Toast.makeText(
                            this@CreateUserActivity,
                            "Can't create user right now",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }

    }

    private fun validateCustomerFields(): Boolean {

        if (binding.editTextCustomerName.text.isNullOrEmpty()) {
            binding.TextFieldCustomerName.isErrorEnabled = true
            binding.TextFieldCustomerName.error = "This can't be empty"
            return false
        }

        if (binding.editTextAddress.text.isNullOrEmpty()) {
            binding.TextFieldAddress.isErrorEnabled = true
            binding.TextFieldAddress.error = "This can't be empty"
            return false
        }

        return true

    }

    private fun validateRetailerFields(): Boolean {

        if (binding.editTextRetailerName.text.isNullOrEmpty()) {
            binding.TextFieldRetailerName.isErrorEnabled = true
            binding.TextFieldRetailerName.error = "This can't be empty"
            return false
        }

        if (binding.editTextRetAddress.text.isNullOrEmpty()) {
            binding.TextFieldRetAddress.isErrorEnabled = true
            binding.TextFieldRetAddress.error = "This can't be empty"
            return false
        }

        if (binding.editTextShopName.text.isNullOrEmpty()) {
            binding.TextFieldShopName.isErrorEnabled = true
            binding.TextFieldShopName.error = "This can't be empty"
            return false
        }

        if (binding.busType.selectedItem == null) {
            Toast.makeText(this@CreateUserActivity, "Business Type required", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        if (user == null) {
            return false
        }
        if (user.email == null) {
            Toast.makeText(this@CreateUserActivity, "Email is required", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        return true

    }

    fun showForRetailer() {

        binding.customerLayout.visibility = View.GONE
        binding.retailerLayout.visibility = View.VISIBLE

        binding.phoneNo.text = Editable.Factory.getInstance().newEditable(user!!.phoneNumber)

        binding.retailerEmailCard.setOnClickListener {
            launcher.launch(GoogleSignIn.getClient(this, gso).signInIntent)
        }
        lifecycleScope.launch {
            val job =
                async { RetrofitHelper.getInstance(this@CreateUserActivity).getAllBusinessTypes() }
            val response = job.await()
            if (response.isSuccessful && response.body() != null) {

                binding.busType.adapter = ArrayAdapter(
                    this@CreateUserActivity,
                    android.R.layout.simple_spinner_item,
                    response.body()!!.toMutableList()
                )

            } else {
                Log.d("TAG", "Couldn't load businness types")
            }
        }
        binding.retcreateBtn.setOnClickListener {
            if (validateRetailerFields()) {

                val dialog = LoadingDialog()
                dialog.show(supportFragmentManager, "loading")
                dialog.isCancelable = false

                val name = binding.editTextRetailerName.text!!.toString()
                val businessName = binding.editTextShopName.text!!.toString()
                val address = binding.editTextRetAddress.text!!.toString()
                val email = user.email
                val password = user.uid
                val phone = user.phoneNumber!!.substring(3)
                val busType = (binding.busType.selectedItem as BusinessTypes).id.toHexString()
                val r =
                    User(
                        name,
                        email!!,
                        getFcmToken(),
                        password,
                        phone,
                        address,
                        UserRole.Retailer,
                        businessName,
                        busType,
                        0,
                        ObjectId.get()
                    )

                lifecycleScope.launch {
                    val job1 = async {
                        RetrofitHelper.getInstance(this@CreateUserActivity).createRetailer(r)
                    }
                    val result1 = job1.await()
                    if (result1.isSuccessful && result1.body() != null) {

                        val job2 = async {
                            RetrofitHelper.getInstance(this@CreateUserActivity)
                                .getUser(null, user.phoneNumber!!.substring(3))
                        }

                        val result2 = job2.await()
                        if (result2.isSuccessful && result2.body() != null) {

                            dialog.dismiss()
                            PreferenceManager.getInstance(this@CreateUserActivity)
                                .saveAuthToken(result1.body()!!)
                            PreferenceManager.getInstance(this@CreateUserActivity).add(
                                Retailer(
                                    result2.body()!!.name,
                                    result2.body()!!.email,
                                    result2.body()!!.password,
                                    result2.body()!!.phone,
                                    result2.body()!!.address,
                                    result2.body()!!.businessName!!,
                                    result2.body()!!.businessType!!,
                                    result2.body()!!.totalSales,
                                    getFcmToken(),
                                    result2.body()!!.id
                                )
                            )
                            startActivity(
                                Intent(
                                    this@CreateUserActivity,
                                    MainActivity::class.java
                                )
                            )
                            finish()
                        }
                    } else {

                        dialog.dismiss()
                        Toast.makeText(
                            this@CreateUserActivity,
                            "Can't create user right now",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
    }
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