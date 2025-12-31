package com.app.beloz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.beloz.ui.components.PedidoRecienteCard
import com.app.beloz.ui.components.PedidoItem
import com.app.beloz.ui.viewModel.PedidosViewModel
import com.app.beloz.ui.viewModel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Pedidos(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: PedidosViewModel
) {
    val user = authViewModel.user

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pedidos", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        if (user == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF2F2F7)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Debes iniciar sesión para ver tus pedidos.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        } else {
            LaunchedEffect(user.idUser) {
                viewModel.cargarPedidos(user.idUser)
            }

            val pedidos by viewModel.pedidos.collectAsState()
            val pedidoReciente = pedidos.maxByOrNull { it.fecha }

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                if (pedidos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No tienes pedidos disponibles.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                } else {
                    if (pedidoReciente != null) {
                        Text(
                            text = "Pedido más reciente",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                        )
                        PedidoRecienteCard(
                            pedido = pedidoReciente,
                            onPedidoClick = { pedido ->
                                navController.navigate("detallePedido/${pedido.id}")
                            }
                        )
                    }

                    if (pedidos.isNotEmpty() && pedidos.any { it != pedidoReciente }) {
                        Text(
                            text = "Pedidos anteriores",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 16.dp, top = 24.dp)
                        )
                    }

                    LazyColumn {
                        items(pedidos.filter { it != pedidoReciente }) { pedido ->
                            PedidoItem(
                                pedido = pedido,
                                onPedidoClick = { selectedPedido ->
                                    navController.navigate("detallePedido/${selectedPedido.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
