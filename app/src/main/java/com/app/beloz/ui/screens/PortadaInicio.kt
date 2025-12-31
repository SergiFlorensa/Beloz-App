package com.app.beloz.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import com.app.beloz.R

@Composable
fun PortadaInicio(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(1000)
        navController.navigate("home") {
            popUpTo("splash") { inclusive = true }
        }
        println("Navigated to home")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.cycle),
            contentDescription = "Splash Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RichTextTitle()
        }
    }
}
@Composable
fun RichTextTitle() {
    val vintageTipoLetra = FontFamily(Font(R.font.vintage))
    val textBeColor = Color(0xFF1AB89E)
    val textLozColor = Color(0xFFEBCA5F)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Be",
            style = TextStyle(
                fontSize = 90.sp,
                fontWeight = FontWeight.Bold,
                color = textBeColor ,
                fontFamily = vintageTipoLetra,
                shadow = Shadow(
                    color = Color.Black,
                    offset = Offset(2f, 2f),
                    blurRadius = 20f
                )
            ),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Loz",
            style = TextStyle(
                fontSize = 90.sp,
                fontWeight = FontWeight.Bold,
                color = textLozColor,
                fontFamily = vintageTipoLetra,
                shadow = Shadow(
                    color = Color.Black,
                    offset = Offset(2f, 2f),
                    blurRadius = 20f
                )
            ),
            textAlign = TextAlign.Center
        )
    }
}