package com.raghav.digitalpaymentsbook.data.network

import android.util.Log
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date

//Client
object RetrofitHelper {

    private const val BASE_URL = "http://192.168.42.89:8084/";

    private fun retrofitService(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
//            .client(getUnsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val userAPI: UserAPI by lazy {
        retrofitService().create(UserAPI::class.java)
    }
}