package com.raghav.digitalpaymentsbook.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.raghav.digitalpaymentsbook.adapter.RetailerAdapter
import com.raghav.digitalpaymentsbook.data.model.enums.ConnectionStatus
import com.raghav.digitalpaymentsbook.data.network.RetrofitHelper
import com.raghav.digitalpaymentsbook.databinding.FragmentConnectionsBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ConnectionsFragment(private val status: ConnectionStatus) : Fragment() {

    var _binding: FragmentConnectionsBinding? = null
    val binding: FragmentConnectionsBinding
        get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConnectionsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        lifecycleScope.launch {
            val job = async { RetrofitHelper.getInstance(requireActivity()).getMyConnections(status) }
            val adapter = RetailerAdapter {
                val frag = RetailerDetailsFragment(it,status){
                    lifecycleScope.launch {
                        val job = async { RetrofitHelper.getInstance(requireActivity()).updateConnectionReq(it) }
                        val res = job.await()
                        if(res.isSuccessful){
                            Toast.makeText(requireActivity(), res.body()?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                frag.show(
                    requireActivity().supportFragmentManager,
                    "TAG"
                )
            }
            val response = job.await()
            if (response.isSuccessful && response.body() != null) {
                adapter.submitList(response.body()!!.map { it.user })
            }
            binding.recyclerView.adapter = adapter


        }


    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}