package com.app.beloz.apis.services

import com.app.beloz.data.models.DatosBancarios
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface PaymentApi {
    @GET("api/pagos/{user_id}")
    suspend fun obtenerDatos(@Path("user_id") userId: Int): DatosBancarios?

    @PUT("api/pagos/{user_id}")
    suspend fun guardar(
        @Path("user_id") userId: Int,
        @Body body: BackendPaymentUpsert
    ): DatosBancarios
}

data class BackendPaymentUpsert(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("nombre_titular") val nombreTitular: String?,
    @SerializedName("numero_tarjeta_encriptado") val numeroTarjetaEncriptado: String?,
    @SerializedName("iv") val iv: String?,
    @SerializedName("fecha_expiracion") val fechaExpiracion: String?,
    @SerializedName("tipo_tarjeta") val tipoTarjeta: String?,
    @SerializedName("metodo_pago_predeterminado") val metodoPagoPredeterminado: Boolean = true
)
