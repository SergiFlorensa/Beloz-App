package com.app.beloz.apis.services

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Header

interface AuthApi {
    @POST("api/auth/register")
    suspend fun crearUsuario(@Body body: SupabaseUserInsert): UserResponseDto

    @POST("api/auth/login")
    suspend fun login(@Body body: Map<String, String>): UserResponseDto

    @POST("api/auth/update_email")
    suspend fun updateEmail(@Header("Authorization") authorization: String, @Body body: Map<String, String>): UserResponseDto

    @POST("api/auth/update_password")
    suspend fun updatePassword(@Header("Authorization") authorization: String, @Body body: Map<String, String>)

    @POST("api/auth/update_phone")
    suspend fun updatePhoneNumber(@Header("Authorization") authorization: String, @Body body: Map<String, String>): UserResponseDto

    @POST("api/auth/delete_account")
    suspend fun deleteAccount(@Header("Authorization") authorization: String)

    @POST("api/auth/logout")
    suspend fun logout(@Header("Authorization") authorization: String)
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
    @SerializedName("password") val passwordHash: String,
    @SerializedName("num_telefono") val numTelefono: String
)
