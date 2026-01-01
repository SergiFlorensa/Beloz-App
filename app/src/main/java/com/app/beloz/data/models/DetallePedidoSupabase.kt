package com.app.beloz.data.models

import com.google.gson.annotations.SerializedName

data class DetallePedidoSupabase(
    @SerializedName("id_detalle") val idDetalle: Int,
    @SerializedName("pedido_id") val pedidoId: Int,
    @SerializedName("plato_id") val platoId: Int,
    @SerializedName("cantidad") val cantidad: Int,
    @SerializedName("precio") val precio: Double,
    @SerializedName("plato_nombre") val platoNombre: String? = null,
    @SerializedName("restaurante_nombre") val restauranteNombre: String? = null,
    @SerializedName("platos") val plato: PlatoDetalle? = null
)

data class PlatoDetalle(
    @SerializedName("name") val name: String? = null,
    @SerializedName("restaurante") val restaurante: RestauranteDetalle? = null
)

data class RestauranteDetalle(
    @SerializedName("name") val name: String? = null
)
