package com.app.beloz.data.repositories

import com.app.beloz.apis.services.PaymentService
import com.app.beloz.data.models.DatosBancarios

class PaymentRepository(private val paymentService: PaymentService) {
    suspend fun savePaymentData(datosBancarios: DatosBancarios) {
        paymentService.savePaymentData(datosBancarios)
    }

    suspend fun getPaymentData(userId: Int): DatosBancarios? {
        return paymentService.getPaymentData(userId)
    }
}
