package com.app.beloz.data.remote

import com.app.beloz.BuildConfig
import com.app.beloz.utils.SessionManager
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object BelozApiClient {
    @Volatile private var sessionManager: SessionManager? = null

    fun initialize(sessionManager: SessionManager) {
        this.sessionManager = sessionManager
    }

    private val baseUrl = BuildConfig.BELOZ_API_BASE_URL.trim().let {
        (if (it.endsWith('/')) it else "$it/").toHttpUrl()
    }.also {
        require(it.username.isEmpty() && it.password.isEmpty() && it.query == null && it.fragment == null)
        require(BuildConfig.DEBUG || it.isHttps) { "Release API requires HTTPS" }
    }

    private val client = OkHttpClient.Builder()
        // Do not log URLs, headers or bodies: they can contain credentials and personal data.
        .addInterceptor { chain ->
            val original = chain.request()
            if (original.url.scheme != baseUrl.scheme || original.url.host != baseUrl.host || original.url.port != baseUrl.port) {
                throw IOException("Destino de red no permitido.")
            }
            val publicAuth = original.url.encodedPath.endsWith("/api/auth/login") ||
                original.url.encodedPath.endsWith("/api/auth/register")
            val token = if (publicAuth) null else sessionManager?.getUserToken()
            val request = original.newBuilder().apply {
                if (token != null && original.header("Authorization") == null) header("Authorization", "Bearer $token")
            }.build()
            val response = chain.proceed(request)
            if (response.code == 401 && token != null && request.header("Authorization") == "Bearer $token") {
                sessionManager?.clearSessionIfTokenMatches(token)
            }
            response
        }
        .followRedirects(false)
        .followSslRedirects(false)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
