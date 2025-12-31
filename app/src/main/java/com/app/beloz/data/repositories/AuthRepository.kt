package com.app.beloz.data.repositories

import com.app.beloz.apis.services.AuthService
import com.app.beloz.data.models.User
import com.app.beloz.utils.SessionManager

object AuthRepository {
    private const val baseUrl = "https://beloz-production.up.railway.app"
    private lateinit var authService: AuthService

    fun initialize(sessionManager: SessionManager) {
        authService = AuthService(baseUrl, sessionManager)
    }

    suspend fun register(name: String, surname: String, email: String, password: String, numTelefono: String): User {
        return authService.register(name, surname, email, password, numTelefono)
    }

    suspend fun login(email: String, password: String): User {
        return authService.login(email, password)
    }
    suspend fun updateEmail(userId: Int, newEmail: String): User {
        return authService.updateEmail(userId, newEmail)
    }
    suspend fun updatePassword(userId: Int, currentPassword: String, newPassword: String) {
        authService.updatePassword(userId, currentPassword, newPassword)
    }
    suspend fun updatePhoneNumber(numTelefono: String): User {
        return authService.updatePhoneNumber(numTelefono)
    }
    suspend fun deleteUser() {
        authService.deleteUser()
    }


}
