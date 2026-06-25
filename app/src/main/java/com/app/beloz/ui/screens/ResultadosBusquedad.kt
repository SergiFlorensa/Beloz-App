package com.app.beloz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.beloz.ui.components.BelozColors
import com.app.beloz.ui.components.BelozTopAppBar
import com.app.beloz.ui.components.RestauranteCard
import com.app.beloz.ui.viewModel.RestaurantesViewModel

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
        containerColor = BelozColors.MintSurface,
        topBar = {
            BelozTopAppBar(
                title = "Busqueda",
                subtitle = query.ifBlank { "Resultados" },
                navController = navController
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(BelozColors.MintSurface)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        color = BelozColors.Green,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                errorMessage != null -> {
                    Text(
                        text = "Error: $errorMessage",
                        color = BelozColors.Orange,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                restaurantes.isEmpty() -> {
                    Text(
                        text = "No se encontraron resultados para tu busqueda.",
                        color = BelozColors.MutedText,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
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
