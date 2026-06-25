package com.app.beloz.apis.services

import com.app.beloz.data.models.Plato
import retrofit2.http.GET
import retrofit2.http.Query

interface PlatosApi {
    @GET("api/platos") // Implementaremos este endpoint genérico si es necesario
    suspend fun getPlatos(
        @Query("restaurante_id") restauranteId: Int? = null
    ): List<Plato>
}
