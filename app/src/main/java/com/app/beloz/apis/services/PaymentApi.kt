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
        @Query("userId") userFilter: String,
        @Query("limit") limit: Int = 1
    ): List<DatosBancarios>

    @POST("datos_bancarios")
    @Headers("Prefer: return=representation")
    suspend fun insertar(@Body body: SupabasePaymentInsert): List<DatosBancarios>

    @PATCH("datos_bancarios")
    @Headers("Prefer: return=representation")
    suspend fun actualizar(
        @Query("userId") userFilter: String,
        @Body body: SupabasePaymentUpdate
    ): List<DatosBancarios>
}

data class SupabasePaymentInsert(
    @SerializedName("userId") val userId: Int,
    @SerializedName("nombreTitular") val nombreTitular: String?,
    @SerializedName("numeroTarjetaEncriptado") val numeroTarjetaEncriptado: String?,
    @SerializedName("iv") val iv: String?,
    @SerializedName("fechaExpiracion") val fechaExpiracion: String?,
    @SerializedName("tipoTarjeta") val tipoTarjeta: String?,
    @SerializedName("metodoPagoPredeterminado") val metodoPagoPredeterminado: Boolean = true
)

data class SupabasePaymentUpdate(
    @SerializedName("nombreTitular") val nombreTitular: String?,
    @SerializedName("numeroTarjetaEncriptado") val numeroTarjetaEncriptado: String?,
    @SerializedName("iv") val iv: String?,
    @SerializedName("fechaExpiracion") val fechaExpiracion: String?,
    @SerializedName("tipoTarjeta") val tipoTarjeta: String?,
    @SerializedName("metodoPagoPredeterminado") val metodoPagoPredeterminado: Boolean = true
)
