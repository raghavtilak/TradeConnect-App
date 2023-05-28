package com.raghav.digitalpaymentsbook.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.raghav.digitalpaymentsbook.adapter.RetailerAdapter
import com.raghav.digitalpaymentsbook.data.model.Connection
import com.raghav.digitalpaymentsbook.data.model.enums.ConnectionStatus
import com.raghav.digitalpaymentsbook.data.network.RetrofitHelper
import com.raghav.digitalpaymentsbook.databinding.FragmentConnectionsBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class ConnectionsFragment(private val connections: List<Connection>) : Fragment() {

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

        val adapter = RetailerAdapter { retailer ->

            if(connections.isNotEmpty()) {

                val connectionId = connections.find { it.user == retailer }?.id
                Log.d("TAG","${connectionId ?:"Connection id not found"}")

                val frag = RetailerDetailsFragment(retailer, ConnectionStatus.pending, connections[0].isCreatedByUser) { status ->
                    lifecycleScope.launch {
                        val job = async {
                            val jsonObject = JSONObject()
                            jsonObject.put("status", status.name)
                            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                            RetrofitHelper.getInstance(requireActivity()).updateConnectionReq(connectionId!!,body)
                        }
                        val res = job.await()
                        if (res.isSuccessful) {
                            Toast.makeText(
                                requireActivity(),
                                res.body()?.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }else{
                            Toast.makeText(
                                requireActivity(),
                                res.body()?.error,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
                frag.show(
                    requireActivity().supportFragmentManager,
                    "TAG"
                )
            }
        }
        binding.recyclerView.adapter = adapter
        adapter.submitList(connections.map { it.user })



    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}