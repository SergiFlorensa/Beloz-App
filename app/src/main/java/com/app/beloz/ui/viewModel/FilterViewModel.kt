package com.app.beloz.ui.viewModel

import androidx.lifecycle.ViewModel
import com.app.beloz.R

class FilterViewModel : ViewModel() {
    val foodTypeOptions = listOf(
        mapOf("label" to "Pizza", "iconPath" to R.drawable.pizzaa),
        mapOf("label" to "Kebab", "iconPath" to R.drawable.kebab3d),
        mapOf("label" to "Ramen", "iconPath" to R.drawable.ramen3d),
        mapOf("label" to "Nachos", "iconPath" to R.drawable.nachos),
        mapOf("label" to "Americana", "iconPath" to R.drawable.costillas),
        mapOf("label" to "Arabe", "iconPath" to R.drawable.tajin),
        mapOf("label" to "Sushi", "iconPath" to R.drawable.sushi),
        mapOf("label" to "India", "iconPath" to R.drawable.indian),
        mapOf("label" to "Poke", "iconPath" to R.drawable.pokebo),
        mapOf("label" to "Peruana", "iconPath" to R.drawable.ceviche),
        mapOf("label" to "Hamburguesas", "iconPath" to R.drawable.burg),

    )

    val priceOptions = listOf(
        mapOf("label" to "Barato", "iconPath" to R.drawable.euro),
        mapOf("label" to "Medio", "iconPath" to R.drawable.euro),
        mapOf("label" to "Caro", "iconPath" to R.drawable.euro)
    )

    val sortOptions = listOf(
        mapOf("label" to "Relevancia", "iconPath" to R.drawable.popularidad),
        mapOf("label" to "Valoración", "iconPath" to R.drawable.pulgar),
    )
}
