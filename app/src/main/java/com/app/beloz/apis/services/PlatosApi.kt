package com.app.beloz.apis.services

import com.app.beloz.data.models.Plato
import retrofit2.http.GET
import retrofit2.http.Query

interface PlatosApi {
    @GET("api/platos")
    suspend fun getPlatosPorRestaurante(@Query("restauranteId") restauranteId: Int): List<Plato>
}
