package com.app.beloz.ui.viewModel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.beloz.data.models.User
import com.app.beloz.data.repositories.AuthRepository
import com.app.beloz.utils.SessionManager
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    var user by mutableStateOf<User?>(null)
        private set
    private val sessionManager = SessionManager.getInstance(application)

    init {
        AuthRepository.initialize(sessionManager)

        viewModelScope.launch {
            sessionManager.sessionChanges.collect {
                restoreSession()
            }
        }
    }

    private fun restoreSession() {
        user = if (sessionManager.isLoggedIn()) {
            User(
                idUser = sessionManager.getUserId(),
                name = sessionManager.getUserName() ?: "",
                surname = sessionManager.getUserSurname()
                    ?: "",
                email = sessionManager.getUserEmail() ?: "",
                numTelefono = sessionManager.getUserPhoneNumber()
                    ?: "",
                token = sessionManager.getUserToken()
            )
        } else null
    }

    fun register(
        name: String,
        surname: String,
        email: String,
        password: String,
        numTelefono: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                AuthRepository.register(name, surname, email, password, numTelefono)
                onSuccess()
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Error desconocido"
                onError(errorMessage)
            }
        }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (email.isBlank() || password.isBlank()) {
            onError("Por favor, completa todos los campos.")
            return
        }
        viewModelScope.launch {
            try {
                val authenticatedUser = AuthRepository.login(email, password)
                sessionManager.saveSession(
                    userId = authenticatedUser.idUser,
                    userName = authenticatedUser.name,
                    userSurname = authenticatedUser.surname ?: "",
                    userEmail = authenticatedUser.email,
                    numTelefono = authenticatedUser.numTelefono ?: "",
                    userToken = authenticatedUser.token
                )
                restoreSession()
                onSuccess()
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Error desconocido"
                when {
                    errorMessage.contains("Usuario no encontrado", ignoreCase = true) -> {
                        onError("El email no está registrado.")
                    }
                    errorMessage.contains("Contraseña incorrecta", ignoreCase = true) -> {
                        onError("La contraseña es incorrecta.")
                    }
                    else -> {
                        onError(errorMessage)
                    }
                }
            }
        }
    }

    fun logout(pedidosViewModel: PedidosViewModel, cartViewModel: CartViewModel) {
        user = null
        sessionManager.clearSession()
        pedidosViewModel.limpiarPedidos()
        cartViewModel.clearCart()
    }

    fun updateEmail(
        newEmail: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (user == null) {
                    onError("Usuario no autenticado.")
                    return@launch
                }
                if (newEmail.isBlank()) {
                    onError("El correo no puede estar vacío.")
                    return@launch
                }
                if (newEmail == user!!.email) {
                    onError("El nuevo correo es igual al actual.")
                    return@launch
                }

                val updatedUser = AuthRepository.updateEmail(user!!.idUser, newEmail)

                user = updatedUser

                sessionManager.saveSession(
                    userId = user!!.idUser,
                    userName = user!!.name,
                    userSurname = user!!.surname ?: "",
                    userEmail = user!!.email,
                    numTelefono = user!!.numTelefono ?: "",
                    userToken = user!!.token
                )

                onSuccess()
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }

    fun updatePassword(
        currentPassword: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (user == null) {
                    onError("Usuario no autenticado.")
                    return@launch
                }
                if (currentPassword.isBlank() || newPassword.isBlank()) {
                    onError("Todos los campos son obligatorios.")
                    return@launch
                }
                if (currentPassword == newPassword) {
                    onError("La nueva contraseña debe ser diferente a la actual.")
                    return@launch
                }
                if (newPassword.length < 8 || newPassword.toByteArray(Charsets.UTF_8).size > 72) {
                    onError("Usa al menos 8 caracteres y un máximo de 72 bytes.")
                    return@launch
                }

                AuthRepository.updatePassword(
                    userId = user!!.idUser,
                    currentPassword = currentPassword,
                    newPassword = newPassword
                )

                onSuccess()
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }
    fun deleteAccount(
        paymentViewModel: PaymentViewModel,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (user == null) {
                    onError("Usuario no autenticado.")
                    return@launch
                }
                AuthRepository.deleteUser(user!!.idUser)
                sessionManager.clearSession()
                paymentViewModel.clearPaymentData()
                user = null
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error desconocido al eliminar la cuenta")
            }
        }
    }





    fun updatePhoneNumber(
        numTelefono: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (user == null) {
                    onError("Usuario no autenticado.")
                    return@launch
                }
                if (numTelefono.isBlank()) {
                    onError("El número de teléfono no puede estar vacío.")
                    return@launch
                }

                val updatedUser = AuthRepository.updatePhoneNumber(user!!.idUser, numTelefono)

                user = updatedUser
                sessionManager.saveSession(
                    userId = user!!.idUser,
                    userName = user!!.name,
                    userSurname = user!!.surname ?: "",
                    userEmail = user!!.email,
                    numTelefono = user!!.numTelefono ?: "",
                    userToken = user!!.token
                )

                onSuccess()
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }
}
