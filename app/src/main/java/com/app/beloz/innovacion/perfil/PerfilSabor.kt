package com.app.beloz.innovacion.perfil

data class Conteo(
    val clave: String,
    val conteo: Int
)

data class PerfilSabor(
    val totalEventos: Int = 0,
    val topTiposComida: List<Conteo> = emptyList(),
    val topRangosPrecio: List<Conteo> = emptyList(),
    val topRestaurantes: List<Conteo> = emptyList()
) {
    companion object {
        fun fromEventos(eventos: List<EventoUso>): PerfilSabor {
            if (eventos.isEmpty()) return PerfilSabor()

            val tipos = mutableMapOf<String, Int>()
            val precios = mutableMapOf<String, Int>()
            val restaurantes = mutableMapOf<String, Int>()

            for (evento in eventos) {
                val peso = pesoEvento(evento.tipo)
                val tipoComida = evento.metadata["food_type"]
                if (!tipoComida.isNullOrBlank()) {
                    tipos[tipoComida] = (tipos[tipoComida] ?: 0) + peso
                }
                val precio = evento.metadata["price_level"]
                if (!precio.isNullOrBlank()) {
                    precios[precio] = (precios[precio] ?: 0) + peso
                }
                val restauranteId = evento.metadata["restaurant_id"]
                if (!restauranteId.isNullOrBlank()) {
                    restaurantes[restauranteId] = (restaurantes[restauranteId] ?: 0) + peso
                }
            }

            return PerfilSabor(
                totalEventos = eventos.size,
                topTiposComida = top(tipos, 5),
                topRangosPrecio = top(precios, 3),
                topRestaurantes = top(restaurantes, 5)
            )
        }

        private fun top(map: Map<String, Int>, limit: Int): List<Conteo> {
            if (map.isEmpty()) return emptyList()
            return map.entries
                .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
                .take(limit)
                .map { Conteo(it.key, it.value) }
        }

        private fun pesoEvento(tipo: TipoEvento): Int {
            return when (tipo) {
                TipoEvento.VIEW_RESTAURANT -> 1
                TipoEvento.VIEW_DISH -> 1
                TipoEvento.ADD_TO_CART -> 3
                TipoEvento.CHECKOUT -> 4
                TipoEvento.PURCHASE -> 5
            }
        }
    }
}
