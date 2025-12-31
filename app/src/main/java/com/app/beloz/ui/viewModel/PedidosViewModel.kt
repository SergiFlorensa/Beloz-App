package com.app.beloz.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.beloz.data.models.Pedido
import com.app.beloz.data.models.DetallePedido
import com.app.beloz.data.repositories.PedidosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PedidosViewModel : ViewModel() {

    private val pedidosRepository = PedidosRepository

    private val _pedidos = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidos: StateFlow<List<Pedido>> = _pedidos

    private val _detallesPedido = MutableStateFlow<List<DetallePedido>>(emptyList())
    val detallesPedido: StateFlow<List<DetallePedido>> = _detallesPedido

    private val _nombreRestaurante = MutableStateFlow<String?>(null)
    val nombreRestaurante: StateFlow<String?> = _nombreRestaurante

    fun cargarPedidos(userId: Int) {
        viewModelScope.launch {
            try {
                _pedidos.value = pedidosRepository.getPedidosPorUsuario(userId)
            } catch (e: Exception) {
                println("Error al cargar los pedidos: ${e.message}")
            }
        }
    }

    fun cargarDetallesPedido(pedidoId: Int) {
        viewModelScope.launch {
            try {
                val detalles = pedidosRepository.getDetallePedido(pedidoId)
                if (detalles.isNotEmpty()) {
                    _nombreRestaurante.value = detalles.first().restauranteNombre
                }
                _detallesPedido.value = detalles
            } catch (e: Exception) {
                println("Error al cargar detalles del pedido: ${e.message}")
            }
        }
    }
    fun limpiarPedidos() {
        _pedidos.value = emptyList()
        _detallesPedido.value = emptyList()
        _nombreRestaurante.value = null
    }
}

