package com.app.beloz.data.models


import com.google.gson.annotations.SerializedName

data class Plato(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("image_path") val imagePath: String?,
    @SerializedName("restaurante_id") val restaurantId: Int
)
