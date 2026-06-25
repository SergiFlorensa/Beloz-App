package com.app.beloz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.beloz.ui.components.BelozColors
import com.app.beloz.ui.components.BelozTopAppBar
import com.app.beloz.ui.components.DetallePedidoItem
import com.app.beloz.ui.viewModel.AuthViewModel
import com.app.beloz.ui.viewModel.PedidosViewModel

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
            modifier = Modifier
                .fillMaxSize()
                .background(BelozColors.MintSurface),
            contentAlignment = Alignment.Center
        ) {
            Text("Debes iniciar sesion para ver los detalles del pedido.", color = BelozColors.MutedText)
        }
    } else {
        LaunchedEffect(pedidoId) {
            viewModel.cargarDetallesPedido(pedidoId)
        }

        Scaffold(
            containerColor = BelozColors.MintSurface,
            topBar = {
                BelozTopAppBar(
                    title = "Detalle pedido",
                    subtitle = nombreRestaurante ?: "Resumen del restaurante",
                    navController = navController
                )
            }
        ) { paddingValues ->
            if (detalles.isEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(BelozColors.MintSurface),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BelozColors.Green)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(BelozColors.MintSurface)
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Column {
                            Text(
                                text = nombreRestaurante ?: "Restaurante desconocido",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                color = BelozColors.Ink
                            )
                            Text(
                                text = "Platos incluidos en este pedido",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BelozColors.MutedText
                            )
                        }
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
