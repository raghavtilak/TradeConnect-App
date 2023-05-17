package com.raghav.digitalpaymentsbook.adapter;

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.raghav.digitalpaymentsbook.data.model.enums.ConnectionStatus
import com.raghav.digitalpaymentsbook.ui.fragment.ConnectionsFragment

public class ConnectionsTabAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                ConnectionsFragment(ConnectionStatus.accepted)
            }
            1 -> {
                ConnectionsFragment(ConnectionStatus.pending)

            }
            else -> {
                ConnectionsFragment(ConnectionStatus.rejected)

            }
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}