package com.raghav.digitalpaymentsbook.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.raghav.digitalpaymentsbook.adapter.ProductAdapter
import com.raghav.digitalpaymentsbook.data.model.Customer
import com.raghav.digitalpaymentsbook.data.model.Product
import com.raghav.digitalpaymentsbook.data.model.Transaction
import com.raghav.digitalpaymentsbook.data.network.RetrofitHelper
import com.raghav.digitalpaymentsbook.databinding.ActivityAddProductBinding
import com.raghav.digitalpaymentsbook.ui.dialog.LoadingDialog
import com.raghav.digitalpaymentsbook.ui.viewmodel.AddProductViewmodel
import com.raghav.digitalpaymentsbook.util.Constants
import com.raghav.digitalpaymentsbook.util.getRetailer
import kotlinx.coroutines.launch

class AddProductActivity : AppCompatActivity(), ProductAdapter.OnRemoveClickListener {

    lateinit var binding: ActivityAddProductBinding
    lateinit var viewmodel: AddProductViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewmodel = ViewModelProvider(this)[AddProductViewmodel::class.java]
        val adapter = ProductAdapter(this)
        viewmodel.list.observe(this) {
            adapter.submitList(it.toMutableList())
        }
        viewmodel.totalPrice.observe(this){
            binding.totalAmount.text=it.toString()
        }
        viewmodel.paid.observe(this){
            binding.userPaid.text=it.toString()
        }
        viewmodel.due.observe(this){
            binding.dueAmount.text=it.toString()
        }

        binding.addTransaction.setOnClickListener {
            val dialog = LoadingDialog()
            dialog.isCancelable=false
            dialog.show(supportFragmentManager,"loading")

            lifecycleScope.launch {

                val c = intent.getParcelableExtra<Customer>(Constants.CUSTOMER_STR)!!
                val r= getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE).getRetailer()
                val transaction = Transaction(c.id,r.id,viewmodel.due.value!!,viewmodel.paid.value!!,viewmodel.totalPrice.value!!)
                val result = RetrofitHelper.userAPI.addTransaction(transaction)

                if(result.isSuccessful && result.body()!=null){
                    dialog.dismiss()
                    val intent = Intent()
                    intent.putExtra(Constants.TRANSACTION_STR,result.body())
                    setResult(RESULT_OK,intent)
                    finish()
                }else{
                    dialog.dismiss()
                    Toast.makeText(this@AddProductActivity,"Some error occurred",Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.addProduct.setOnClickListener {
            if(validate()){
                val name = binding.editTextProduct.text!!.toString()
                val price = binding.editTextProductPrice.text!!.toString().toInt()
                val paid = binding.editTextPaid.text!!.toString().toInt()

                val p = Product(name,price,paid)
                viewmodel.addProduct(p)
            }
        }
    }

    private fun validate(): Boolean {
        if(binding.editTextProduct.text.isNullOrEmpty()){
            binding.TextFieldProduct.isErrorEnabled=true
            binding.TextFieldProduct.error="This is required"
            return false
        }
        if(binding.editTextProductPrice.text.isNullOrEmpty()){
            binding.TextFieldProductPrice.isErrorEnabled=true
            binding.TextFieldProductPrice.error="This is required"
            return false
        }
        return true
    }

    override fun onItemRemove(product: Product) {
        viewmodel.removeProduct(product)
    }
}