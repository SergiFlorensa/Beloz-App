package com.app.beloz.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.beloz.data.models.CatalogSearch
import com.app.beloz.data.models.Restaurante
import com.app.beloz.data.repositories.RestauranteRepository
import com.app.beloz.innovacion.perfil.PerfilSabor
import com.app.beloz.innovacion.perfil.PerfilSaborRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RestaurantesViewModel(application: Application) : AndroidViewModel(application) {
    private val _catalog = MutableStateFlow<List<Restaurante>>(emptyList())
    val catalog: StateFlow<List<Restaurante>> = _catalog.asStateFlow()
    private val _catalogIsLoading = MutableStateFlow(false)
    val catalogIsLoading = _catalogIsLoading.asStateFlow()
    private val _catalogError = MutableStateFlow<String?>(null)
    val catalogError = _catalogError.asStateFlow()

    private val _restaurantes = MutableStateFlow<List<Restaurante>>(emptyList())
    val restaurantes = _restaurantes.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val perfilRepo = PerfilSaborRepository(application)
    private var ultimoPerfil = PerfilSabor()
    private var catalogLoaded = false
    private var catalogJob: Job? = null
    private var resultsJob: Job? = null
    private var resultsVersion = 0L

    init {
        viewModelScope.launch {
            perfilRepo.perfilFlow().collect { ultimoPerfil = it }
        }
        cargarRestaurantes()
    }

    fun cargarRestaurantes() {
        val version = ++resultsVersion
        resultsJob?.cancel()
        catalogJob?.cancel()
        _isLoading.value = true
        _errorMessage.value = null
        _catalogIsLoading.value = true
        _catalogError.value = null
        catalogJob = viewModelScope.launch {
            try {
                val loaded = aplicarPerfil(RestauranteRepository.fetchRestaurantes(), ultimoPerfil)
                ensureActive()
                _catalog.value = loaded
                catalogLoaded = true
                // A catalog refresh must not overwrite a newer search or filter.
                if (version == resultsVersion) _restaurantes.value = loaded
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _catalogError.value = LOAD_ERROR
                if (version == resultsVersion) _errorMessage.value = LOAD_ERROR
            } finally {
                if (coroutineContext[Job]?.isActive == true) {
                    _catalogIsLoading.value = false
                    if (version == resultsVersion) _isLoading.value = false
                }
            }
        }
    }

    fun buscarRestaurantes(query: String) {
        if (CatalogSearch.normalize(query).isEmpty()) {
            ++resultsVersion
            resultsJob?.cancel()
            _restaurantes.value = emptyList()
            _isLoading.value = false
            _errorMessage.value = null
            return
        }
        loadResults { CatalogSearch.search(availableCatalog(), query) }
    }

    fun getRestaurantesFiltradosPorTipos(types: List<String>) =
        loadResults { CatalogSearch.filterCuisine(availableCatalog(), types) }

    fun getRestaurantesPorNivelPrecio(priceLevel: String) =
        loadResults { CatalogSearch.filterPrice(availableCatalog(), priceLevel) }

    fun getRestaurantesPorRelevancia() =
        loadResults { availableCatalog().sortedByDescending { it.relevancia } }

    fun getRestaurantesPorValoracion() = loadResults {
        availableCatalog().filter { it.valoracion.isFinite() && it.valoracion > 0.0 && it.valoracion <= 5.0 }
            .sortedByDescending { it.valoracion }
    }

    private suspend fun availableCatalog(): List<Restaurante> {
        catalogJob?.join()
        return if (catalogLoaded) _catalog.value else RestauranteRepository.fetchRestaurantes()
    }

    private fun loadResults(fetch: suspend () -> List<Restaurante>) {
        val version = ++resultsVersion
        resultsJob?.cancel()
        _restaurantes.value = emptyList()
        _isLoading.value = true
        _errorMessage.value = null
        resultsJob = viewModelScope.launch {
            try {
                val results = fetch()
                ensureActive()
                if (version == resultsVersion) _restaurantes.value = results
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                if (version == resultsVersion) _errorMessage.value = LOAD_ERROR
            } finally {
                if (version == resultsVersion) _isLoading.value = false
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
        return restaurantes.map { restaurant ->
            restaurant.copy(recomendacionMotivo = when {
                restaurant.restauranteId.toString() == topRestauranteId -> "Entre tus visitas habituales"
                restaurant.typeOfFood == topTipo -> "Por tu interes en $topTipo"
                tipos.containsKey(restaurant.typeOfFood) -> "Relacionado con tus gustos"
                else -> null
            })
        }.sortedByDescending { restaurant ->
            (tipos[restaurant.typeOfFood] ?: 0) * 2 +
                (precios[restaurant.priceLevel] ?: 0) +
                (favoritos[restaurant.restauranteId.toString()] ?: 0) * 3
        }
    }

    private companion object {
        const val LOAD_ERROR = "No se han podido cargar los restaurantes. Comprueba la conexion y vuelve a intentarlo."
    }
}



