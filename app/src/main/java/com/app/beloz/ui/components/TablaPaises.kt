package com.app.beloz.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.beloz.R
import com.app.beloz.theme.DanfordFontFamily

@Composable
fun TablaPaises(navController: NavController) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "¿Dónde quiere viajar \ntu paladar?",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    fontFamily = DanfordFontFamily,
                ),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))

            Image(
                painter = painterResource(id = R.drawable.airplane),
                contentDescription = "Icono de país",
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.height(280.dp),
            contentPadding = PaddingValues(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(12) { index ->
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clickable {
                            navController.navigate("comida_pais_restaurante/${nombrePais(index)}")
                        }
                        .background(Color(0xFFFFDB57), shape = RoundedCornerShape(15.dp))
                ) {
                    Text(
                        text = nombrePais(index),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(4.dp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFA03D3D), // Color del texto
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFamily = DanfordFontFamily
                        )
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

fun nombrePais(index: Int): String {
    return when (index) {
        0 -> "Japón"
        1 -> "Argentina"
        2 -> "España"
        3 -> "Perú"
        4 -> "Arabia"
        5 -> "India"
        6 -> "Italia"
        7 -> "Marruecos"
        8 -> "Vietnam"
        9 -> "Tailandia"
        10 -> "México"
        11 -> "China"
        else -> "Otra"
    }
}
