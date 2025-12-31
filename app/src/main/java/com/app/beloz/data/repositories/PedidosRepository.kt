package com.app.beloz.data.repositories

import com.app.beloz.apis.services.PedidosApi
import com.app.beloz.apis.services.PedidosService
import com.app.beloz.data.models.Pedido
import com.app.beloz.data.models.DetallePedido
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PedidosRepository {

    private const val baseUrl = "https://beloz-production.up.railway.app/"
    private val pedidosService by lazy { PedidosService(baseUrl) }

    suspend fun crearPedido(
        userId: Int,
        restaurantId: Int,
        detalles: List<PedidosApi.DetallePedidoRequest>
    ): Pedido? {
        return withContext(Dispatchers.IO) {
            try {
                pedidosService.crearPedido(
                    PedidosApi.CrearPedidoRequest(
                        userId = userId,
                        restaurantId = restaurantId,
                        detalles = detalles
                    )
                )
            } catch (e: Exception) {
                println("Error al crear pedido: ${e.message}")
                null
            }
        }
    }


    suspend fun getPedidosPorUsuario(userId: Int): List<Pedido> {
        return withContext(Dispatchers.IO) {
            val response = pedidosService.getPedidosPorUsuario(userId)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                throw Exception("Error al obtener pedidos: ${response.message()}")
            }
        }
    }

    suspend fun getDetallePedido(pedidoId: Int): List<DetallePedido> {
        return withContext(Dispatchers.IO) {
            val response = pedidosService.getDetallePedido(pedidoId)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                throw Exception("Error al obtener detalles del pedido: ${response.message()}")
            }
        }
    }
}
