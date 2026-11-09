package com.app.beloz.ui.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.app.beloz.data.models.DatosBancarios
import com.app.beloz.data.repositories.PaymentRepository
import com.app.beloz.utils.SessionManager

// Card storage is disabled until a tokenized payment provider is integrated.
class PaymentViewModel(
    private val paymentRepository: PaymentRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    var datosBancarios by mutableStateOf<DatosBancarios?>(null)
        private set
    fun clearPaymentData() { datosBancarios = null }
    fun loadPaymentData(onError: (String) -> Unit = {}) {
        clearPaymentData()
        onError("Los pagos no están disponibles.")
    }
}
