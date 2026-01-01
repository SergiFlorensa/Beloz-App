package com.app.beloz.data.repositories

import com.app.beloz.apis.services.PedidosApi
import com.app.beloz.apis.services.PedidosService
import com.app.beloz.data.models.DetallePedido
import com.app.beloz.data.models.Pedido
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PedidosRepository {

    private val pedidosService by lazy { PedidosService() }

    suspend fun crearPedido(
        userId: Int,
        restaurantId: Int,
        detalles: List<PedidosApi.DetallePedidoRequest>
    ): Pedido? {
        return withContext(Dispatchers.IO) {
            try {
                val total = detalles.sumOf { it.precio * it.cantidad }
                val pedido = pedidosService.crearPedido(
                    PedidosApi.CrearPedidoRequest(
                        userId = userId,
                        restaurantId = restaurantId,
                        total = total
                    )
                )
                val pedidoId = pedido?.id
                if (pedidoId != null) {
                    val detallesInsert = detalles.map {
                        PedidosApi.DetallePedidoInsertRequest(
                            pedidoId = pedidoId,
                            platoId = it.platoId,
                            cantidad = it.cantidad,
                            precio = it.precio
                        )
                    }
                    pedidosService.crearDetalles(detallesInsert)
                }
                pedido
            } catch (e: Exception) {
                println("Error al crear pedido: ${e.message}")
                null
            }
        }
    }


    suspend fun getPedidosPorUsuario(userId: Int): List<Pedido> {
        return withContext(Dispatchers.IO) {
            pedidosService.getPedidosPorUsuario(userId)
        }
    }

    suspend fun getDetallePedido(pedidoId: Int): List<DetallePedido> {
        return withContext(Dispatchers.IO) {
            val detalles = pedidosService.getDetallePedido(pedidoId)
            detalles.map { detalle ->
                val nombrePlato = detalle.platoNombre
                    ?: detalle.plato?.name
                    ?: "Desconocido"
                val restauranteNombre = detalle.restauranteNombre
                    ?: detalle.plato?.restaurante?.name
                    ?: "Desconocido"
                DetallePedido(
                    idDetalle = detalle.idDetalle,
                    pedidoId = detalle.pedidoId,
                    platoId = detalle.platoId,
                    cantidad = detalle.cantidad,
                    precio = detalle.precio,
                    nombrePlato = nombrePlato,
                    restauranteNombre = restauranteNombre
                )
            }
        }
    }
}
