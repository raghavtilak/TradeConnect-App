package com.raghav.digitalpaymentsbook.util

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import okhttp3.Interceptor
import okhttp3.Response


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data_store")

/**
 * Interceptor to add auth token to requests
 */
class AuthInterceptor(val context: Context) : Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {
        val token = PreferenceManager.getInstance(context).getAuthToken()

        val requestBuilder = chain.request().newBuilder()

        // If token has been saved, add it to the request
        requestBuilder.addHeader("Authorization", "Bearer $token")

        return chain.proceed(requestBuilder.build())
    }
}