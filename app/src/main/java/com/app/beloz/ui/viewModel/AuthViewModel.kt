package com.app.beloz.ui.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.beloz.data.models.User
import com.app.beloz.data.repositories.AuthRepository
import com.app.beloz.utils.SessionManager
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    var user: User? = null
    private val sessionManager = SessionManager.getInstance(application)

    init {
        AuthRepository.initialize(sessionManager)

        if (sessionManager.isLoggedIn()) {
            user = User(
                idUser = sessionManager.getUserId(),
                name = sessionManager.getUserName() ?: "",
                surname = sessionManager.getUserSurname()
                    ?: "",
                email = sessionManager.getUserEmail() ?: "",
                numTelefono = sessionManager.getUserPhoneNumber()
                    ?: "",
                token = sessionManager.getUserToken()
            )
        }
        Log.d("AuthViewModel", "Phone number loaded: ${user?.numTelefono}")

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
                user = AuthRepository.login(email, password)
                Log.d("AuthViewModel", "Phone number loaded on login: ${user?.numTelefono}")

                sessionManager.saveSession(
                    userId = user!!.idUser,
                    userName = user!!.name,
                    userSurname = user!!.surname ?: "", // Aseguramos que no sea nulo
                    userEmail = user!!.email,
                    numTelefono = user!!.numTelefono ?: "",
                    userToken = user!!.token
                )
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
                if (currentPassword.length < 6) {
                    onError("La contraseña actual debe tener al menos 6 caracteres.")
                    return@launch
                }

                AuthRepository.updatePassword(
                    userId = user!!.idUser,
                    currentPassword = currentPassword,
                    newPassword = newPassword
                )
                if (newPassword.length < 6) {
                    onError("La nueva contraseña debe tener al menos 6 caracteres.")
                    return@launch
                }

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
                AuthRepository.deleteUser()
                sessionManager.clearSession()
                paymentViewModel.clearPaymentData()
                user = null
                Log.d("AuthViewModel", "Cuenta eliminada y todos los datos limpiados correctamente.")
                onSuccess()
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al eliminar la cuenta: ${e.message}")
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
                Log.d("AuthViewModel", "Updating phone number: $numTelefono")

                val updatedUser = AuthRepository.updatePhoneNumber(numTelefono)

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