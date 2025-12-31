package com.app.beloz.apis.services

import com.app.beloz.data.models.*
import com.app.beloz.utils.SessionManager
import com.app.beloz.utils.TokenInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthService(private val baseUrl: String, private val sessionManager: SessionManager) {
    private val retrofit: Retrofit by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(TokenInterceptor(sessionManager))
            .build()

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    suspend fun register(
        name: String,
        surname: String,
        email: String,
        password: String,
        numTelefono: String
    ): User {
        val user = UserRegistration(name, surname, email, password, numTelefono)

        return try {
            authApi.register(user)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = parseErrorMessage(errorBody)
            throw Exception(errorMessage)
        } catch (e: Exception) {
            throw Exception(e.message ?: "Error desconocido")
        }
    }


    suspend fun login(email: String, password: String): User {
        val credentials = AuthCredentials(email, password)
        return try {
            authApi.login(credentials)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = parseErrorMessage(errorBody)
            throw Exception(errorMessage)
        } catch (e: Exception) {
            throw Exception(e.message ?: "Error desconocido")
        }
    }
    suspend fun updateEmail(userId: Int, newEmail: String): User {
        val request = UpdateEmailRequest(userId, newEmail)
        try {
            return authApi.updateEmail(request)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = parseErrorMessage(errorBody)
            throw Exception(errorMessage)
        }
    }

    suspend fun updatePassword(userId: Int, currentPassword: String, newPassword: String) {
        val request = UpdatePasswordRequest(userId, currentPassword, newPassword)
        try {
            authApi.updatePassword(request)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = parseErrorMessage(errorBody)
            throw Exception(errorMessage)
        }
    }
    suspend fun updatePhoneNumber(numTelefono: String): User {
        val token = sessionManager.getUserToken() ?: ""
        val authHeader = "Bearer $token"
        val request = UpdatePhoneNumberRequest(numTelefono)
        try {
            return authApi.updatePhoneNumber(authHeader, request)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = parseErrorMessage(errorBody)
            throw Exception(errorMessage)
        }
    }



    private fun parseErrorMessage(errorBody: String?): String {
        return try {
            val jsonObject = JSONObject(errorBody)
            jsonObject.getString("error")
        } catch (e: Exception) {
            "Error desconocido"
        }
    }
    suspend fun deleteUser(): String {
        val token = sessionManager.getUserToken() ?: ""
        val authHeader = "Bearer $token"

        try {
            authApi.deleteUser(authHeader)
            return "Cuenta eliminada con éxito"
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = parseErrorMessage(errorBody)
            throw Exception(errorMessage)
        }
    }

}

