package com.app.beloz.apis.services

import com.app.beloz.data.models.Restaurante
import retrofit2.http.GET
import retrofit2.http.Query

interface RestauranteApi {
    @GET("restaurante")
    suspend fun fetchRestaurantes(
        @Query("select") select: String = "*",
        @Query("order") order: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("country") country: String? = null,
        @Query("es_popular") esPopular: String? = null,
        @Query("type_of_food") typeOfFood: String? = null,
        @Query("price_level") priceLevel: String? = null,
        @Query("or") or: String? = null
    ): List<Restaurante>
}
