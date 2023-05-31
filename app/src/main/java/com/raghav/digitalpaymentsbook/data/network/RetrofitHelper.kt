package com.raghav.digitalpaymentsbook.data.network

import android.content.Context
import android.util.Log
import com.raghav.digitalpaymentsbook.util.GsonUtils
import com.raghav.digitalpaymentsbook.util.SingletonHolder
import com.raghav.digitalpaymentsbook.util.UnsafeOkHttpClient
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//Client
class RetrofitHelper {

    val BASE_URL = "http://192.168.170.47:5000/api/v1/";

//    private fun retrofitService(): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(UnsafeOkHttpClient().getUnsafeOkHttpClient())
//            .addConverterFactory(GsonConverterFactory.create(GsonUtils.gson))
//            .build()
//    }

    companion object : SingletonHolder<UserAPI,Context>({
        Retrofit.Builder()
            .baseUrl("https://cd08-2409-4052-2e0a-c5b-4057-a956-c6c7-e87c.ngrok-free.app/api/v1/")
            .client(UnsafeOkHttpClient(it).getUnsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(GsonUtils.gson))
            .build().create(UserAPI::class.java)
    })

//    val userAPI: UserAPI by lazy {
//        retrofitService().create(UserAPI::class.java)
//    }

}