package com.raghav.digitalpaymentsbook.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.raghav.digitalpaymentsbook.databinding.ActivityMainBinding
import com.raghav.digitalpaymentsbook.databinding.ActivityProfileBinding
import com.github.mikephil.charting.formatter.ValueFormatter


class ProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.analytics.setOnClickListener {
            startActivity(Intent(this,AnalyticsActivity::class.java))
        }


    }
}