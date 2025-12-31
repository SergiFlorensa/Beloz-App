package com.app.beloz.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.beloz.ui.components.DetallePedidoItem
import com.app.beloz.ui.viewModel.AuthViewModel
import com.app.beloz.ui.viewModel.PedidosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallesPedido(
    navController: NavController,
    pedidoId: Int,
    viewModel: PedidosViewModel = viewModel(),
    authViewModel: AuthViewModel
) {
    val user = authViewModel.user
    val detalles by viewModel.detallesPedido.collectAsState()
    val nombreRestaurante by viewModel.nombreRestaurante.collectAsState()

    if (user == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Debes iniciar sesión para ver los detalles del pedido.")
        }
    } else {
        // Cargar los detalles del pedido
        LaunchedEffect(pedidoId) {
            viewModel.cargarDetallesPedido(pedidoId)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detalle del Pedido") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                        }
                    }
                )
            }
        ) { paddingValues ->
            if (detalles.isEmpty()) {
                // Mostrar mensaje mientras carga
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Cargando detalles del pedido...")
                }
            } else {
                // Mostrar nombre del restaurante y detalles del pedido en una lista
                LazyColumn(modifier = Modifier.padding(paddingValues)) {
                    item {
                        // Mostrar el nombre del restaurante en la parte superior
                        Text(
                            text = "Restaurante: ${nombreRestaurante ?: "Desconocido"}",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    items(detalles) { detalle ->
                        DetallePedidoItem(
                            nombre = detalle.nombrePlato,
                            cantidad = detalle.cantidad,
                            precio = detalle.precio
                        )
                    }
                }
            }
        }
    }
}
