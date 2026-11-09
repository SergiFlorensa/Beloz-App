package com.app.beloz.data.repositories


import com.app.beloz.apis.services.PlatosService
import com.app.beloz.data.models.Plato
import retrofit2.HttpException

object PlatosRepository {
    private val platosService = PlatosService()

    suspend fun getPlatosPorRestaurante(restauranteId: Int): List<Plato> {
        return try {
            platosService.getPlatosPorRestaurante(restauranteId)
        } catch (error: HttpException) {
            if (error.code() == 404) emptyList() else throw error
        }
    }

}
