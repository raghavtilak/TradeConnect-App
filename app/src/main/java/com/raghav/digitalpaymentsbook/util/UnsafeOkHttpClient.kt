package com.raghav.digitalpaymentsbook.util

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.*

class UnsafeOkHttpClient(val context: Context) {

    public fun getUnsafeOkHttpClient(): OkHttpClient? {
        Log.d("TAG","getUnsafeOkHttpClient")
        return try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {

                    override fun checkClientTrusted(
                        chain: Array<out java.security.cert.X509Certificate>?,
                        authType: String?
                    ) {
                    }

                    override fun checkServerTrusted(
                        chain: Array<out java.security.cert.X509Certificate>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                        return arrayOf()
                    }


                }
            )

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            val trustManagerFactory: TrustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(null as KeyStore?)
            val trustManagers: Array<TrustManager> =
                trustManagerFactory.trustManagers
            check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
                "Unexpected default trust managers:" + trustManagers.contentToString()
            }

            val trustManager =
                trustManagers[0] as X509TrustManager


            val builder = OkHttpClient.Builder()
            builder.addInterceptor(AuthInterceptor(context))
            builder.addNetworkInterceptor { chain->

                val request: Request = chain.request()

                val t1 = System.nanoTime()
                Log.d("TAG",
                    java.lang.String.format(
                        "Sending request %s on %s%n%s",
                        request.url(), chain.connection(), request.headers()
                    )
                )

                val response: Response = chain.proceed(request)

                val t2 = System.nanoTime()
                Log.d("TAG",
                    java.lang.String.format(
                        "Received response for %s in %.1fms%n%s",
                        response.request().url(), (t2 - t1) / 1e6, response.headers()
                    )
                )

                response;

            }
            builder.sslSocketFactory(sslSocketFactory, trustManager)
            builder.hostnameVerifier(HostnameVerifier { _, _ -> true })

            Log.d("TAG","getUnsafeOkHttpClient 2")


            builder.build()


        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

}