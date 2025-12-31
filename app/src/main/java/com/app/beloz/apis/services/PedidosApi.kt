package com.app.beloz.apis.services

import com.app.beloz.data.models.Pedido
import com.app.beloz.data.models.DetallePedido
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PedidosApi {
    @POST("api/pedidos/crear")
    suspend fun crearPedido(@Body pedido: CrearPedidoRequest): Response<Pedido>

    @GET("api/pedidos")
    suspend fun getPedidosPorUsuario(@Query("user_id") userId: Int): Response<List<Pedido>>

    @GET("api/pedidos/{pedidoId}")
    suspend fun getDetallePedido(@Path("pedidoId") pedidoId: Int): Response<List<DetallePedido>>

    data class CrearPedidoRequest(
        val userId: Int,
        val restaurantId: Int,
        val detalles: List<DetallePedidoRequest>
    )

    @Serializable
    data class DetallePedidoRequest(
        val platoId: Int,
        val cantidad: Int,
        val precio: Double
    )
}
