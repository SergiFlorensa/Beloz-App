package com.app.beloz.data.models

import com.google.gson.annotations.SerializedName

data class DetallePedido(
    @SerializedName("id_detalle")
    val idDetalle: Int,
    @SerializedName("pedido_id")
    val pedidoId: Int,
    @SerializedName("plato_id")
    val platoId: Int,
    @SerializedName("cantidad")
    val cantidad: Int,
    @SerializedName("precio")
    val precio: Double,
    @SerializedName("plato_nombre")
    val nombrePlato: String,
    @SerializedName("restaurante_nombre")
    val restauranteNombre: String
)
