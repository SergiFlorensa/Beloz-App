package com.app.beloz.apis.services

import com.app.beloz.data.models.Restaurante
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestauranteService(private val baseUrl: String) {
    private val retrofit: Retrofit by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
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

    private val restauranteApi: RestauranteApi by lazy {
        retrofit.create(RestauranteApi::class.java)
    }

    suspend fun fetchRestaurantes(): List<Restaurante> {
        return restauranteApi.fetchRestaurantes()
    }

    suspend fun fetchRestaurantesByCountry(country: String): List<Restaurante> {
        return restauranteApi.fetchRestaurantesByCountry(country)
    }

    suspend fun fetchRestaurantesPopulares(): List<Restaurante> {
        return restauranteApi.fetchRestaurantesPopulares()
    }

    suspend fun searchRestaurantes(query: String): List<Restaurante> {
        return restauranteApi.searchRestaurantes(query)
    }

    suspend fun getRestaurantesFiltradosPorTipos(types: List<String>): List<Restaurante> {
        val typesQueryParam = types.joinToString(",")
        return restauranteApi.getRestaurantesFiltradosPorTipos(typesQueryParam)
    }
    suspend fun getRestaurantesPorNivelPrecio(priceLevel: String): List<Restaurante> {
        return restauranteApi.getRestaurantesPorNivelPrecio(priceLevel)
    }
    suspend fun fetchRestaurantesPorValoracion(): List<Restaurante> {
        return restauranteApi.fetchRestaurantesPorValoracion()
    }

    suspend fun fetchRestaurantesPorRelevancia(): List<Restaurante> {
        return restauranteApi.fetchRestaurantesPorRelevancia()
    }
    suspend fun fetchRestaurantesInteres(): List<Restaurante> {
        return restauranteApi.fetchRestaurantesInteres()
    }

}
