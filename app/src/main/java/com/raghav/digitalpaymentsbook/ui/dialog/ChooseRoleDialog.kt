package com.raghav.digitalpaymentsbook.ui.dialog

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.raghav.digitalpaymentsbook.databinding.DialogChooseRoleBinding
import com.raghav.digitalpaymentsbook.ui.activity.CreateUserActivity
import com.raghav.digitalpaymentsbook.util.Constants.CUSTOMER
import com.raghav.digitalpaymentsbook.util.Constants.RETAILER
import com.raghav.digitalpaymentsbook.util.setWidthPercent
import com.raghav.digitalpaymentsbook.util.setupWidthToMatchParent

class ChooseRoleDialog : DialogFragment() {

    var _binding: DialogChooseRoleBinding? =null
    val binding : DialogChooseRoleBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding= DialogChooseRoleBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setWidthPercent(65)
setupWidthToMatchParent()

        binding.customerBtn.setOnClickListener {
            startActivity(Intent(requireActivity(),CreateUserActivity::class.java).putExtra("role",CUSTOMER))
            dialog?.dismiss()
        }
        binding.retailerBtn.setOnClickListener {
            startActivity(Intent(requireActivity(),CreateUserActivity::class.java).putExtra("role",RETAILER))
            dialog?.dismiss()
        }
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

}