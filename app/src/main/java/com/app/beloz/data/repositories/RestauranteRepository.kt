package com.app.beloz.data.repositories

import com.app.beloz.apis.services.RestauranteService
import com.app.beloz.data.models.Restaurante
import com.app.beloz.data.models.CatalogSearch
import retrofit2.HttpException

object RestauranteRepository {
    private val restaurantService = RestauranteService()

    suspend fun fetchRestaurantes(): List<Restaurante> {
        return restaurantService.fetchRestaurantes()
    }

    suspend fun fetchRestaurantesByCountry(country: String): List<Restaurante> {
        return try {
            restaurantService.fetchRestaurantesByCountry(country)
        } catch (e: HttpException) {
            if (e.code() == 404) {
                emptyList()
            } else {
                throw e
            }
        }
    }


    suspend fun fetchRestaurantesPopulares(): List<Restaurante> {
        return restaurantService.fetchRestaurantesPopulares()
    }

    suspend fun searchRestaurantes(query: String): List<Restaurante> {
        if (CatalogSearch.normalize(query).isEmpty()) return emptyList()
        return CatalogSearch.search(fetchRestaurantes(), query)
    }
    suspend fun getRestaurantesFiltradosPorTipos(types: List<String>): List<Restaurante> {
        return try {
            restaurantService.getRestaurantesFiltradosPorTipos(types)
        } catch (e: HttpException) {
            if (e.code() == 404) {
                emptyList()
            } else {
                throw e
            }
        }
    }
    suspend fun getRestaurantesPorNivelPrecio(priceLevel: String): List<Restaurante> {
        return CatalogSearch.filterPrice(fetchRestaurantes(), priceLevel)
    }
    suspend fun fetchRestaurantesPorValoracion(): List<Restaurante> {
        return fetchRestaurantes().filter { it.valoracion.isFinite() && it.valoracion > 0.0 && it.valoracion <= 5.0 }
            .sortedByDescending { it.valoracion }
    }

    suspend fun fetchRestaurantesPorRelevancia(): List<Restaurante> {
        return fetchRestaurantes().sortedByDescending { it.relevancia }
    }
    suspend fun fetchRestaurantesInteres(): List<Restaurante> {
        return restaurantService.fetchRestaurantesInteres()
    }

}
