package com.app.beloz.apis.services

import com.app.beloz.data.models.User
import com.app.beloz.data.remote.BelozApiClient

class AuthService {
    private val authApi: AuthApi by lazy {
        BelozApiClient.retrofit.create(AuthApi::class.java)
    }

    suspend fun register(
        name: String,
        surname: String,
        email: String,
        password: String,
        numTelefono: String
    ): User {
        val insert = SupabaseUserInsert(
            name = name,
            surname = surname,
            email = email,
            passwordHash = password, // El backend se encarga del hash
            numTelefono = numTelefono
        )
        val response = authApi.crearUsuario(insert)
        return User(
            idUser = response.id ?: response.idUser ?: response.userId ?: 0,
            email = response.email ?: email,
            name = response.name ?: name,
            surname = response.surname ?: surname,
            token = null,
            numTelefono = response.numTelefono ?: numTelefono
        )
    }

    suspend fun login(email: String, password: String): User {
        val response = authApi.login(mapOf("email" to email, "password" to password))
        val userId = response.userId ?: response.id ?: response.idUser ?: throw Exception("Login fallido")

        return User(
            idUser = userId,
            email = response.email ?: email,
            name = response.name.orEmpty(),
            surname = response.surname.orEmpty(),
            token = response.token,
            numTelefono = response.numTelefono.orEmpty()
        )
    }

    suspend fun updateEmail(userId: Int, newEmail: String): User {
        throw Exception("Funcionalidad de actualización de perfil pendiente en backend unificado.")
    }

    suspend fun updatePassword(userId: Int, currentPassword: String, newPassword: String) {
        throw Exception("Funcionalidad de actualización de contraseña pendiente en backend unificado.")
    }

    suspend fun updatePhoneNumber(userId: Int, numTelefono: String): User {
        throw Exception("Funcionalidad de actualización de teléfono pendiente en backend unificado.")
    }

    suspend fun deleteUser(userId: Int) {
        // Implementar en backend si se requiere
    }
}
