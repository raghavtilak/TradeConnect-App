package com.raghav.digitalpaymentsbook.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.raghav.digitalpaymentsbook.adapter.RetailerAdapter
import com.raghav.digitalpaymentsbook.adapter.SellItemAdapter
import com.raghav.digitalpaymentsbook.data.model.enums.UserRole
import com.raghav.digitalpaymentsbook.data.network.RetrofitHelper
import com.raghav.digitalpaymentsbook.databinding.ActivityMySellsBinding
import com.raghav.digitalpaymentsbook.ui.dialog.LoadingDialog
import com.raghav.digitalpaymentsbook.ui.fragment.BatchsDetailContainerFragment
import kotlinx.coroutines.launch

class MySellsActivity : AppCompatActivity() {
    lateinit var binding: ActivityMySellsBinding

    val loadingDialog = LoadingDialog()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMySellsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.recyclerView.layoutManager = LinearLayoutManager(this@MySellsActivity)

        val adapter = SellItemAdapter{
            val frag = BatchsDetailContainerFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList("batches",ArrayList(it.batches))
            frag.arguments = bundle
            frag.show(supportFragmentManager,"batchDetails")
        }

        lifecycleScope.launch{

            loadingDialog.show(supportFragmentManager,"loading")

            val result =
                RetrofitHelper.getInstance(this@MySellsActivity).mySells()
            if (result.isSuccessful && result.body() != null) {
                val list = result.body()!!
                adapter.submitList(list)
                loadingDialog.dismiss()
            }else{
                loadingDialog.dismiss()
                Log.d("TAG","custo her ${result.body()}")
            }
        }

    }
}