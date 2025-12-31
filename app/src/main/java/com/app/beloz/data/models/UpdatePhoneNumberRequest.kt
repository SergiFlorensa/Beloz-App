package com.app.beloz.data.models

import com.google.gson.annotations.SerializedName

data class UpdatePhoneNumberRequest(
    //@SerializedName("id_user") val idUser: Int,
    @SerializedName("num_telefono") val numTelefono: String
)
