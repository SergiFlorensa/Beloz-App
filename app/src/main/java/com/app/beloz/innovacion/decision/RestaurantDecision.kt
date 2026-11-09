package com.app.beloz.innovacion.decision

import com.app.beloz.data.models.Restaurante
import java.util.Locale

enum class DecisionMode(val label: String) {
    DISCOVER("Descubrir"), QUICK("Menos espera"), PRICE("Precio suave")
}

data class RestaurantChoice(val restaurant: Restaurante, val reason: String)

object RestaurantDecision {
    fun priceRank(value: String?): Int? {
        val normalized = value?.trim()?.lowercase(Locale.ROOT).orEmpty()
        return when (normalized) {
            "€", "$", "bajo", "economico", "económico", "low" -> 1
            "€€", "$$", "medio", "moderado", "medium" -> 2
            "€€€", "$$$", "alto", "high" -> 3
            else -> null
        }
    }

    fun priceLabel(value: String?): String = when (priceRank(value)) {
        1 -> "€"
        2 -> "€€"
        3 -> "€€€"
        else -> "Sin dato"
    }

    fun choose(catalog: List<Restaurante>, mode: DecisionMode): List<RestaurantChoice> {
        val unique = catalog.distinctBy { it.restauranteId }
        val ordered = when (mode) {
            DecisionMode.QUICK -> unique.filter { it.waitTime > 0 }
                .sortedWith(compareBy<Restaurante> { it.waitTime }.thenBy { it.name })
            DecisionMode.PRICE -> unique.filter { priceRank(it.priceLevel) != null }
                .sortedWith(compareBy<Restaurante> { priceRank(it.priceLevel) }
                    .thenBy { if (it.waitTime > 0) it.waitTime else Int.MAX_VALUE }.thenBy { it.name })
            DecisionMode.DISCOVER -> {
                val alphabetical = unique.sortedBy { it.name.lowercase(Locale.ROOT) }
                val varied = alphabetical.distinctBy { it.typeOfFood.trim().lowercase(Locale.ROOT) }
                (varied + alphabetical).distinctBy { it.restauranteId }
            }
        }
        return ordered.take(3).map {
            val reason = when (mode) {
                DecisionMode.QUICK -> "De las esperas más cortas del catálogo"
                DecisionMode.PRICE -> "Nivel de precio orientativo: ${priceLabel(it.priceLevel)}"
                DecisionMode.DISCOVER -> it.typeOfFood.ifBlank { "Otra opción de Reus" }
            }
            RestaurantChoice(it, reason)
        }
    }
}
