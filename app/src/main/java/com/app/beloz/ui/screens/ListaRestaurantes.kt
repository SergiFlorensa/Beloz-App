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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.beloz.innovacion.perfil.EventoUso
import com.app.beloz.innovacion.perfil.PerfilSaborRepository
import com.app.beloz.innovacion.perfil.TipoEvento
import com.app.beloz.ui.components.BelozColors
import com.app.beloz.ui.components.BelozTopAppBar
import com.app.beloz.ui.components.RestauranteCard
import com.app.beloz.ui.viewModel.RestaurantesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaRestaurantes(
    navController: NavController,
    searchQuery: String,
    sortOrder: String = "",
) {
    val viewModel: RestaurantesViewModel = viewModel()
    val context = LocalContext.current
    val perfilRepo = remember { PerfilSaborRepository(context) }
    val scope = rememberCoroutineScope()

    val restaurantes by viewModel.restaurantes.collectAsState(initial = emptyList())
    val loading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()

    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            viewModel.buscarRestaurantes(searchQuery)
        } else {
            viewModel.cargarRestaurantes()
        }
    }

    val filteredRestaurantes = remember(restaurantes, sortOrder) {
        com.app.beloz.data.models.CatalogSearch.sort(restaurantes, sortOrder)
    }

    Scaffold(
        topBar = {
            BelozTopAppBar(
                title = if (searchQuery.isEmpty()) "Restaurantes" else "Resultados",
                subtitle = if (searchQuery.isEmpty()) "Locales disponibles" else "\"$searchQuery\"",
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
            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (error != null) {
                Text(error.orEmpty(), color = BelozColors.MutedText)
                TextButton(onClick = {
                    if (searchQuery.isBlank()) viewModel.cargarRestaurantes() else viewModel.buscarRestaurantes(searchQuery)
                }) { Text("Reintentar") }
            } else if (filteredRestaurantes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay restaurantes disponibles",
                        modifier = Modifier.padding(16.dp),
                        color = BelozColors.MutedText
                    )
                }
            } else {
                LazyColumn {
                    items(filteredRestaurantes, key = { it.restauranteId }) { restaurante ->
                        RestauranteCard(
                            imagePath = restaurante.imagePath,
                            name = restaurante.name,
                            waitTime = restaurante.waitTime,
                            priceLevel = restaurante.priceLevel,
                            typeOfFood = restaurante.typeOfFood,
                            country = restaurante.country,
                            valoracion = restaurante.valoracion,
                            relevancia = restaurante.relevancia,
                            recomendacionMotivo = restaurante.recomendacionMotivo,
                            onClick = {
                                scope.launch {
                                    perfilRepo.registrarEvento(
                                        EventoUso(
                                            tipo = TipoEvento.VIEW_RESTAURANT,
                                            metadata = mapOf(
                                                "restaurant_id" to restaurante.restauranteId.toString(),
                                                "food_type" to restaurante.typeOfFood,
                                                "price_level" to restaurante.priceLevel,
                                                "country" to restaurante.country
                                            )
                                        )
                                    )
                                }
                                navController.navigate("platos_restaurante/${restaurante.restauranteId}")
                            }
                        )
                    }
                }
            }
        }
    }
}
