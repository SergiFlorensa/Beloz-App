package com.app.beloz.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.IOException

class SupabaseAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val origin = SupabaseConfig.REST_BASE_URL.toHttpUrl()
        val url = chain.request().url
        if (!url.isHttps || url.host != origin.host || url.port != origin.port) {
            throw IOException("Destino de red no permitido.")
        }
        val request = chain.request()
            .newBuilder()
            .addHeader("apikey", SupabaseConfig.SUPABASE_ANON_KEY)
            .addHeader("Authorization", "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}")
            .addHeader("Accept", "application/json")
            .build()
        return chain.proceed(request)
    }
}
