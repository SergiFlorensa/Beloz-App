package com.app.beloz.ui.viewModel

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.beloz.data.models.DatosBancarios
import com.app.beloz.data.repositories.PaymentRepository
import com.app.beloz.utils.SessionManager
import kotlinx.coroutines.launch
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec

class PaymentViewModel(
    private val paymentRepository: PaymentRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    var datosBancarios by mutableStateOf<DatosBancarios?>(null)
        private set

    private val keyAlias = "payment_key"

    init {
        generateSecretKeyIfNeeded()
        loadPaymentData()
        val userId = sessionManager.getUserId()
        if (userId != -1) {
            loadPaymentData()
        }
    }

    private fun generateSecretKeyIfNeeded() {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        if (!keyStore.containsAlias(keyAlias)) {
            Log.d("PaymentViewModel", "Generando nueva clave con alias: $keyAlias")
            val keyGenerator =
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                keyAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setKeySize(256).build()
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        } else {
            Log.d("PaymentViewModel", "Clave existente encontrada con alias: $keyAlias")
        }
    }

    fun encryptCardNumber(cardNumber: String): Pair<String, String> {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val secretKeyEntry = keyStore.getEntry(keyAlias, null) as? KeyStore.SecretKeyEntry
        val secretKey = secretKeyEntry?.secretKey ?: throw Exception("SecretKeyEntry is null")

        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val encrypted = cipher.doFinal(cardNumber.toByteArray(Charsets.UTF_8))

        return Pair(
            Base64.encodeToString(encrypted, Base64.DEFAULT),
            Base64.encodeToString(iv, Base64.DEFAULT)
        )
    }

    fun decryptCardNumber(encryptedData: String?, iv: String?): String? {
        if (encryptedData == null || iv == null) {
            Log.e("PaymentViewModel", "Encrypted data or IV is null")
            return null
        }
        return try {
            val encryptedByteArray = Base64.decode(encryptedData, Base64.DEFAULT)
            val ivByteArray = Base64.decode(iv, Base64.DEFAULT)

            val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            val secretKeyEntry = keyStore.getEntry(keyAlias, null) as? KeyStore.SecretKeyEntry
            if (secretKeyEntry == null) {
                Log.e("PaymentViewModel", "SecretKeyEntry is null")
                return null
            }
            val secretKey = secretKeyEntry.secretKey

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            val ivSpec = IvParameterSpec(ivByteArray)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
            val decryptedBytes = cipher.doFinal(encryptedByteArray)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            Log.e("PaymentViewModel", "Error al desencriptar", e)
            null
        }
    }

    fun loadPaymentData(onError: (String) -> Unit = {}) {
        val userId = sessionManager.getUserId()
        Log.d("PaymentViewModel", "Cargando datos para userId: $userId")
        if (userId == -1) {
            onError("Usuario no autenticado, debes iniciar sesión para guardar o modificar los datos.")
            return
        }
        viewModelScope.launch {
            try {
                val data = paymentRepository.getPaymentData(userId)
                datosBancarios = data
                Log.d("PaymentViewModel", "Datos bancarios cargados: $data")
            } catch (e: Exception) {
                onError("Error al cargar los datos de pago: ${e.message}")
                Log.e("PaymentViewModel", "Error al cargar datos: ${e.message}")
            }
        }
    }

    fun clearPaymentData() {
        datosBancarios = null
    }

    fun savePaymentData(
        nombreTitular: String,
        numeroTarjeta: String,
        fechaExpiracion: String,
        tipoTarjeta: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = sessionManager.getUserId()
        if (userId == -1) {
            onError("Usuario no autenticado.")
            return
        }

        if (nombreTitular.isBlank() || numeroTarjeta.isBlank() || fechaExpiracion.isBlank()) {
            onError("Por favor, complete todos los campos.")
            return
        }

        if (numeroTarjeta.length != 16) {
            onError("El número de tarjeta debe contener exactamente 16 dígitos.")
            return
        }

        val expirationRegex = Regex("^\\d{2}/\\d{2}$")
        if (!expirationRegex.matches(fechaExpiracion)) {
            onError("La fecha de expiración debe tener el formato MM/AA.")
            return
        }

        val mes = fechaExpiracion.substring(0, 2).toIntOrNull()
        val anio = fechaExpiracion.substring(3, 5).toIntOrNull()

        if (mes == null || anio == null) {
            onError("La fecha de expiración no es válida.")
            return
        }

        if (mes !in 1..12) {
            onError("El mes debe estar entre 01 y 12.")
            return
        }

        val current = java.util.Calendar.getInstance()
        val currentYear =
            current.get(java.util.Calendar.YEAR) % 100
        val currentMonth =
            current.get(java.util.Calendar.MONTH) + 1

        if (anio < currentYear || (anio == currentYear && mes < currentMonth)) {
            onError("La tarjeta no puede estar caducada. La fecha de expiración debe ser igual o posterior a la fecha actual.")
            return
        }

        try {
            val (numeroTarjetaEncriptado, iv) = encryptCardNumber(numeroTarjeta)
            val datosBancarios = DatosBancarios(
                userId = userId,
                nombreTitular = nombreTitular,
                numeroTarjetaEncriptado = numeroTarjetaEncriptado,
                iv = iv,
                fechaExpiracion = fechaExpiracion,
                tipoTarjeta = tipoTarjeta
            )

            viewModelScope.launch {
                try {
                    paymentRepository.savePaymentData(datosBancarios)
                    this@PaymentViewModel.datosBancarios = datosBancarios
                    onSuccess()
                } catch (e: Exception) {
                    onError("Error al guardar los datos: ${e.message}")
                }
            }
        } catch (e: Exception) {
            onError("Error al procesar los datos de tarjeta: ${e.message}")
        }
    }
}