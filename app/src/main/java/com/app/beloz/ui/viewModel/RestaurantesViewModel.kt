package com.app.beloz.ui.viewModel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.app.beloz.data.models.Restaurante
import com.app.beloz.data.repositories.RestauranteRepository

class RestaurantesViewModel : ViewModel() {
    private val _restaurantes = MutableStateFlow<List<Restaurante>>(emptyList())
    val restaurantes: StateFlow<List<Restaurante>> = _restaurantes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        cargarRestaurantes()
    }

    fun cargarRestaurantes() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val restaurantesCargados = RestauranteRepository.fetchRestaurantes()
                _restaurantes.value = restaurantesCargados
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun buscarRestaurantes(query: String) {
        viewModelScope.launch {
            if (query.isNotBlank()) {
                try {
                    val restaurantesEncontrados = RestauranteRepository.searchRestaurantes(query)
                    _restaurantes.value = restaurantesEncontrados
                } catch (e: Exception) {
                    _restaurantes.value = emptyList()
                }
            } else {
                _restaurantes.value = emptyList()
            }
        }
    }

    fun getRestaurantesFiltradosPorTipos(types: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val restaurantesEncontrados = RestauranteRepository.getRestaurantesFiltradosPorTipos(types)
                _restaurantes.value = restaurantesEncontrados
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun getRestaurantesPorNivelPrecio(priceLevel: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val restaurantesEncontrados = RestauranteRepository.getRestaurantesPorNivelPrecio(priceLevel)
                _restaurantes.value = restaurantesEncontrados
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }

        }
    }
    fun getRestaurantesPorRelevancia() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val lista = RestauranteRepository.fetchRestaurantesPorRelevancia()
                _restaurantes.value = lista
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getRestaurantesPorValoracion() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val lista = RestauranteRepository.fetchRestaurantesPorValoracion()
                _restaurantes.value = lista
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

}



