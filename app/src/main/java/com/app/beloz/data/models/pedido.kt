package com.app.beloz.data.models

import com.google.gson.annotations.SerializedName

data class Pedido(
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName(value = "restaurant_id", alternate = ["restaurante_id"])
    val restaurantId: Int,
    @SerializedName("fecha")
    val fecha: String,
    @SerializedName("total")
    val total: Double
)
