package com.raghav.digitalpaymentsbook.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.raghav.digitalpaymentsbook.R
import com.raghav.digitalpaymentsbook.data.model.Customer
import com.raghav.digitalpaymentsbook.data.model.Retailer
import com.raghav.digitalpaymentsbook.data.network.RetrofitHelper
import com.raghav.digitalpaymentsbook.databinding.ActivityCreateUserBinding
import com.raghav.digitalpaymentsbook.ui.dialog.LoadingDialog
import com.raghav.digitalpaymentsbook.util.Constants
import com.raghav.digitalpaymentsbook.util.add
import kotlinx.coroutines.launch

class CreateUserActivity : AppCompatActivity() {

    lateinit var binding: ActivityCreateUserBinding
    val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.getIntExtra("role",0)==(Constants.CUSTOMER)){
            showForCustomer()
        }else{
            showForRetailer()
        }
    }

    fun showForCustomer(){
        binding.customerLayout.visibility= View.VISIBLE
        binding.retailerLayout.visibility= View.GONE
        
        binding.phoneNo.text = Editable.Factory.getInstance().newEditable(user!!.phoneNumber)

        binding.createBtn.setOnClickListener { 
            if(validateCustomerFields()){
                
                val dialog = LoadingDialog()
                dialog.show(supportFragmentManager,"loading")
                dialog.isCancelable=false
                
                val name = binding.editTextCustomerName.text!!.toString()
                val address = binding.editTextAddress.text!!.toString()
                val password = user?.uid
                val phone = user?.phoneNumber?.substring(1)
                val c = Customer(name,password!!,phone!!,address )
                
                lifecycleScope.launch{
                    val result = RetrofitHelper.userAPI.createCustomer(c)
                    if(result.isSuccessful && result.body()!=null){
                        dialog.dismiss()
                        val prefs = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE)
                        prefs.add(c)
                        startActivity(Intent(this@CreateUserActivity,MainActivity::class.java))
                    }else{
                        dialog.dismiss()
                        Toast.makeText(this@CreateUserActivity,"Can't create user right now",Toast.LENGTH_SHORT).show()
                    }
                }
                
            }
        }
        
    }

    private fun validateCustomerFields(): Boolean {
        
        if(binding.editTextCustomerName.text.isNullOrEmpty()){
            binding.TextFieldCustomerName.isErrorEnabled = true
            binding.TextFieldCustomerName.error = "This can't be empty"
            return false
        }

        if(binding.editTextAddress.text.isNullOrEmpty()){
            binding.TextFieldAddress.isErrorEnabled = true
            binding.TextFieldAddress.error = "This can't be empty"
            return false
        }
        
        return true
        
    }

    private fun validateRetailerFields(): Boolean {

        if(binding.editTextRetailerName.text.isNullOrEmpty()){
            binding.TextFieldRetailerName.isErrorEnabled = true
            binding.TextFieldRetailerName.error = "This can't be empty"
            return false
        }

        if(binding.editTextRetAddress.text.isNullOrEmpty()){
            binding.TextFieldRetAddress.isErrorEnabled = true
            binding.TextFieldRetAddress.error = "This can't be empty"
            return false
        }

        if(binding.editTextShopName.text.isNullOrEmpty()){
            binding.TextFieldShopName.isErrorEnabled = true
            binding.TextFieldShopName.error = "This can't be empty"
            return false
        }

        return true

    }

    fun showForRetailer(){
        binding.customerLayout.visibility= View.GONE
        binding.retailerLayout.visibility= View.VISIBLE

        binding.phoneNo.text = Editable.Factory.getInstance().newEditable(user!!.phoneNumber)

        binding.retcreateBtn.setOnClickListener {
            if(validateCustomerFields()){

                val dialog = LoadingDialog()
                dialog.show(supportFragmentManager,"loading")
                dialog.isCancelable=false

                val name = binding.editTextRetailerName.text!!.toString()
                val shopName = binding.editTextShopName.text!!.toString()
                val address = binding.editTextRetAddress.text!!.toString()
                val password = user?.uid
                val phone = user?.phoneNumber?.substring(1)
                val r = Retailer(name,password!!,phone!!,address,shopName)

                lifecycleScope.launch{
                    val result = RetrofitHelper.userAPI.createRetailer(r)
                    if(result.isSuccessful && result.body()!=null){
                        dialog.dismiss()
                        val prefs = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE)
                        prefs.add(r)
                        startActivity(Intent(this@CreateUserActivity,MainActivity::class.java))
                    }else{
                        dialog.dismiss()
                        Toast.makeText(this@CreateUserActivity,"Can't create user right now",Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }


}