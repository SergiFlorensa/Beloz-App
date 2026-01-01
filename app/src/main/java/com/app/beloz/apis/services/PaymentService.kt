package com.app.beloz.apis.services

import android.util.Log
import com.app.beloz.data.models.DatosBancarios
import com.app.beloz.data.remote.SupabaseClient

class PaymentService {
    private val paymentApi: PaymentApi by lazy {
        SupabaseClient.retrofit.create(PaymentApi::class.java)
    }

    suspend fun savePaymentData(datosBancarios: DatosBancarios): Result<Unit> {
        return try {
            val filtro = eq(datosBancarios.userId)
            val existente = paymentApi.obtenerDatos(userFilter = filtro, order = "id.desc")
            if (existente.isEmpty()) {
                paymentApi.insertar(datosBancarios.toInsert())
            } else {
                paymentApi.actualizar(filtro, datosBancarios.toUpdate())
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al guardar los datos de pago: ${e.message}", e))
        }
    }

    suspend fun getPaymentData(userId: Int): DatosBancarios? {
        return try {
            paymentApi.obtenerDatos(userFilter = eq(userId), order = "id.desc").firstOrNull()
        } catch (e: Exception) {
            Log.e("PaymentService", "Error: ${e.message}")
            null
        }
    }

    private fun eq(value: Any) = "eq.$value"
}

private fun DatosBancarios.toInsert() = SupabasePaymentInsert(
    userId = userId,
    nombreTitular = nombreTitular,
    numeroTarjetaEncriptado = numeroTarjetaEncriptado,
    iv = iv,
    fechaExpiracion = fechaExpiracion,
    tipoTarjeta = tipoTarjeta,
    metodoPagoPredeterminado = metodoPagoPredeterminado
)

private fun DatosBancarios.toUpdate() = SupabasePaymentUpdate(
    nombreTitular = nombreTitular,
    numeroTarjetaEncriptado = numeroTarjetaEncriptado,
    iv = iv,
    fechaExpiracion = fechaExpiracion,
    tipoTarjeta = tipoTarjeta,
    metodoPagoPredeterminado = metodoPagoPredeterminado
)
