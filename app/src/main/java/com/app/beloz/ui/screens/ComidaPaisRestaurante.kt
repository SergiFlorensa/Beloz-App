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
import androidx.navigation.NavHostController
import com.app.beloz.data.models.Restaurante
import com.app.beloz.data.repositories.RestauranteRepository
import com.app.beloz.ui.components.RestauranteCard
import com.app.beloz.theme.DanfordFontFamily
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComidaPaisRestaurante(navController: NavHostController, country: String) {
    var restaurantes by remember { mutableStateOf<List<Restaurante>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(country) {
        isLoading = true
        errorMessage = null
        try {
            restaurantes = RestauranteRepository.fetchRestaurantesByCountry(country)
        } catch (e: Exception) {
            Log.e("ComidaPaisRestaurante", "Error fetching restaurants", e)
            errorMessage = "Ha ocurrido un error al obtener los restaurantes."
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nos vamos a: $country", color = Color.White, fontFamily = DanfordFontFamily) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF285346),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
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
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: $errorMessage", color = Color.Red, fontFamily = DanfordFontFamily)
                    }
                }
                restaurantes.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "No hay restaurantes relacionados con este país.",
                            color = Color.White,
                            fontFamily = DanfordFontFamily
                        )
                    }
                }
                else -> {
                    LazyColumn {
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

                                onClick = {
                                    navController.navigate("platos_restaurante/${restaurant.restauranteId}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
