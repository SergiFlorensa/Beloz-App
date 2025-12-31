package com.app.beloz.data.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id_user") val idUser: Int,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("surname") val surname: String,
    @SerializedName("token") val token: String? = null,
    @SerializedName("num_telefono") val numTelefono: String?,
)
