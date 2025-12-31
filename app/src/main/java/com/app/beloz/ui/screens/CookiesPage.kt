package com.app.beloz.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun CookiesPage(navController: NavController) {
    var cookiesAccepted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Cookies", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF285346))
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF285346))
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "¿Qué son las Cookies?",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Justify
                )
                Text(
                    text = "Las cookies son pequeños archivos de texto que se almacenan en el dispositivo del usuario cuando navega por la aplicación. Estas cookies nos permiten recordar ciertas preferencias o acciones, como el idioma preferido, la autenticación del usuario, o las opciones de navegación. También se utilizan para analizar cómo los usuarios interactúan con la aplicación, lo que nos ayuda a mejorar la experiencia y el rendimiento.",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Justify

                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tipos de Cookies:",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "• Cookies esenciales para el funcionamiento básico.\n• Cookies de rendimiento para análisis.\n• Cookies de funcionalidad para personalizar la experiencia.\n• Cookies publicitarias para mostrar anuncios relacionados.",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Gestión de Cookies:",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Puedes aceptar o rechazar el uso de cookies en nuestra aplicación. Solo las cookies esenciales para el funcionamiento básico de la aplicación se activarán por defecto.",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Aceptar Cookies",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Switch(
                        checked = cookiesAccepted,
                        onCheckedChange = { cookiesAccepted = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFFFA500),
                            uncheckedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFFFA500),
                            uncheckedTrackColor = Color.Gray
                        )
                    )
                }
            }
        }
    }
}



