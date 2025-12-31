package com.app.beloz.apis.services

import com.app.beloz.data.models.Restaurante
import retrofit2.http.GET
import retrofit2.http.Query

interface RestauranteApi {
    @GET("api/restaurantes")
    suspend fun fetchRestaurantes(): List<Restaurante>

    @GET("api/restaurantes/country")
    suspend fun fetchRestaurantesByCountry(@Query("country") country: String): List<Restaurante>

    @GET("api/restaurantes/populares")
    suspend fun fetchRestaurantesPopulares(): List<Restaurante>

    @GET("api/restaurantes/search")
    suspend fun searchRestaurantes(@Query("query") query: String): List<Restaurante>

    @GET("api/restaurantes/filter")
    suspend fun getRestaurantesFiltradosPorTipos(@Query("types") types: String): List<Restaurante>

    @GET("api/restaurantes/filter_by_price")
    suspend fun getRestaurantesPorNivelPrecio(@Query("priceLevel") priceLevel: String): List<Restaurante>

    @GET("api/restaurantes/ordenar_por_valoracion")
    suspend fun fetchRestaurantesPorValoracion(): List<Restaurante>

    @GET("api/restaurantes/ordenar_por_relevancia")
    suspend fun fetchRestaurantesPorRelevancia(): List<Restaurante>

    @GET("api/restaurantes/interes")
    suspend fun fetchRestaurantesInteres(): List<Restaurante>

}

