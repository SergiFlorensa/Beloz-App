package com.app.beloz.innovacion.contexto.datos

import com.app.beloz.data.remote.BelozApiClient
import com.app.beloz.innovacion.contexto.datos.api.BelozBackendApi
import com.app.beloz.innovacion.contexto.dominio.ContextoEntrada
import com.app.beloz.innovacion.contexto.dominio.SugerenciaContextual

class ProveedorRecomendacionesRemoto {
    private val api: BelozBackendApi by lazy {
        BelozApiClient.retrofit.create(BelozBackendApi::class.java)
    }

    suspend fun obtenerRecomendaciones(contexto: ContextoEntrada): List<SugerenciaContextual> {
        return try {
            api.obtenerRecomendaciones(contexto).map {
                SugerenciaContextual(
                    titulo = it.titulo,
                    descripcion = it.descripcion,
                    etiquetas = it.etiquetas,
                    motivo = it.motivo ?: "Recomendacion del motor de inteligencia Beloz",
                    restauranteId = it.restauranteId,
                    restauranteNombre = it.restauranteNombre,
                    imagePath = it.imagePath,
                    priceLevel = it.priceLevel,
                    waitTime = it.waitTime,
                    typeOfFood = it.typeOfFood,
                    country = it.country,
                    valoracion = it.valoracion,
                    score = it.score
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
