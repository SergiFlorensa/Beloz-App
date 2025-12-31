package com.app.beloz.apis.services

import com.app.beloz.apis.services.PedidosApi.CrearPedidoRequest
import com.app.beloz.data.models.DetallePedido
import com.app.beloz.data.models.Pedido
import com.app.beloz.data.remote.SupabaseClient

class PedidosService {
    private val pedidosApi: PedidosApi by lazy {
        SupabaseClient.retrofit.create(PedidosApi::class.java)
    }

    suspend fun crearPedido(pedidoRequest: CrearPedidoRequest): Pedido? {
        val response = pedidosApi.crearPedido(pedidoRequest)
        return response.firstOrNull()
    }

    suspend fun crearDetalles(detalles: List<PedidosApi.DetallePedidoInsertRequest>) {
        if (detalles.isEmpty()) return
        pedidosApi.crearDetalles(detalles)
    }

    suspend fun getPedidosPorUsuario(userId: Int): List<Pedido> {
        return pedidosApi.getPedidosPorUsuario(userId = "eq.$userId", order = "fecha.desc")
    }

    suspend fun getDetallePedido(pedidoId: Int): List<DetallePedido> {
        return pedidosApi.getDetallePedido(pedidoId = "eq.$pedidoId")
    }
}
