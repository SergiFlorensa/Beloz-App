package com.app.beloz.data.models

data class UpdateEmailRequest(
    val id_user: Int,
    val new_email: String
)
