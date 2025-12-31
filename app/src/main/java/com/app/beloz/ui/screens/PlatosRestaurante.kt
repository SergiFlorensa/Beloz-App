package com.app.beloz.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.app.beloz.ui.components.MainScaffold
import com.app.beloz.ui.components.PlatoCard
import com.app.beloz.ui.viewModel.CartViewModel
import com.app.beloz.ui.viewModel.PlatosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun PlatosRestaurante(
    restauranteId: Int,
    navController: NavController,
    viewModel: PlatosViewModel = remember { PlatosViewModel() },
    cartViewModel: CartViewModel
) {
    val platos by viewModel.platos.collectAsState()
    val carrito by cartViewModel.carrito.collectAsState()

    LaunchedEffect(restauranteId) {
        viewModel.cargarPlatosPorRestaurante(restauranteId)
    }

    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    MainScaffold(
        navController = navController,
        cartViewModel = cartViewModel,
        topBar = {
            TopAppBar(
                title = { Text("Platos del Restaurante", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF285346),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFF285346))
                .padding(16.dp)
        ) {
            if (platos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay platos disponibles para este restaurante",
                        modifier = Modifier.padding(16.dp),
                        color = Color.White
                    )
                }
            } else {
                LazyColumn {
                    items(platos) { plato ->
                        PlatoCard(
                            imagePath = plato.imagePath,
                            name = plato.name,
                            description = plato.description,
                            price = plato.price,
                            onAddToCart = { quantity ->
                                val added = cartViewModel.addToCart(plato, quantity, restauranteId)
                                if (!added) {
                                    dialogMessage = "Solo puedes añadir platos de un mismo restaurante al carrito."
                                    showDialog = true
                                }
                            },
                            onClick = {}
                        )
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Aceptar")
                    }
                },
                title = { Text("Error") },
                text = { Text(dialogMessage) }
            )
        }
    }
}
