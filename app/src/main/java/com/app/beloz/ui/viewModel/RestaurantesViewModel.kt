package com.app.beloz.ui.viewModel



import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.app.beloz.data.models.Restaurante
import com.app.beloz.data.repositories.RestauranteRepository
import com.app.beloz.innovacion.perfil.PerfilSabor
import com.app.beloz.innovacion.perfil.PerfilSaborRepository

class RestaurantesViewModel(application: Application) : AndroidViewModel(application) {
    private val _restaurantes = MutableStateFlow<List<Restaurante>>(emptyList())
    val restaurantes: StateFlow<List<Restaurante>> = _restaurantes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val perfilRepo = PerfilSaborRepository(application)
    private var ultimoPerfil = PerfilSabor()

    init {
        viewModelScope.launch {
            perfilRepo.perfilFlow().collect { perfil ->
                ultimoPerfil = perfil
            }
        }
        cargarRestaurantes()
    }

    fun cargarRestaurantes() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val restaurantesCargados = RestauranteRepository.fetchRestaurantes()
                _restaurantes.value = aplicarPerfil(restaurantesCargados, ultimoPerfil)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun buscarRestaurantes(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            if (query.isNotBlank()) {
                _restaurantes.value = emptyList()
                try {
                    val restaurantesEncontrados = RestauranteRepository.searchRestaurantes(query)
                    _restaurantes.value = ordenarBusquedaPorTexto(restaurantesEncontrados, query)
                } catch (e: Exception) {
                    _restaurantes.value = emptyList()
                    _errorMessage.value = e.message
                } finally {
                    _isLoading.value = false
                }
            } else {
                _restaurantes.value = emptyList()
                _isLoading.value = false
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

    private fun aplicarPerfil(restaurantes: List<Restaurante>, perfil: PerfilSabor): List<Restaurante> {
        if (perfil.totalEventos == 0) return restaurantes

        val topTipo = perfil.topTiposComida.firstOrNull()?.clave
        val topRestauranteId = perfil.topRestaurantes.firstOrNull()?.clave

        val tipos = perfil.topTiposComida.associate { it.clave to it.conteo }
        val precios = perfil.topRangosPrecio.associate { it.clave to it.conteo }
        val favoritos = perfil.topRestaurantes.associate { it.clave to it.conteo }

        return restaurantes.map { restaurante ->
            var score = 0
            score += (tipos[restaurante.typeOfFood] ?: 0) * 2 // Weight food type more
            score += precios[restaurante.priceLevel] ?: 0
            score += (favoritos[restaurante.restauranteId.toString()] ?: 0) * 3 // Weight historical favorites most

            val motivo = when {
                restaurante.restauranteId.toString() == topRestauranteId -> "Tu favorito habitual"
                restaurante.typeOfFood == topTipo -> "Porque te gusta la comida $topTipo"
                tipos.containsKey(restaurante.typeOfFood) -> "Relacionado con tus gustos"
                else -> null
            }

            restaurante.copy(recomendacionMotivo = motivo).apply { 
                // We use a temporary score for sorting but we don't store it in the model
                // unless we add a score field. For now, we'll sort and return.
            }
        }.sortedWith(
            compareByDescending<Restaurante> { restaurante ->
                var score = 0
                score += (tipos[restaurante.typeOfFood] ?: 0) * 2
                score += precios[restaurante.priceLevel] ?: 0
                score += (favoritos[restaurante.restauranteId.toString()] ?: 0) * 3
                score
            }.thenByDescending { it.valoracion }
        )
    }

    private fun ordenarBusquedaPorTexto(restaurantes: List<Restaurante>, query: String): List<Restaurante> {
        val normalizedQuery = query.trim().lowercase()
        if (normalizedQuery.isBlank()) return restaurantes

        return restaurantes.sortedWith(
            compareBy<Restaurante> { restaurante ->
                val name = restaurante.name.lowercase()
                val type = restaurante.typeOfFood.lowercase()
                when {
                    name.startsWith(normalizedQuery) -> 0
                    type.startsWith(normalizedQuery) -> 1
                    name.contains(normalizedQuery) -> 2
                    type.contains(normalizedQuery) -> 3
                    else -> 4
                }
            }.thenBy { it.name.lowercase() }
        )
    }
}



