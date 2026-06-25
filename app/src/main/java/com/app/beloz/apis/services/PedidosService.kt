package com.app.beloz.apis.services

import com.app.beloz.apis.services.PedidosApi.CrearPedidoRequest
import com.app.beloz.data.models.DetallePedidoSupabase
import com.app.beloz.data.models.Pedido
import com.app.beloz.data.remote.BelozApiClient

class PedidosService {
    private val pedidosApi: PedidosApi by lazy {
        BelozApiClient.retrofit.create(PedidosApi::class.java)
    }

    suspend fun crearPedido(pedidoRequest: CrearPedidoRequest): Pedido? {
        return pedidosApi.crearPedido(pedidoRequest)
    }

    suspend fun crearDetalles(pedidoId: Int, detalles: List<PedidosApi.DetallePedidoInsertRequest>) {
        if (detalles.isEmpty()) return
        pedidosApi.crearDetalles(pedidoId, detalles)
    }

    suspend fun getPedidosPorUsuario(userId: Int): List<Pedido> {
        return pedidosApi.getPedidosPorUsuario(userId = userId)
    }

    suspend fun getDetallePedido(pedidoId: Int): List<DetallePedidoSupabase> {
        return pedidosApi.getDetallePedido(pedidoId = pedidoId)
    }
}
