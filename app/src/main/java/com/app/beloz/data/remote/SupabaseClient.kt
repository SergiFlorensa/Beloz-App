package com.app.beloz.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SupabaseClient {
    private val client = OkHttpClient.Builder()
        .addInterceptor(SupabaseAuthInterceptor())
        .followRedirects(false)
        .followSslRedirects(false)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(SupabaseConfig.REST_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
