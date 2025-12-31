package com.app.beloz.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.beloz.data.models.Plato
import com.app.beloz.data.repositories.PlatosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlatosViewModel : ViewModel() {
    private val _platos = MutableStateFlow<List<Plato>>(emptyList())
    val platos: StateFlow<List<Plato>> get() = _platos

    fun cargarPlatosPorRestaurante(restauranteId: Int) {
        viewModelScope.launch {
            try {
                val platosObtenidos = PlatosRepository.getPlatosPorRestaurante(restauranteId)
                _platos.value = platosObtenidos
                Log.d("PlatosViewModel", "Platos obtenidos: ${platosObtenidos.size}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
