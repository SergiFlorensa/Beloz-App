package com.app.beloz.apis.services

import com.app.beloz.data.models.User
import com.app.beloz.data.remote.BelozApiClient
import com.app.beloz.utils.SessionManager

class AuthService {
    private var sessionManager: SessionManager? = null
    private val authApi: AuthApi by lazy { BelozApiClient.retrofit.create(AuthApi::class.java) }

    fun initialize(sessionManager: SessionManager) {
        this.sessionManager = sessionManager
    }

    suspend fun register(name: String, surname: String, email: String, password: String, numTelefono: String): User {
        val response = authApi.crearUsuario(SupabaseUserInsert(name, surname, email, password, numTelefono))
        val id = response.id ?: response.idUser ?: response.userId
        if (id == null || id <= 0) throw AuthFailure("El servidor no ha confirmado el registro.")
        return User(id, response.email ?: email, response.name ?: name, response.surname ?: surname,
            numTelefono = response.numTelefono ?: numTelefono)
    }

    suspend fun login(email: String, password: String): User {
        val response = authApi.login(mapOf("email" to email, "password" to password))
        val id = response.userId ?: response.id ?: response.idUser
        if (id == null || id <= 0 || response.token.isNullOrBlank()) {
            throw AuthFailure("El servidor no ha devuelto una sesion valida.")
        }
        return User(id, response.email ?: email, response.name.orEmpty(), response.surname.orEmpty(),
            response.token, response.numTelefono.orEmpty())
    }

    suspend fun updateEmail(userId: Int, newEmail: String): User {
        val current = authenticatedUser(userId)
        val response = authApi.updateEmail("Bearer ${current.token}", mapOf("new_email" to newEmail))
        return mergeProfile(response, current.copy(email = newEmail))
    }

    suspend fun updatePassword(userId: Int, currentPassword: String, newPassword: String) {
        val current = authenticatedUser(userId)
        authApi.updatePassword("Bearer ${current.token}",
            mapOf("current_password" to currentPassword, "new_password" to newPassword))
    }

    suspend fun updatePhoneNumber(userId: Int, numTelefono: String): User {
        val current = authenticatedUser(userId)
        val response = authApi.updatePhoneNumber("Bearer ${current.token}", mapOf("num_telefono" to numTelefono))
        return mergeProfile(response, current.copy(numTelefono = numTelefono))
    }

    suspend fun deleteUser(userId: Int) {
        val current = authenticatedUser(userId)
        authApi.deleteAccount("Bearer ${current.token}")
    }

    suspend fun logout(token: String) {
        authApi.logout("Bearer $token")
    }

    private fun authenticatedUser(userId: Int): User {
        val session = sessionManager ?: throw AuthFailure("Vuelve a iniciar sesion.")
        val token = session.getUserToken()
        if (token == null || session.getUserId() != userId) throw AuthFailure("La sesion ha caducado. Vuelve a iniciar sesion.")
        return User(userId, session.getUserEmail().orEmpty(), session.getUserName().orEmpty(),
            session.getUserSurname().orEmpty(), token, session.getUserPhoneNumber().orEmpty())
    }

    private fun mergeProfile(response: UserResponseDto, current: User): User {
        val id = response.userId ?: response.id ?: response.idUser
            ?: throw AuthFailure("El servidor no ha confirmado la actualizacion.")
        if (id != current.idUser) throw AuthFailure("La respuesta de la cuenta no es valida.")
        return current.copy(email = response.email ?: current.email, name = response.name ?: current.name,
            surname = response.surname ?: current.surname, numTelefono = response.numTelefono ?: current.numTelefono,
            token = response.token?.takeIf { it.isNotBlank() } ?: current.token)
    }
}

class AuthFailure(message: String) : Exception(message)
