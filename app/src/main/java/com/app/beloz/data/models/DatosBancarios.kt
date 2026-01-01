package com.app.beloz.data.models

import com.google.gson.annotations.SerializedName

data class DatosBancarios(
    val id: Int = 0,
    @SerializedName(value = "user_id", alternate = ["userId"])
    val userId: Int,
    @SerializedName(value = "nombre_titular", alternate = ["nombreTitular"])
    val nombreTitular: String?,
    @SerializedName(value = "numero_tarjeta_encriptado", alternate = ["numeroTarjetaEncriptado"])
    val numeroTarjetaEncriptado: String?,
    val iv: String?,
    @SerializedName(value = "fecha_expiracion", alternate = ["fechaExpiracion"])
    val fechaExpiracion: String?,
    @SerializedName(value = "tipo_tarjeta", alternate = ["tipoTarjeta"])
    val tipoTarjeta: String?,
    @SerializedName(value = "metodo_pago_predeterminado", alternate = ["metodoPagoPredeterminado"])
    val metodoPagoPredeterminado: Boolean = true

)
