package com.app.beloz.apis.services

import com.app.beloz.data.models.Plato
import com.app.beloz.data.models.Restaurante
import com.app.beloz.data.remote.BelozApiClient

class RestauranteService {
    private val restauranteApi: RestauranteApi by lazy {
        BelozApiClient.retrofit.create(RestauranteApi::class.java)
    }

    suspend fun fetchRestaurantes(): List<Restaurante> {
        return restauranteApi.fetchRestaurantes()
    }

    suspend fun fetchRestaurantesByCountry(country: String): List<Restaurante> {
        return restauranteApi.fetchRestaurantes(country = country)
    }

    suspend fun fetchRestaurantesPopulares(): List<Restaurante> {
        return restauranteApi.fetchPopulares()
    }

    suspend fun searchRestaurantes(query: String): List<Restaurante> {
        return restauranteApi.searchRestaurantes(query)
    }

    suspend fun getRestaurantesFiltradosPorTipos(types: List<String>): List<Restaurante> {
        if (types.isEmpty()) return emptyList()
        return restauranteApi.fetchRestaurantesPorTipos(types.joinToString(","))
    }

    suspend fun getRestaurantesPorNivelPrecio(priceLevel: String): List<Restaurante> {
        return restauranteApi.fetchRestaurantes()
    }

    suspend fun fetchRestaurantesPorValoracion(): List<Restaurante> {
        return restauranteApi.fetchRestaurantes()
    }

    suspend fun fetchRestaurantesPorRelevancia(): List<Restaurante> {
        return restauranteApi.fetchPopulares()
    }

    suspend fun fetchRestaurantesInteres(): List<Restaurante> {
        return restauranteApi.fetchPopulares()
    }

    suspend fun fetchPlatos(restauranteId: Int): List<Plato> {
        return restauranteApi.fetchPlatos(restauranteId)
    }
}
