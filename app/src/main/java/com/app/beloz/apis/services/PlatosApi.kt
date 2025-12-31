package com.app.beloz.apis.services

import com.app.beloz.data.models.Plato
import retrofit2.http.GET
import retrofit2.http.Query

interface PlatosApi {
    @GET("platos")
    suspend fun getPlatos(
        @Query("select") select: String = "*",
        @Query("restaurante_id") restauranteId: String? = null,
        @Query("order") order: String? = null
    ): List<Plato>
}
