package com.app.beloz.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.beloz.R

private data class CountryFlavor(val name: String, val tone: Color)

@Composable
fun TablaPaises(navController: NavController) {
    val countries = listOf(
        CountryFlavor("Japon", Color(0xFFFFC4C4)),
        CountryFlavor("Argentina", Color(0xFFB9E5FF)),
        CountryFlavor("Espana", Color(0xFFFFD46B)),
        CountryFlavor("Peru", Color(0xFFFFB6A1)),
        CountryFlavor("Arabia", Color(0xFFE9D4FF)),
        CountryFlavor("India", Color(0xFFFFC58A)),
        CountryFlavor("Italia", Color(0xFFBFE8C7)),
        CountryFlavor("Marruecos", Color(0xFFFFD1B8)),
        CountryFlavor("Vietnam", Color(0xFFC9F0D8)),
        CountryFlavor("Tailandia", Color(0xFFFFE18A)),
        CountryFlavor("Mexico", Color(0xFFBCE7D5)),
        CountryFlavor("China", Color(0xFFFFB3B3))
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp),
        shape = RoundedCornerShape(26.dp),
        color = BelozColors.Ink,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Viaja con el paladar",
                        color = Color.White,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Explora restaurantes por origen",
                        color = Color(0xFFD9E8DE),
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = BelozColors.Green,
                    modifier = Modifier.size(54.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.airplane),
                            contentDescription = "Viajes gastronomicos",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.height(278.dp),
                contentPadding = PaddingValues(bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(countries) { country ->
                    CountryFlavorTile(
                        country = country,
                        onClick = {
                            navController.navigate("comida_pais_restaurante/${country.name}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CountryFlavorTile(
    country: CountryFlavor,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(88.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .background(
                Brush.linearGradient(
                    listOf(country.tone, Color.White)
                )
            )
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(34.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.45f))
        )
        Text(
            text = country.name,
            color = BelozColors.Ink,
            fontFamily = FontFamily.SansSerif,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
}

fun nombrePais(index: Int): String {
    return when (index) {
        0 -> "Japon"
        1 -> "Argentina"
        2 -> "Espana"
        3 -> "Peru"
        4 -> "Arabia"
        5 -> "India"
        6 -> "Italia"
        7 -> "Marruecos"
        8 -> "Vietnam"
        9 -> "Tailandia"
        10 -> "Mexico"
        11 -> "China"
        else -> "Otra"
    }
}
