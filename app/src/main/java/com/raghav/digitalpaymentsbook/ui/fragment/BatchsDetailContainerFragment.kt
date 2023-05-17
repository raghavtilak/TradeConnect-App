package com.raghav.digitalpaymentsbook.ui.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.raghav.digitalpaymentsbook.adapter.BatchDetailTabAdapter
import com.raghav.digitalpaymentsbook.data.model.Batch
import com.raghav.digitalpaymentsbook.databinding.FragmentBatchsDetailContainerBinding

class BatchsDetailContainerFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBatchsDetailContainerBinding? = null
    val binding: FragmentBatchsDetailContainerBinding
        get() = _binding!!

    var batches: ArrayList<Batch>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBatchsDetailContainerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bottomSheet = view.parent as View
        bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
        bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)

        batches = arguments?.getParcelableArrayList("batches")
        batches?.let {
            val adapter = BatchDetailTabAdapter(requireActivity(), it)
            binding.viewpager.adapter = adapter
            binding.viewpager.offscreenPageLimit = 5

            TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
                tab.text = "Batch $position"
            }.attach()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}