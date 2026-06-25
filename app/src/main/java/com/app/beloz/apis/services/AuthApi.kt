package com.app.beloz.apis.services

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/register")
    suspend fun crearUsuario(@Body body: SupabaseUserInsert): UserResponseDto

    @POST("api/auth/login")
    suspend fun login(@Body body: Map<String, String>): UserResponseDto
}

data class UserResponseDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("id_user") val idUser: Int?,
    @SerializedName("email") val email: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("surname") val surname: String?,
    @SerializedName("num_telefono") val numTelefono: String?,
    @SerializedName("token") val token: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("user_id") val userId: Int? // Para el login
)

data class SupabaseUserInsert(
    @SerializedName("name") val name: String,
    @SerializedName("surname") val surname: String,
    @SerializedName("email") val email: String,
    @SerializedName("password_hash") val passwordHash: String, // Cambiado para coincidir con el modelo Python
    @SerializedName("num_telefono") val numTelefono: String
)
