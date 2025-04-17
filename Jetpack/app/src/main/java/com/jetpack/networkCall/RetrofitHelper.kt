package com.jetpack.networkCall

import android.content.Context
import com.jetpack.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitHelper {
    companion object {
        private var retrofit: Retrofit? = null
        private var okHttpClient: OkHttpClient? = null
        private const val REQUEST_TIMEOUT = 120

        fun getClient(context: Context): Retrofit {
            if (okHttpClient == null) {
                initOkHttp(context)
            }
            if (retrofit == null) {
                retrofit = Retrofit.Builder().baseUrl(Constants.BASE_URL)
                    .client(okHttpClient!!)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }

            return retrofit!!
        }

        private fun initOkHttp(context:Context) {
            val httpClient = OkHttpClient().newBuilder()
                .connectTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            httpClient.addInterceptor(interceptor)

            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val httpBuilder = original.newBuilder()
                    .addHeader("X-RapidAPI-Host", "mangaverse-api.p.rapidapi.com")
                    .addHeader("x-rapidapi-key", "88bc27e666mshd77a6a226b007d5p1d0764jsnaae92ff164a7")

                try {
                    val request = httpBuilder.build()
                    chain.proceed(request)
                } catch (e: Exception) {
                    throw e
                }
            }

            httpClient.retryOnConnectionFailure(true)
            okHttpClient = httpClient.build()
        }

    }
}