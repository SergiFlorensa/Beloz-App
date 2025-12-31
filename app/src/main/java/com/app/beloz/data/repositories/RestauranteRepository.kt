package com.app.beloz.data.repositories

import com.app.beloz.apis.services.RestauranteService
import com.app.beloz.data.models.Restaurante
import retrofit2.HttpException

object RestauranteRepository {
    private const val baseUrl = "https://beloz-production.up.railway.app"
    private val restaurantService = RestauranteService(baseUrl)

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
        return restaurantService.searchRestaurantes(query)
    }
    suspend fun getRestaurantesFiltradosPorTipos(types: List<String>): List<Restaurante> {
        return restaurantService.getRestaurantesFiltradosPorTipos(types)
    }
    suspend fun getRestaurantesPorNivelPrecio(priceLevel: String): List<Restaurante> {
        return restaurantService.getRestaurantesPorNivelPrecio(priceLevel)
    }
    suspend fun fetchRestaurantesPorValoracion(): List<Restaurante> {
        return restaurantService.fetchRestaurantesPorValoracion()
    }

    suspend fun fetchRestaurantesPorRelevancia(): List<Restaurante> {
        return restaurantService.fetchRestaurantesPorRelevancia()
    }
    suspend fun fetchRestaurantesInteres(): List<Restaurante> {
        return restaurantService.fetchRestaurantesInteres()
    }

}
