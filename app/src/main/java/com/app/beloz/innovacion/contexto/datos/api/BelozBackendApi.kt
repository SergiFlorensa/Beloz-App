package com.app.beloz.innovacion.contexto.datos.api

import com.app.beloz.innovacion.contexto.dominio.ContextoEntrada
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

interface BelozBackendApi {
    @POST("api/recomendaciones")
    suspend fun obtenerRecomendaciones(@Body contexto: ContextoEntrada): List<SugerenciaBackendResponse>
}

data class SugerenciaBackendResponse(
    val titulo: String,
    val descripcion: String,
    val etiquetas: List<String>,
    val motivo: String? = null,
    @SerializedName("restaurante_id")
    val restauranteId: Int? = null,
    @SerializedName("restaurante_nombre")
    val restauranteNombre: String? = null,
    @SerializedName("image_path")
    val imagePath: String? = null,
    @SerializedName("price_level")
    val priceLevel: String? = null,
    @SerializedName("wait_time")
    val waitTime: Int? = null,
    @SerializedName("type_of_food")
    val typeOfFood: String? = null,
    val country: String? = null,
    val valoracion: Double? = null,
    val score: Double? = null
)
