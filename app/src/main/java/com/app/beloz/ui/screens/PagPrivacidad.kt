package com.app.beloz.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.beloz.R
import com.app.beloz.ui.components.EstructuraCuentaItems

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun PagPrivacidad(navController: NavController) {
    Scaffold(
        topBar = {
            Surface(
                color = Color(0xFF285346),
                modifier = Modifier.fillMaxWidth()
            ) {
                TopAppBar(
                    title = { Text("Gestionar Privacidad", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                )
            }
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
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Opciones de Privacidad",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                EstructuraCuentaItems(
                    icon = R.drawable.cookie,
                    label = "Gestión de Cookies",
                    onTap = { navController.navigate("cookies") },
                    isEditable = false
                )

                EstructuraCuentaItems(
                    icon = R.drawable.privacidad,
                    label = "Revisión de Permisos y Datos",
                    onTap = { navController.navigate("permisos") }
                )
                EstructuraCuentaItems(
                    icon = R.drawable.politica,
                    label = "Política de Privacidad",
                    onTap = { navController.navigate("politicas") },
                    isEditable = false
                )

                EstructuraCuentaItems(
                    icon = R.drawable.eliminar,
                    label = "Eliminar Cuenta",
                    onTap = { navController.navigate("eliminarcuenta") }
                )

            }
        }
    }
}
