package com.raghav.digitalpaymentsbook.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.raghav.digitalpaymentsbook.adapter.SellItemAdapter
import com.raghav.digitalpaymentsbook.data.network.RetrofitHelper
import com.raghav.digitalpaymentsbook.databinding.ActivityMySellsBinding
import com.raghav.digitalpaymentsbook.ui.dialog.LoadingDialog
import com.raghav.digitalpaymentsbook.ui.fragment.BatchsDetailContainerFragment
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class MySellsActivity : AppCompatActivity() {
    lateinit var binding: ActivityMySellsBinding
    lateinit var adapter: SellItemAdapter
    val loadingDialog = LoadingDialog()
    val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.d(
            "TAG",
            "ERROR=${throwable.message} , ${throwable.printStackTrace()}"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMySellsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.recyclerView.layoutManager = LinearLayoutManager(this@MySellsActivity)

        adapter = SellItemAdapter {
            val frag = BatchsDetailContainerFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList("batches", ArrayList(it.batches))
            frag.arguments = bundle
            frag.show(supportFragmentManager, "batchDetails")
        }

        binding.recyclerView.adapter = adapter

        updateList()

        binding.swipeRefresh.setOnRefreshListener {
            updateList()
            binding.swipeRefresh.isRefreshing = false
        }

        binding.addSell.setOnClickListener {
            startActivity(Intent(this,CreateSellActivity::class.java))
        }
    }

    private fun updateList() {
        lifecycleScope.launch(handler) {

            loadingDialog.show(supportFragmentManager, "loading")

            val result =
                RetrofitHelper.getInstance(this@MySellsActivity).mySells()
            if (result.isSuccessful && result.body() != null) {
                val list = result.body()!!
                adapter.submitList(list)
                loadingDialog.dismiss()
            } else {
                loadingDialog.dismiss()
                Log.d("TAG", "custo her ${result.body()}")
            }
        }
    }
}