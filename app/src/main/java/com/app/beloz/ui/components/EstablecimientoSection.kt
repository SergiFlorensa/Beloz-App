package com.app.beloz.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.app.beloz.theme.DanfordFontFamily
import com.app.beloz.data.models.Restaurante
import com.app.beloz.data.repositories.RestauranteRepository
import kotlinx.coroutines.launch

@Composable
fun EstablecimientoSection(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val restaurants = remember { mutableStateOf<List<Restaurante>>(emptyList()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                restaurants.value = RestauranteRepository.fetchRestaurantesInteres()
                println("Restaurantes recibidos: ${restaurants.value}")
            } catch (e: Exception) {
                println("Error al obtener los restaurantes de interés: ${e.message}")
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Establecimientos de interés",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            )
            IconButton(onClick = {
                navController.navigate("lista_restaurantes")
            }) {
                Icon(
                    painter = painterResource(id = com.app.beloz.R.drawable.ampliar),
                    contentDescription = "Siguiente",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.height(250.dp)
        ) {
            items(restaurants.value) { restaurant ->
                HorizontalItem(
                    restaurant = restaurant,
                    onClick = {
                        navController.navigate("platos_restaurante/${restaurant.restauranteId}")
                    }
                )
            }
        }
    }
}

@Composable
fun HorizontalItem(restaurant: Restaurante, onClick: () -> Unit) {
    val baseUrl = "https://beloz-production.up.railway.app/images/"
    val fullImagePath = baseUrl + (restaurant.imagePath ?: "images/default.png")

    Card(
        modifier = Modifier
            .width(220.dp)
            .padding(end = 12.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = rememberImagePainter(data = fullImagePath),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .padding(10.dp)
            ) {
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        fontFamily = DanfordFontFamily
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
