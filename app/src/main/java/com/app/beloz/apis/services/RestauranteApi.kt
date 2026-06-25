package com.app.beloz.apis.services

import com.app.beloz.data.models.Restaurante
import com.app.beloz.data.models.Plato
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RestauranteApi {
    @GET("api/restaurantes")
    suspend fun fetchRestaurantes(
        @Query("country") country: String? = null
    ): List<Restaurante>

    @GET("api/restaurantes/populares")
    suspend fun fetchPopulares(): List<Restaurante>

    @GET("api/restaurantes/filter")
    suspend fun fetchRestaurantesPorTipos(
        @Query("types") types: String
    ): List<Restaurante>

    @GET("api/restaurantes/search")
    suspend fun searchRestaurantes(
        @Query("query") query: String
    ): List<Restaurante>

    @GET("api/restaurantes/{id}/platos")
    suspend fun fetchPlatos(
        @Path("id") restauranteId: Int
    ): List<Plato>
}
