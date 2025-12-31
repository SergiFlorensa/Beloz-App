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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.beloz.ui.components.RestauranteCard
import com.app.beloz.ui.viewModel.RestaurantesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipoComidaSeleccionadaRestaurantes(selectedTypes: List<String>, navController: NavController) {
    val viewModel: RestaurantesViewModel = viewModel()
    val restaurantes by viewModel.restaurantes.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(selectedTypes) {
        viewModel.getRestaurantesFiltradosPorTipos(selectedTypes)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Restaurantes", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF285346),
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                ),
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
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage != null -> {
                    Text("Error: $errorMessage", modifier = Modifier.padding(16.dp), color = Color.Red)
                }
                restaurantes.isEmpty() -> {
                    Text("No hay restaurantes disponibles", modifier = Modifier.padding(16.dp), color = Color.Gray)
                }
                else -> {
                    LazyColumn {
                        items(restaurantes) { restaurante ->
                            RestauranteCard(
                                imagePath = restaurante.imagePath ?: "",
                                name = restaurante.name,
                                waitTime = restaurante.waitTime,
                                priceLevel = restaurante.priceLevel,
                                typeOfFood = restaurante.typeOfFood,
                                country = restaurante.country,
                                valoracion = restaurante.valoracion,
                                relevancia = restaurante.relevancia,
                                onClick = {
                                    navController.navigate("platos_restaurante/${restaurante.restauranteId}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
