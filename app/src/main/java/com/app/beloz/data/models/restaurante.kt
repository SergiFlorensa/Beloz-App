package com.app.beloz.data.models

import com.google.gson.annotations.SerializedName
import java.text.Normalizer
import java.util.Locale

data class Restaurante(
    @SerializedName(value = "restaurante_id", alternate = ["id"]) val restauranteId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("image_path") val imagePath: String?,
    @SerializedName("wait_time") val waitTime: Int,
    @SerializedName("price_level") val priceLevel: String,
    @SerializedName("type_of_food") val typeOfFood: String,
    @SerializedName("country") val country: String,
    @SerializedName("es_popular") val esPopular: Boolean,
    @SerializedName("logo_restaurante") val logoRestaurante: String?,
    @SerializedName("relevancia") val relevancia: Int,
    @SerializedName("valoracion") val valoracion: Double,
    var recomendacionMotivo: String? = null
)

/** Shared matching for suggestions, full results and cuisine filters. */
object CatalogSearch {
    fun normalize(text: String): String = Normalizer.normalize(text, Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "")
        .lowercase(Locale.ROOT)
        .replace(Regex("[^\\p{L}\\p{N}]+"), " ")
        .trim()

    fun rank(name: String, keywords: String, query: String): Int? {
        val normalizedQuery = normalize(query)
        if (normalizedQuery.isEmpty()) return null
        val normalizedName = normalize(name)
        val normalizedKeywords = normalize(keywords)
        val queryTokens = normalizedQuery.split(' ')
        val nameTokens = normalizedName.split(' ')
        val keywordTokens = normalizedKeywords.split(' ')
        fun matches(tokens: List<String>) = queryTokens.all { queryToken ->
            tokens.any { it.startsWith(queryToken) }
        }
        return when {
            normalizedName == normalizedQuery -> 0
            normalizedName.startsWith(normalizedQuery) -> 1
            matches(nameTokens) -> 2
            normalizedQuery.length > 1 && matches(keywordTokens) -> 3
            normalizedQuery.length > 1 && matches(nameTokens + keywordTokens) -> 4
            else -> null
        }
    }

    fun search(restaurantes: List<Restaurante>, query: String): List<Restaurante> = restaurantes
        .mapNotNull { restaurant ->
            rank(restaurant.name, restaurant.typeOfFood, query)
                ?.let { restaurant to it }
        }
        .sortedWith(compareBy<Pair<Restaurante, Int>> { it.second }
            .thenBy { normalize(it.first.name) }.thenBy { it.first.restauranteId })
        .map { it.first }
        .distinctBy { it.restauranteId }

    fun priceTier(value: String): Int? = when (value.trim().lowercase(Locale.ROOT)) {
        "barato", "bajo", "economico", "económico", "€", "$" -> 1
        "medio", "moderado", "€€", "$$" -> 2
        "caro", "alto", "€€€", "$$$" -> 3
        else -> null
    }

    fun filterPrice(restaurantes: List<Restaurante>, value: String): List<Restaurante> {
        val tier = priceTier(value) ?: return emptyList()
        return restaurantes.filter { priceTier(it.priceLevel) == tier }
    }

    fun filterCuisine(restaurantes: List<Restaurante>, types: List<String>): List<Restaurante> =
        restaurantes.filter { restaurant -> types.any { rank(restaurant.typeOfFood, "", it) != null } }

    fun sort(restaurantes: List<Restaurante>, order: String): List<Restaurante> = when (order) {
        "nombre" -> restaurantes.sortedBy { normalize(it.name) }
        "espera" -> restaurantes.sortedWith(compareBy<Restaurante> {
            it.waitTime.takeIf { minutes -> minutes > 0 } ?: Int.MAX_VALUE
        }.thenBy { normalize(it.name) })
        else -> restaurantes
    }
}
