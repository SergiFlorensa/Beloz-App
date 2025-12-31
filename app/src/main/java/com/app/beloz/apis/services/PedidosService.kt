package com.app.beloz.apis.services

import com.app.beloz.data.models.Pedido
import com.app.beloz.data.models.DetallePedido
import com.app.beloz.apis.services.PedidosApi.CrearPedidoRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class PedidosService(baseUrl: String) {

    private val baseUrl = "https://beloz-production.up.railway.app/"
    private val retrofit: Retrofit by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private val pedidosApi: PedidosApi by lazy {
        retrofit.create(PedidosApi::class.java)
    }
    suspend fun crearPedido(pedidoRequest: CrearPedidoRequest): Pedido? {
        println("Enviando solicitud a $baseUrl/api/pedidos/crear con datos: $pedidoRequest")
        val response = pedidosApi.crearPedido(pedidoRequest)
        if (response.isSuccessful) {
            return response.body()
        } else {
            println("Error: Código ${response.code()} - ${response.errorBody()?.string()}")
            throw Exception("Error al crear el pedido: Código ${response.code()} - ${response.message()}")
        }
    }
    suspend fun getPedidosPorUsuario(userId: Int) = pedidosApi.getPedidosPorUsuario(userId)
    suspend fun getDetallePedido(pedidoId: Int) = pedidosApi.getDetallePedido(pedidoId)
}
