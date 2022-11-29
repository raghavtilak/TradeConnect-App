package com.raghav.digitalpaymentsbook.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.raghav.digitalpaymentsbook.adapter.TransactionsAdapter
import com.raghav.digitalpaymentsbook.data.network.RetrofitHelper
import com.raghav.digitalpaymentsbook.databinding.FragmentSettledTransactionsBinding
import com.raghav.digitalpaymentsbook.ui.viewmodel.TransactionViewmodel
import kotlinx.coroutines.launch

class SettledTransactionsFragment : Fragment() {

    var _binding: FragmentSettledTransactionsBinding? =null
    val binding : FragmentSettledTransactionsBinding
        get() = _binding!!

    lateinit var viewmodel: TransactionViewmodel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentSettledTransactionsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewmodel = ViewModelProvider(requireActivity())[TransactionViewmodel::class.java]
        val adapter = TransactionsAdapter()
        viewmodel.settledList.observe(requireActivity()){
            adapter.submitList(it.toMutableList())
        }

        lifecycleScope.launch {
            val result = RetrofitHelper.userAPI.getAllSettledTransactions(viewmodel.customer!!.id,viewmodel.retailer!!.id)
            if(result.isSuccessful && result.body()!=null){
                val list = result.body()!!
                viewmodel.settledList.value = list.toMutableList()
            }else{
                Toast.makeText(requireActivity(),"Some error occurred", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}