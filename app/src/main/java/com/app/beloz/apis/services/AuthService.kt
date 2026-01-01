package com.app.beloz.apis.services

import com.app.beloz.data.models.User
import com.app.beloz.data.remote.SupabaseClient

class AuthService {
    private val authApi: AuthApi by lazy {
        SupabaseClient.retrofit.create(AuthApi::class.java)
    }

    suspend fun register(
        name: String,
        surname: String,
        email: String,
        password: String,
        numTelefono: String
    ): User {
        val existing = authApi.fetchUsuarios(emailFilter = eq(email))
        if (existing.isNotEmpty()) {
            throw Exception("El email ya está registrado.")
        }
        val insert = SupabaseUserInsert(
            name = name,
            surname = surname,
            email = email,
            password = password,
            numTelefono = numTelefono
        )
        val created = authApi.crearUsuario(insert)
        return created.firstOrNull()?.toUser()
            ?: throw Exception("No se pudo registrar el usuario.")
    }

    suspend fun login(email: String, password: String): User {
        val usuarios = authApi.fetchUsuarios(
            emailFilter = eq(email),
            passwordFilter = eq(password)
        )
        return usuarios.firstOrNull()?.toUser()
            ?: throw Exception("Credenciales incorrectas.")
    }

    suspend fun updateEmail(userId: Int, newEmail: String): User {
        val updated = authApi.actualizarUsuario(
            idFilter = eq(userId),
            body = mapOf("email" to newEmail)
        )
        return updated.firstOrNull()?.toUser() ?: throw Exception("No se pudo actualizar el correo.")
    }

    suspend fun updatePassword(userId: Int, currentPassword: String, newPassword: String) {
        val usuario = authApi.fetchUsuarios(idFilter = eq(userId)).firstOrNull()
            ?: throw Exception("Usuario no encontrado.")
        if (usuario.password.isNullOrBlank() || usuario.password != currentPassword) {
            throw Exception("La contraseña actual es incorrecta.")
        }
        val updated = authApi.actualizarUsuario(
            idFilter = eq(userId),
            body = mapOf("password" to newPassword)
        )
        if (updated.isEmpty()) {
            throw Exception("No se pudo actualizar la contraseña.")
        }
    }

    suspend fun updatePhoneNumber(userId: Int, numTelefono: String): User {
        val updated = authApi.actualizarUsuario(
            idFilter = eq(userId),
            body = mapOf("num_telefono" to numTelefono)
        )
        return updated.firstOrNull()?.toUser() ?: throw Exception("No se pudo actualizar el teléfono.")
    }

    suspend fun deleteUser(userId: Int) {
        authApi.eliminarUsuario(eq(userId))
    }

    private fun eq(value: Any): String = "eq.$value"
}

private fun SupabaseUserDto.toUser(): User {
    if (idUser == null || email.isNullOrBlank()) {
        throw Exception("Usuario inválido.")
    }
    return User(
        idUser = idUser,
        email = email,
        name = name.orEmpty(),
        surname = surname.orEmpty(),
        token = token,
        numTelefono = numTelefono
    )
}
