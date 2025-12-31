package com.app.beloz.data.repositories


import com.app.beloz.apis.services.PlatosService
import com.app.beloz.data.models.Plato

object PlatosRepository {
    private val platosService = PlatosService()

    suspend fun getPlatosPorRestaurante(restauranteId: Int): List<Plato> {
        return platosService.getPlatosPorRestaurante(restauranteId)
    }

}
