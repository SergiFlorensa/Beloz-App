package com.app.beloz.data.models


import com.google.gson.annotations.SerializedName

data class Plato(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("image_path") val imagePath: String?,
    @SerializedName("restaurante_id") val restaurantId: Int,
    @SerializedName("category") val category: String? = null
)

fun List<Plato>.menuSections(): Map<String, List<Plato>> {
    val labels = linkedMapOf<String, String>()
    return distinctBy { it.id }.groupBy { plato ->
        val label = plato.category?.trim()?.takeIf { it.isNotEmpty() } ?: "Carta"
        labels.getOrPut(CatalogSearch.normalize(label)) { label }
    }
}
