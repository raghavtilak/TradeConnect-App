package com.raghav.digitalpaymentsbook.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raghav.digitalpaymentsbook.adapter.BatchAdapter
import com.raghav.digitalpaymentsbook.data.model.Batch
import com.raghav.digitalpaymentsbook.data.model.Retailer
import com.raghav.digitalpaymentsbook.data.model.apis.RetailerProduct
import com.raghav.digitalpaymentsbook.data.model.enums.ConnectionStatus
import com.raghav.digitalpaymentsbook.data.network.RetrofitHelper
import com.raghav.digitalpaymentsbook.databinding.ActivityCreateOrderBinding
import com.raghav.digitalpaymentsbook.databinding.ItemBatchBinding
import com.raghav.digitalpaymentsbook.databinding.ItemRetailerBinding
import com.raghav.digitalpaymentsbook.ui.dialog.LoadingDialog
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject


class CreateOrderActivity : AppCompatActivity() {

    lateinit var binding: ActivityCreateOrderBinding
    val loadingDialog = LoadingDialog()

    val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.d(
            "TAG",
            "ERROR=${throwable.message} ${throwable.printStackTrace()}"
        )
    }
    var orderBatchAdapter: BatchAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val retAdapter = object : ArrayAdapter<Retailer>(
            this@CreateOrderActivity, android.R.layout.simple_dropdown_item_1line,
            mutableListOf()
        ) {
            override fun getView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                return initGetView(position, convertView, parent)
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                return initDropDownView(position, convertView, parent)
            }

            private fun initGetView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ):View{
                val textView = TextView(this@CreateOrderActivity)
                val c = getItem(position)!!
                textView.text = c.name
                textView.updatePadding(20,20,20,20)
                return textView
            }
            private fun initDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val binding = ItemRetailerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                with(binding) {
                    val c = getItem(position)!!
                    retailerName.text = c.name
                    businessName.text = c.businessName
                    phone.text = c.phone
                    avatarText.text = if (c.businessName.contains(" "))
                        "${c.businessName.split(" ")[0][0]}${c.businessName.split(" ")[1][0]}"
                    else
                        c.businessName[0].toString()
                }
                return binding.root
            }
        }

        binding.chooseRet.adapter = retAdapter

        lifecycleScope.launch(handler) {
            loadingDialog.show(supportFragmentManager, "loading")
            val job =
                async {
                    RetrofitHelper.getInstance(this@CreateOrderActivity)
                        .getMyConnections(ConnectionStatus.accepted)
                }
            val response = job.await()
            if (response.isSuccessful && response.body() != null) {
                loadingDialog.dismiss()
                if (response.body()!!.isEmpty()) {
                    Toast.makeText(
                        this@CreateOrderActivity,
                        "You don't have any connections, to create an order",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    retAdapter.clear()
                    retAdapter.addAll(response.body()!!.map { it.user }.toMutableList())
                    updateUi()
                }
            } else {
                Toast.makeText(
                    this@CreateOrderActivity,
                    "Some error occurred. Couldn't load your connections.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("TAG", "Couldn't load connections")
            }
        }


    }

    private fun updateUi() {
        val prodAdapter = object : ArrayAdapter<RetailerProduct>(
            this@CreateOrderActivity, android.R.layout.simple_dropdown_item_1line , mutableListOf()
        ) {
            override fun getView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                return initView(position, convertView, parent)
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                return initView(position, convertView, parent)
            }

            private fun initView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ):View{
                val textView = TextView(this@CreateOrderActivity)
                val c = getItem(position)!!
                textView.text = c.productName
                textView.paint.isFakeBoldText = true
                textView.updatePadding(20,20,20,20)
                return textView
            }
        }

