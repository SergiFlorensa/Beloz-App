package com.app.beloz.apis.services

import com.app.beloz.data.models.Restaurante
import com.app.beloz.data.remote.SupabaseClient

class RestauranteService {
    private val restauranteApi: RestauranteApi by lazy {
        SupabaseClient.retrofit.create(RestauranteApi::class.java)
    }

    suspend fun fetchRestaurantes(): List<Restaurante> {
        return restauranteApi.fetchRestaurantes()
    }

    suspend fun fetchRestaurantesByCountry(country: String): List<Restaurante> {
        return restauranteApi.fetchRestaurantes(country = "eq.$country")
    }

    suspend fun fetchRestaurantesPopulares(): List<Restaurante> {
        return restauranteApi.fetchRestaurantes(
            esPopular = "eq.true",
            order = "relevancia.desc"
        )
    }

    suspend fun searchRestaurantes(query: String): List<Restaurante> {
        val clean = query.trim()
        if (clean.isBlank()) return emptyList()
        val or = "(name.ilike.*$clean*,type_of_food.ilike.*$clean*)"
        return restauranteApi.fetchRestaurantes(or = or)
    }

    suspend fun getRestaurantesFiltradosPorTipos(types: List<String>): List<Restaurante> {
        return restauranteApi.fetchRestaurantes(typeOfFood = buildInFilter(types))
    }
    suspend fun getRestaurantesPorNivelPrecio(priceLevel: String): List<Restaurante> {
        return restauranteApi.fetchRestaurantes(priceLevel = "eq.$priceLevel")
    }
    suspend fun fetchRestaurantesPorValoracion(): List<Restaurante> {
        return restauranteApi.fetchRestaurantes(order = "valoracion.desc")
    }

    suspend fun fetchRestaurantesPorRelevancia(): List<Restaurante> {
        return restauranteApi.fetchRestaurantes(order = "relevancia.desc")
    }
    suspend fun fetchRestaurantesInteres(): List<Restaurante> {
        return restauranteApi.fetchRestaurantes(order = "relevancia.desc", limit = 10)
    }

    private fun buildInFilter(values: List<String>): String {
        val encoded = values.map { value ->
            val cleaned = value.trim()
            if (cleaned.any { !it.isLetterOrDigit() && it != '_' && it != '-' }) {
                "\"${cleaned.replace("\"", "\\\"")}\""
            } else {
                cleaned
            }
        }
        return "in.(${encoded.joinToString(",")})"
    }
}
