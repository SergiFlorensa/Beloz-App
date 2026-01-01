package com.app.beloz.data.models

import com.google.gson.annotations.SerializedName

data class Restaurante(
    @SerializedName(value = "restaurante_id", alternate = ["id"]) val restauranteId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("image_path") val imagePath: String?,
    @SerializedName("wait_time") val waitTime: Int,
    @SerializedName("price_level") val priceLevel: String,
    @SerializedName("type_of_food") val typeOfFood: String,
    @SerializedName("country") val country: String,
    @SerializedName("es_popular") val esPopular: Boolean,
    @SerializedName("logo_restaurante") val logoRestaurante: String?,
    @SerializedName("relevancia") val relevancia: Int,
    @SerializedName("valoracion") val valoracion: Double
)
