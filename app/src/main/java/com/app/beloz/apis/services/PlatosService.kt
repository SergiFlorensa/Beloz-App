package com.app.beloz.apis.services


import com.app.beloz.data.models.Plato
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlatosService(private val baseUrl: String) {
    private val retrofit: Retrofit by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val platosApi: PlatosApi by lazy {
        retrofit.create(PlatosApi::class.java)
    }

    suspend fun getPlatosPorRestaurante(restauranteId: Int): List<Plato> {
        return platosApi.getPlatosPorRestaurante(restauranteId)
    }
}
