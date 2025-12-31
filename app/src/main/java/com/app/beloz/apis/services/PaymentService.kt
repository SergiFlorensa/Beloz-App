package com.app.beloz.apis.services

import android.util.Log
import com.app.beloz.data.models.DatosBancarios
import org.json.JSONObject

class PaymentService(
    private val paymentApi: PaymentApi
) {
    suspend fun savePaymentData(datosBancarios: DatosBancarios): Result<Unit> {
        return try {
            val response = paymentApi.savePaymentData(datosBancarios)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(parseErrorMessage(response.errorBody()?.string())))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error al guardar los datos de pago: ${e.message}", e))
        }
    }


    suspend fun getPaymentData(userId: Int): DatosBancarios? {
        return try {
            val response = paymentApi.getPaymentData(userId)
            Log.d("PaymentService", "Response code: ${response.code()}")
            if (response.isSuccessful) {
                val body = response.body()
                Log.d("PaymentService", "Cuerpo de la respuesta: $body")
                if (body != null) {
                    // Verifica que los campos no sean null
                    if (body.numeroTarjetaEncriptado != null && body.iv != null) {
                        body
                    } else {
                        Log.e("PaymentService", "Campos numeroTarjetaEncriptado o iv son null")
                        null
                    }
                } else {
                    Log.e("PaymentService", "El cuerpo de la respuesta es null")
                    null
                }
            } else if (response.code() == 404) {
                null
            } else {
                throw Exception(parseErrorMessage(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Log.e("PaymentService", "Error: ${e.message}")
            throw Exception("Error al obtener los datos de pago: ${e.message}", e)
        }
    }


    private fun parseErrorMessage(errorBody: String?): String {
        return try {
            val jsonObject = JSONObject(errorBody ?: "")
            jsonObject.getString("error")
        } catch (e: Exception) {
            "Error desconocido"
        }
    }
}
