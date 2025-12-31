package com.app.beloz.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.beloz.apis.services.PedidosApi
import com.app.beloz.data.models.User
import com.app.beloz.data.models.Plato
import com.app.beloz.data.repositories.PedidosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {

    private val _carrito = MutableStateFlow<List<Pair<Plato, Int>>>(emptyList())
    val carrito: StateFlow<List<Pair<Plato, Int>>> = _carrito

    private val _restauranteId = MutableStateFlow<Int?>(null)
    val restauranteId: StateFlow<Int?> = _restauranteId

    var currentUser: User? = null

    private val _itemCount = MutableStateFlow(0)
    val itemCount: StateFlow<Int> = _itemCount

    init {
        viewModelScope.launch {
            carrito.collect { items ->
                _itemCount.value = items.sumOf { (_, cantidad) -> cantidad }
            }
        }
    }

    fun addToCart(plato: Plato, cantidad: Int, restauranteId: Int): Boolean {
        val currentRestauranteId = _restauranteId.value
        if (currentRestauranteId == null || currentRestauranteId == restauranteId) {
            _restauranteId.value = restauranteId
            val currentCart = _carrito.value.toMutableList()
            val existingItemIndex = currentCart.indexOfFirst { it.first.id == plato.id }
            if (existingItemIndex >= 0) {
                val existingItem = currentCart[existingItemIndex]
                currentCart[existingItemIndex] =
                    existingItem.copy(second = existingItem.second + cantidad)
            } else {
                currentCart.add(plato to cantidad)
            }
            _carrito.value = currentCart
            return true
        } else {
            return false
        }
    }

    fun removeFromCart(plato: Plato) {
        val currentCart = _carrito.value.toMutableList()
        currentCart.removeAll { it.first.id == plato.id }
        _carrito.value = currentCart

    if (_carrito.value.isEmpty()){
        _restauranteId.value = null
    }

    }

    fun clearCart() {
        _carrito.value = emptyList()
        _restauranteId.value = null
    }

    fun getTotalPrice(): Double {
        return _carrito.value.sumOf { (plato, cantidad) -> plato.price * cantidad }
    }


    fun confirmPurchase(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = currentUser?.idUser ?: return onError("Usuario no autenticado")
        val restaurantId = _restauranteId.value ?: return onError("Restaurante no seleccionado")

        val detalles = _carrito.value.map { (plato, cantidad) ->
            PedidosApi.DetallePedidoRequest(
                platoId = plato.id,
                cantidad = cantidad,
                precio = plato.price
            )
        }

        viewModelScope.launch {
            try {
                val pedido = PedidosRepository.crearPedido(userId, restaurantId, detalles)
                if (pedido != null) {
                    clearCart()
                    onSuccess()
                } else {
                    onError("Error al crear el pedido en el servidor")
                }
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }
}
