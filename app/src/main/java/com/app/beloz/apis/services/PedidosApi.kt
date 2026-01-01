package com.app.beloz.apis.services

import com.app.beloz.data.models.DetallePedidoSupabase
import com.app.beloz.data.models.Pedido
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface PedidosApi {
    @Headers("Prefer: return=representation")
    @POST("pedidos")
    suspend fun crearPedido(@Body pedido: CrearPedidoRequest): List<Pedido>

    @Headers("Prefer: return=minimal")
    @POST("detalle_pedido")
    suspend fun crearDetalles(@Body detalles: List<DetallePedidoInsertRequest>)

    @GET("pedidos")
    suspend fun getPedidosPorUsuario(
        @Query("user_id") userId: String,
        @Query("order") order: String? = null
    ): List<Pedido>

    @GET("detalle_pedido")
    suspend fun getDetallePedido(
        @Query("pedido_id") pedidoId: String,
        @Query("select") select: String = "id_detalle,pedido_id,plato_id,cantidad,precio,platos(name,restaurante(name))"
    ): List<DetallePedidoSupabase>

    data class CrearPedidoRequest(
        @SerializedName("user_id") val userId: Int,
        @SerializedName("restaurant_id") val restaurantId: Int,
        @SerializedName("total") val total: Double
    )

    data class DetallePedidoInsertRequest(
        @SerializedName("pedido_id") val pedidoId: Int,
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
