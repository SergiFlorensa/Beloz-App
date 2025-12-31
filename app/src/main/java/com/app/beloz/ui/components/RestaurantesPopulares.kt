package com.app.beloz.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.beloz.data.models.Restaurante
import com.app.beloz.data.repositories.RestauranteRepository
import com.app.beloz.data.remote.ImageUrlResolver
import com.app.beloz.innovacion.perfil.EventoUso
import com.app.beloz.innovacion.perfil.PerfilSaborRepository
import com.app.beloz.innovacion.perfil.TipoEvento
import com.app.beloz.theme.DanfordFontFamily

@Composable
fun RestaurantesPopulares(navController: NavController) {
    var restaurantes by remember { mutableStateOf<List<Restaurante>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val perfilRepo = remember { PerfilSaborRepository(context) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            restaurantes = RestauranteRepository.fetchRestaurantesPopulares()
        } catch (e: Exception) {
            errorMessage = e.message
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Restaurantes Populares",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontFamily = DanfordFontFamily,
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Cargando restaurantes...", color = Color.Gray)
                }
            }
            errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: $errorMessage", color = Color.Red)
                }
            }
            restaurantes.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay restaurantes populares disponibles.")
                }
            }
            else -> {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    items(restaurantes.filter { it.esPopular }) { restaurant ->
                        RestaurantePopularItem(
                            imageUrl = ImageUrlResolver.resolve(restaurant.logoRestaurante) ?: "",
                            restaurantId = restaurant.restauranteId,
                            onClick = {
                                scope.launch {
                                    perfilRepo.registrarEvento(
                                        EventoUso(
                                            tipo = TipoEvento.VIEW_RESTAURANT,
                                            metadata = mapOf(
                                                "restaurant_id" to restaurant.restauranteId.toString(),
                                                "food_type" to restaurant.typeOfFood,
                                                "price_level" to restaurant.priceLevel,
                                                "country" to restaurant.country
                                            )
                                        )
                                    )
                                }
                                navController.navigate("platos_restaurante/${restaurant.restauranteId}")
                            }
                        )
                    }
                }
            }
        }
    }
}
