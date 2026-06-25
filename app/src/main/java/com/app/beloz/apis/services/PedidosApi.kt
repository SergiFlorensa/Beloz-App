package com.app.beloz.apis.services

import com.app.beloz.data.models.DetallePedidoSupabase
import com.app.beloz.data.models.Pedido
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PedidosApi {
    @POST("api/pedidos/crear")
    suspend fun crearPedido(@Body pedido: CrearPedidoRequest): Pedido

    @POST("api/pedidos/{pedido_id}/detalles")
    suspend fun crearDetalles(
        @Path("pedido_id") pedidoId: Int,
        @Body detalles: List<DetallePedidoInsertRequest>
    ): List<DetallePedidoSupabase>

    @GET("api/pedidos")
    suspend fun getPedidosPorUsuario(
        @Query("user_id") userId: Int
    ): List<Pedido>

    @GET("api/pedidos/{pedido_id}/detalles")
    suspend fun getDetallePedido(
        @Path("pedido_id") pedidoId: Int
    ): List<DetallePedidoSupabase>

    data class CrearPedidoRequest(
        @SerializedName("user_id") val userId: Int,
        @SerializedName("restaurant_id") val restaurantId: Int,
        @SerializedName("total") val total: Double
    )

    data class DetallePedidoInsertRequest(
        @SerializedName("pedido_id") val pedidoId: Int? = null,
        @SerializedName("plato_id") val platoId: Int,
        @SerializedName("cantidad") val cantidad: Int,
        @SerializedName("precio") val precio: Double
    )

    @Serializable
    data class DetallePedidoRequest(
        val platoId: Int,
        val cantidad: Int,
        val precio: Double
    )
}
