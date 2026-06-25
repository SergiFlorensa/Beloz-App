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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.beloz.ui.components.BelozColors
import com.app.beloz.ui.components.BelozTopAppBar
import com.app.beloz.ui.components.RestauranteCard
import com.app.beloz.ui.viewModel.RestaurantesViewModel

@Composable
fun ValoracionRestaurantes(navController: NavController) {
    val viewModel: RestaurantesViewModel = viewModel()
    val restaurantes by viewModel.restaurantes.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getRestaurantesPorValoracion()
    }

    Scaffold(
        topBar = {
            BelozTopAppBar(
                title = "Valoracion",
                subtitle = "Mejor puntuados",
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(BelozColors.MintSurface)
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BelozColors.Green)
                    }
                }
                errorMessage != null -> {
                    Text("Error: $errorMessage", modifier = Modifier.padding(16.dp), color = Color.Red)
                }
                restaurantes.isEmpty() -> {
                    Text("No hay restaurantes disponibles", modifier = Modifier.padding(16.dp), color = BelozColors.MutedText)
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
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
