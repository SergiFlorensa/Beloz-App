package com.app.beloz.apis.services

import com.app.beloz.data.models.*
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/register")
    suspend fun register(@Body user: UserRegistration): User

    @POST("api/auth/login")
    suspend fun login(@Body credentials: AuthCredentials): User

    @POST("api/auth/update_email")
    suspend fun updateEmail(@Body request: UpdateEmailRequest): User

    @POST("api/auth/update_password")
    suspend fun updatePassword(@Body request: UpdatePasswordRequest)

    @POST("api/auth/update_phone")
    suspend fun updatePhoneNumber(
        @Header("Authorization") authHeader: String,
        @Body request: UpdatePhoneNumberRequest
    ): User

    @POST("api/auth/delete_account")
    suspend fun deleteUser(@Header("Authorization") authHeader: String): Response<ResponseBody>
}

@Serializable
data class AuthCredentials(
    val email: String,
    val password: String
)

@Serializable
data class UserRegistration(
    val name: String,
    val surname: String,
    val email: String,
    val password: String,
    @SerializedName("num_telefono") val numTelefono: String
)

