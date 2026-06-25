package com.app.beloz.apis.services

import android.util.Log
import com.app.beloz.data.models.DatosBancarios
import com.app.beloz.data.remote.BelozApiClient

class PaymentService {
    private val paymentApi: PaymentApi by lazy {
        BelozApiClient.retrofit.create(PaymentApi::class.java)
    }

    suspend fun savePaymentData(datosBancarios: DatosBancarios): Result<Unit> {
        return try {
            paymentApi.guardar(datosBancarios.userId, datosBancarios.toUpsert())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al guardar los datos de pago: ${e.message}", e))
        }
    }

    suspend fun getPaymentData(userId: Int): DatosBancarios? {
        return try {
            paymentApi.obtenerDatos(userId)
        } catch (e: Exception) {
            Log.e("PaymentService", "Error: ${e.message}")
            null
        }
    }
}

private fun DatosBancarios.toUpsert() = BackendPaymentUpsert(
    userId = userId,
    nombreTitular = nombreTitular,
    numeroTarjetaEncriptado = numeroTarjetaEncriptado,
    iv = iv,
    fechaExpiracion = fechaExpiracion,
    tipoTarjeta = tipoTarjeta,
    metodoPagoPredeterminado = metodoPagoPredeterminado
)
