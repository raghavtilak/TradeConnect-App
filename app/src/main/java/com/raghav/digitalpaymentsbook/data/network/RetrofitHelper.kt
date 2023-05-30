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
            .baseUrl("https://a600-2409-4052-4e11-d6b0-5d2f-467c-1fe7-cbe0.ngrok-free.app/api/v1/")
            .client(UnsafeOkHttpClient(it).getUnsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(GsonUtils.gson))
            .build().create(UserAPI::class.java)
    })

//    val userAPI: UserAPI by lazy {
//        retrofitService().create(UserAPI::class.java)
//    }

}