package com.raghav.digitalpaymentsbook.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.raghav.digitalpaymentsbook.databinding.ActivityMainBinding
import com.raghav.digitalpaymentsbook.databinding.ActivityMyOrderBinding

class MyOrderActivity : AppCompatActivity() {
    lateinit var binding: ActivityMyOrderBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)    }
}