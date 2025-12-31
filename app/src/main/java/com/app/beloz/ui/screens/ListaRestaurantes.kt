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
import com.app.beloz.ui.components.RestauranteCard
import com.app.beloz.ui.viewModel.RestaurantesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaRestaurantes(
    navController: NavController,
    searchQuery: String,
) {
    val viewModel: RestaurantesViewModel = viewModel()
    val context = LocalContext.current
    val perfilRepo = remember { PerfilSaborRepository(context) }
    val scope = rememberCoroutineScope()

    val restaurantes by viewModel.restaurantes.collectAsState(initial = emptyList())

    val filteredRestaurantes = if (searchQuery.isNotEmpty()) {
        restaurantes.filter { restaurante ->
            (restaurante.typeOfFood?.contains(searchQuery, ignoreCase = true) == true) ||
                    (restaurante.name?.contains(searchQuery, ignoreCase = true) == true)
        }
    } else {
        restaurantes
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = if (searchQuery.isEmpty()) {
                        "Restaurantes"
                    } else {
                        "Resultados para \"$searchQuery\""
                    }
                    Text(title, color = Color.White)
                },

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
                modifier = Modifier.padding(top = 0.dp)
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
            if (filteredRestaurantes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay restaurantes disponibles",
                        modifier = Modifier.padding(16.dp),
                        color = Color.White
                    )
                }
            } else {
                LazyColumn {
                    items(filteredRestaurantes) { restaurante ->
                        RestauranteCard(
                            imagePath = restaurante.imagePath,
                            name = restaurante.name,
                            waitTime = restaurante.waitTime,
                            priceLevel = restaurante.priceLevel,
                            typeOfFood = restaurante.typeOfFood,
                            country = restaurante.country,
                            valoracion = restaurante.valoracion,
                            relevancia = restaurante.relevancia,
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
