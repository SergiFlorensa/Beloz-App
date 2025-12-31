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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.beloz.ui.components.RestauranteCard
import com.app.beloz.ui.viewModel.RestaurantesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultadosBusquedad(query: String, navController: NavController) {
    val viewModel: RestaurantesViewModel = viewModel()
    val restaurantes by viewModel.restaurantes.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(query) {
        if (query.isNotEmpty()) {
            viewModel.buscarRestaurantes(query)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultados para: $query") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage != null -> {
                    Text(
                        text = "Error: $errorMessage",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 16.sp
                    )
                }
                restaurantes.isEmpty() -> {
                    Text(
                        text = "No se encontraron resultados para tu búsqueda.",
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 16.sp
                    )
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(restaurantes) { restaurant ->
                            RestauranteCard(
                                imagePath = restaurant.imagePath ?: "",
                                name = restaurant.name,
                                waitTime = restaurant.waitTime,
                                priceLevel = restaurant.priceLevel,
                                typeOfFood = restaurant.typeOfFood,
                                country = restaurant.country,
                                valoracion = restaurant.valoracion,
                                relevancia = restaurant.relevancia,
                            ) {
                                navController.navigate("platos_restaurante/${restaurant.restauranteId}")
                            }
                        }
                    }
                }
            }
        }
    }
}
