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
fun PoliticasPage(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Política de Privacidad", color = Color.White) },
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
                    text = "Nuestra Política de Privacidad",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Tu privacidad es importante para nosotros. En esta sección, te explicamos cómo recopilamos, usamos y protegemos tus datos personales.",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "¿Qué datos recopilamos?",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "• Información personal: Nombre, correo electrónico, teléfono.\n• Historial de pedidos.\n• Métodos de pago.",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "¿Cómo usamos tus datos?",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "• Para mejorar la experiencia de usuario.\n• Para procesar pagos y envíos.",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Justify
                )
                Text(
                    text = "Protección de Datos Personales (RGPD):\n\nEn conformidad con el Reglamento General de Protección de Datos (RGPD), tus datos personales serán tratados de manera segura y confidencial. Tienes derecho a acceder, rectificar, eliminar o restringir el tratamiento de tus datos en cualquier momento. Para más información, consulta nuestra Política de Privacidad.",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Justify
                )

            }
        }
    }
}
