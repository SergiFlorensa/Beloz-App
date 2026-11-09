package com.app.beloz

import com.app.beloz.data.models.*
import com.app.beloz.innovacion.decision.*
import org.junit.Assert.*
import org.junit.Test

class CatalogTest {
    private fun restaurant(id: Int, name: String, food: String = "Pasta", price: String = "€", wait: Int = 10) =
        Restaurante(id, name, null, wait, price, food, "España", false, null, 0, 0.0)
    private val catalog = listOf(restaurant(1, "La Tagliatella"), restaurant(2, "Café Central", "Tapas"),
        restaurant(3, "Kokoro", "Sushi"), restaurant(4, "El Patio", "Combinados"))

    @Test fun prefixSearchNormalizesAccentsAndMatchesNameWords() {
        assertEquals(listOf(1), CatalogSearch.search(catalog, "tagli").map { it.restauranteId })
        assertEquals(listOf(2), CatalogSearch.search(catalog, "C").map { it.restauranteId })
        assertEquals(listOf(2), CatalogSearch.search(catalog, "cafe").map { it.restauranteId })
        assertEquals(listOf(3), CatalogSearch.search(catalog, "sus").map { it.restauranteId })
        assertTrue(CatalogSearch.search(catalog, "zzzz").isEmpty())
        assertTrue(CatalogSearch.search(catalog, "%").isEmpty())
    }
    @Test fun filtersExcludeUnknownPricesAndUnrelatedCuisine() {
        assertEquals(listOf(3), CatalogSearch.filterCuisine(catalog, listOf("Sushi")).map { it.restauranteId })
        assertNull(CatalogSearch.priceTier("?"))
        assertTrue(CatalogSearch.filterPrice(listOf(restaurant(4, "Unknown", price = "?")), "Barato").isEmpty())
    }
    @Test fun decisionDoesNotInventWaitsOrPrices() {
        val rows = listOf(restaurant(1, "Unknown", price = "?", wait = 0), restaurant(2, "Known", price = "€€", wait = 20))
        assertEquals(listOf(2), RestaurantDecision.choose(rows, DecisionMode.PRICE).map { it.restaurant.restauranteId })
        assertEquals(listOf(2), RestaurantDecision.choose(rows, DecisionMode.QUICK).map { it.restaurant.restauranteId })
    }
    @Test fun sectionsMergeCaseAndWhitespaceAndDeduplicateDishes() {
        val dish = Plato(1, "Ramen", "", 10.0, null, 1, " RAMEN ")
        val sections = listOf(dish, dish, dish.copy(id = 2, category = "ramen"), dish.copy(id = 3, category = null)).menuSections()
        assertEquals(2, sections.size)
        assertEquals(2, sections["RAMEN"]?.size)
        assertEquals(1, sections["Carta"]?.size)
    }
}
