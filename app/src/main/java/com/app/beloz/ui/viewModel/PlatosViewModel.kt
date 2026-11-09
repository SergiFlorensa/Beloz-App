package com.app.beloz.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.beloz.data.models.Plato
import com.app.beloz.data.repositories.PlatosRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlatosViewModel : ViewModel() {
    private val _platos = MutableStateFlow<List<Plato>>(emptyList())
    val platos = _platos.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()
    private var request: Job? = null
    private var version = 0L

    fun cargarPlatosPorRestaurante(restauranteId: Int) {
        val current = ++version
        request?.cancel()
        _platos.value = emptyList()
        _isLoading.value = true
        _errorMessage.value = null
        request = viewModelScope.launch {
            try {
                val platos = PlatosRepository.getPlatosPorRestaurante(restauranteId)
                ensureActive()
                if (current == version) _platos.value = platos.distinctBy { it.id }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                if (current == version) {
                    _errorMessage.value = "No se ha podido cargar la carta. Comprueba la conexion y vuelve a intentarlo."
                }
            } finally {
                if (current == version) _isLoading.value = false
            }
        }
    }
}
