package com.raghav.digitalpaymentsbook.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.raghav.digitalpaymentsbook.R
import com.raghav.digitalpaymentsbook.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (FirebaseAuth.getInstance().currentUser == null
            || getSharedPreferences("digipaybook", Context.MODE_PRIVATE).getLong("phone",0L)==0L
            || getSharedPreferences("digipaybook",
                Context.MODE_PRIVATE).getLong("phone",0L)!= FirebaseAuth.getInstance().currentUser?.phoneNumber?.substring(1)?.toLong()
        ) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)


        }

    }
}