package com.app.beloz.innovacion.perfil

enum class TipoEvento {
    VIEW_RESTAURANT,
    VIEW_DISH,
    ADD_TO_CART,
    CHECKOUT,
    PURCHASE
}

data class EventoUso(
    val tipo: TipoEvento,
    val timestampMs: Long = System.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap()
)
