package com.raghav.digitalpaymentsbook.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.raghav.digitalpaymentsbook.R
import com.raghav.digitalpaymentsbook.data.model.Batch
import com.raghav.digitalpaymentsbook.databinding.FragmentAddConnectionBinding
import com.raghav.digitalpaymentsbook.databinding.FragmentBatchDetailBinding
import java.text.SimpleDateFormat

class BatchDetailFragment : Fragment() {

    private var _binding: FragmentBatchDetailBinding? = null
    val binding: FragmentBatchDetailBinding
        get() = _binding!!

    var batch: Batch? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBatchDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        batch = arguments?.getParcelable("batch")

        batch?.let {
            binding.batchNo.text = "Btach No: ₹${it.batchNo}"
            binding.mrp.text = "MRP: ₹${it.MRP}"
            binding.mfgdate.text = "Mfg: ${SimpleDateFormat("dd/MM/yyyy").format(it.mfg)}"
            binding.expdate.text = "Exp: ${SimpleDateFormat("dd/MM/yyyy").format(it.expiry)}"
            binding.productName.text = "Product Name: ${it.productName}"
            binding.quantity.text = "Quantity: ${it.quantity}"
            binding.buyPrice.text = "Buy Price: ₹${it.buyingPrice}"
            binding.sellPrice.text = "Sell Price: ₹${it.sellingPrice}"

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}