package com.app.ia.driver.apiclient

import android.text.TextUtils
import com.app.ia.driver.DriverApplication
import com.app.ia.driver.utils.CryptLib
import com.app.ia.driver.utils.NetworkConnectionInterceptor
import com.app.ia.driver.local.AppPreferencesHelper
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

object RetrofitFactory {

    private var apiService: ApiService? = null

    fun getInstance(): ApiService {
        return apiService ?: makeRetrofitService()
    }

    private fun makeRetrofitService(): ApiService {
        return Retrofit.Builder()
            .baseUrl(Api.BASE_URL)
            .client(makeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build().create(ApiService::class.java)
    }

    private fun makeOkHttpClient(): OkHttpClient {
        val tlsSocketFactory = TLSSocketFactory()
        val networkConnectionInterceptor = NetworkConnectionInterceptor(DriverApplication.getInstance())
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(makeLoggingInterceptor())
            .addInterceptor(networkConnectionInterceptor)
            .sslSocketFactory(tlsSocketFactory, tlsSocketFactory.trustManager)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .build()
    }

    private fun makeLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }

    private val authInterceptor = Interceptor { chain ->

        val cryptLib = CryptLib()
        val cipherText = cryptLib.encryptPlainTextWithRandomIV(Date().time.toString(), "keMStjdies")

        if (TextUtils.isEmpty(AppPreferencesHelper.getInstance().authToken)) {
            val newRequest = chain.request()
                .newBuilder()
                .addHeader("X-Access-Token", cipherText.trim())
                .build()
            chain.proceed(newRequest)
        }else{
            val newRequest = chain.request()
                .newBuilder()
                .addHeader("X-Access-Token", cipherText.trim())
                .addHeader("Authorization", "Bearer ${AppPreferencesHelper.getInstance().authToken}")
                .build()
            chain.proceed(newRequest)
        }

    }
}