package com.app.beloz.apis.services

import com.app.beloz.data.models.Plato
import com.app.beloz.data.remote.BelozApiClient

class PlatosService {
    private val platosApi: PlatosApi by lazy {
        BelozApiClient.retrofit.create(PlatosApi::class.java)
    }

    suspend fun getPlatosPorRestaurante(restauranteId: Int): List<Plato> {
        return platosApi.getPlatos(
            restauranteId = restauranteId
        )
    }
}
