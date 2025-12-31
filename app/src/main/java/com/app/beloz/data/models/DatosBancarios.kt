package com.app.beloz.data.models

import com.google.gson.annotations.SerializedName

data class DatosBancarios(
    val id: Int = 0,
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("nombreTitular")
    val nombreTitular: String?,
    @SerializedName("numeroTarjetaEncriptado")
    val numeroTarjetaEncriptado: String?,
    val iv: String?,
    @SerializedName("fechaExpiracion")
    val fechaExpiracion: String?,
    @SerializedName("tipoTarjeta")
    val tipoTarjeta: String?,
    val metodoPagoPredeterminado: Boolean = true

)
