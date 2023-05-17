package com.raghav.digitalpaymentsbook.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.raghav.digitalpaymentsbook.adapter.ConnectionsTabAdapter
import com.raghav.digitalpaymentsbook.adapter.TransactionsTabAdapter
import com.raghav.digitalpaymentsbook.databinding.ActivityMainBinding
import com.raghav.digitalpaymentsbook.databinding.ActivityMyConnectionsBinding
import com.raghav.digitalpaymentsbook.ui.fragment.AddConnectionFragment

class MyConnectionsActivity : AppCompatActivity() {
    lateinit var binding: ActivityMyConnectionsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyConnectionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ConnectionsTabAdapter(this)
        binding.viewpager.adapter= adapter
        TabLayoutMediator(binding.tabLayout,binding.viewpager){
                tab, position ->
            tab.text = when(position){
                0 -> "Accepted"
                1-> "Pending"
                else -> "Rejected"
            }
        }.attach()

        binding.addConnection.setOnClickListener {
            val frag = AddConnectionFragment()
            frag.show(
                supportFragmentManager,
                "TAG"
            )
        }
    }
}