//            ArrayAdapter(
//            this@CreateOrderActivity,
//            android.R.layout.simple_spinner_item,
//            mutableListOf("Choose Product")
//        )


        binding.chooseProduct.adapter = prodAdapter

        val batchAdapter = object : ArrayAdapter<Batch>(
            this@CreateOrderActivity, android.R.layout.simple_dropdown_item_1line,
            mutableListOf()
        ) {
            override fun getView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                return initGetView(position, convertView, parent)
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                return initDropDownView(position, convertView, parent)
            }

            private fun initGetView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ):View{
                val textView = TextView(this@CreateOrderActivity)
                val c = getItem(position)!!
                textView.text = c.batchNo
                textView.updatePadding(20,20,20,20)
                return textView
            }

            private fun initDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val binding = ItemBatchBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                with(binding) {
                    val c = getItem(position)!!
                    productName.text = c.productName
                    quantity.text = "Quantity: ${c.quantity}"
                    mrp.text = "MRP: ₹${c.MRP}"
                    sellPrice.text = "Sell Price: ₹${c.sellingPrice}"
                    batchNo.text = "Batch No: ${c.batchNo}"
                }
                return binding.root
            }
        }
        binding.chooseBatch.adapter = batchAdapter

        binding.chooseProduct.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(!prodAdapter.isEmpty) {
                    batchAdapter.clear()
                    batchAdapter.addAll((binding.chooseProduct.selectedItem as RetailerProduct).batches)
                }
                binding.productDetails.isVisible = false
                binding.chooseBatch.setSelection(0)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        binding.chooseRet.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                loadingDialog.show(supportFragmentManager, "loading")
                binding.productDetails.isVisible = false
                orderBatchAdapter?.submitList(mutableListOf())

                batchAdapter.clear()

                lifecycleScope.launch(handler) {
                    val job =
                        async {
                            RetrofitHelper.getInstance(this@CreateOrderActivity)
                                .getRetailerProducts((binding.chooseRet.selectedItem as Retailer).id)
                        }
                    val response = job.await()
                    if (response.isSuccessful && response.body() != null) {

//                        prodAdapter.clear()
//                        prodAdapter.addAll(response.body()!!)

                        prodAdapter.clear()
                        prodAdapter.addAll(response.body()!!)
                        loadingDialog.dismiss()
                    } else {
                        Toast.makeText(
                            this@CreateOrderActivity,
                            "Can't load products",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadingDialog.dismiss()
                    }
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        binding.chooseBatch.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                binding.TextFieldQuantity.isErrorEnabled = false
                binding.productDetails.isVisible = true
                with(binding) {
                    val c = (binding.chooseProduct.selectedItem as RetailerProduct).batches[position]
                    productName.text = c.productName
                    quantity.text = "Quantity: ${c.quantity}"
                    mrp.text = "MRP: ₹${c.MRP}"
                    batchNo.text = "Batch No: ${c.batchNo}"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }


        orderBatchAdapter = BatchAdapter {
            val nl= mutableListOf<Batch>()
            nl.addAll(orderBatchAdapter?.currentList!!)
            nl.remove(it)
            orderBatchAdapter?.submitList(nl)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = orderBatchAdapter


        binding.addBatch.setOnClickListener {
            if (binding.editTextQuantity.text.isNullOrBlank()) {
                binding.TextFieldQuantity.error = "Quantity required"
                binding.TextFieldQuantity.isErrorEnabled = true
            } else if (binding.editTextQuantity.text.toString()
                    .toInt() > (binding.chooseBatch.selectedItem as Batch).quantity
            ) {
                Toast.makeText(
                    this,
                    "The quantity should be less than available quantity",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val nl= mutableListOf<Batch>()
                nl.addAll(orderBatchAdapter!!.currentList)
                val item = (binding.chooseBatch.selectedItem as Batch)
                val batch = item.copy(
                    id = item.id,
                    batchNo = item.batchNo,
                    MRP = item.MRP,
                    mfg = item.mfg,
                    expiry = item.expiry,
                    productName = item.productName,
                    quantity = binding.editTextQuantity.text.toString().toInt(),
                    buyingPrice = item.buyingPrice,
                    sellingPrice = item.sellingPrice,
                    isUpdateAllowed = item.isUpdateAllowed
                )
                nl.add(batch)
                orderBatchAdapter?.submitList(nl)
            }
        }
        binding.createOrder.setOnClickListener {

            if (orderBatchAdapter!!.itemCount == 0) {
                Toast.makeText(this, "There are no products", Toast.LENGTH_SHORT).show()
            } else {


                lifecycleScope.launch(handler) {

                    val order = JSONObject()
                    order.put(
                        "receiverId",
                        (binding.chooseRet.selectedItem as Retailer).id.toHexString()
                    )
                    order.put(
                        "totalAmount",
                        orderBatchAdapter!!.currentList.sumOf { it.sellingPrice })

                    val batches = JSONArray()
                    orderBatchAdapter!!.currentList.forEach {
                        val batch = JSONObject()
                        batch.put("batchNo", it.batchNo)
                        batch.put("quantity", it.quantity)
                        batch.put("price", it.sellingPrice)
                        batches.put(batch)
                    }
                    order.put("batches", batches)

                    val body =
                        order.toString().toRequestBody("application/json".toMediaTypeOrNull())

                    val job =
                        async {
                            RetrofitHelper.getInstance(this@CreateOrderActivity)
                                .createOrder(body)
                        }
                    val response = job.await()
                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(
                            this@CreateOrderActivity,
                            response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@CreateOrderActivity,
                            response.body()?.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
    }
}