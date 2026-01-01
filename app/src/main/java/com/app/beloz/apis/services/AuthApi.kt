package com.app.beloz.apis.services

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @GET("users")
    suspend fun fetchUsuarios(
        @Query("email") emailFilter: String? = null,
        @Query("password") passwordFilter: String? = null,
        @Query("id_user") idFilter: String? = null
    ): List<SupabaseUserDto>

    @POST("users")
    @Headers("Prefer: return=representation")
    suspend fun crearUsuario(@Body body: SupabaseUserInsert): List<SupabaseUserDto>

    @PATCH("users")
    @Headers("Prefer: return=representation")
    suspend fun actualizarUsuario(
        @Query("id_user") idFilter: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): List<SupabaseUserDto>

    @DELETE("users")
    suspend fun eliminarUsuario(@Query("id_user") idFilter: String)
}

data class SupabaseUserDto(
    @SerializedName(value = "id_user", alternate = ["id"]) val idUser: Int?,
    @SerializedName("email") val email: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("surname") val surname: String?,
    @SerializedName("token") val token: String?,
    @SerializedName("num_telefono") val numTelefono: String?,
    @SerializedName("password") val password: String?
)

data class SupabaseUserInsert(
    @SerializedName("name") val name: String,
    @SerializedName("surname") val surname: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("num_telefono") val numTelefono: String
)

