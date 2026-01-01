package com.app.beloz.apis.services

import com.app.beloz.data.models.DatosBancarios
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface PaymentApi {
    @GET("datos_bancarios")
    suspend fun obtenerDatos(
        @Query("user_id") userFilter: String,
        @Query("order") order: String? = null,
        @Query("limit") limit: Int = 1
    ): List<DatosBancarios>

    @POST("datos_bancarios")
    @Headers("Prefer: return=representation")
    suspend fun insertar(@Body body: SupabasePaymentInsert): List<DatosBancarios>

    @PATCH("datos_bancarios")
    @Headers("Prefer: return=representation")
    suspend fun actualizar(
        @Query("user_id") userFilter: String,
        @Body body: SupabasePaymentUpdate
    ): List<DatosBancarios>
}

data class SupabasePaymentInsert(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("nombre_titular") val nombreTitular: String?,
    @SerializedName("numero_tarjeta_encriptado") val numeroTarjetaEncriptado: String?,
    @SerializedName("iv") val iv: String?,
    @SerializedName("fecha_expiracion") val fechaExpiracion: String?,
    @SerializedName("tipo_tarjeta") val tipoTarjeta: String?,
    @SerializedName("metodo_pago_predeterminado") val metodoPagoPredeterminado: Boolean = true
)

data class SupabasePaymentUpdate(
    @SerializedName("nombre_titular") val nombreTitular: String?,
    @SerializedName("numero_tarjeta_encriptado") val numeroTarjetaEncriptado: String?,
    @SerializedName("iv") val iv: String?,
    @SerializedName("fecha_expiracion") val fechaExpiracion: String?,
    @SerializedName("tipo_tarjeta") val tipoTarjeta: String?,
    @SerializedName("metodo_pago_predeterminado") val metodoPagoPredeterminado: Boolean = true
)
