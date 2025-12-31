package com.app.beloz.data.models

data class UpdatePasswordRequest(
    val id_user: Int,
    val current_password: String,
    val new_password: String
)
