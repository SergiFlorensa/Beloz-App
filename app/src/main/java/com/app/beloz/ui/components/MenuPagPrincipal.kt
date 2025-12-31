package com.app.beloz.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.app.beloz.R
import com.app.beloz.theme.DanfordFontFamily

@Composable
fun MenuPagPrincipal(navController: NavController) {
    val menus = listOf("Restaurantes", "Pedidos", "Cuenta", "Carrito")
    val iconos = listOf(
        R.drawable.restaurante,
        R.drawable.bolsa,
        R.drawable.usuarios,
        R.drawable.carrito
    )

    var showDialog by remember { mutableStateOf(false) }

    Image(
        painter = painterResource(id = R.drawable.hamburguesa),
        contentDescription = "Menú",
        modifier = Modifier
            .size(36.dp)
            .clickable(onClick = { showDialog = true })
    )

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black,
                                    Color.DarkGray
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        // Título del menú
                        Text(
                            text = "Menú",
                            color = Color.White,
                            fontFamily = DanfordFontFamily,
                            fontSize = 20.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        menus.forEachIndexed { index, title ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        when (title.lowercase()) {
                                            "restaurantes" -> navController.navigate("lista_restaurantes")
                                            "pedidos" -> navController.navigate("pedidos")
                                            "cuenta" -> navController.navigate("cuenta")
                                            "carrito" -> navController.navigate("carrito")
                                        }
                                        showDialog = false
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = iconos[index]),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .padding(end = 16.dp)
                                )
                                Text(
                                    text = title,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontFamily = DanfordFontFamily
                                )
                            }

                            if (index < menus.size - 1) {
                                Divider(
                                    color = Color(0xFF71CD9D),
                                    thickness = 0.5.dp,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { showDialog = false },
                                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                            ) {
                                Text(
                                    text = "Cerrar",
                                    fontFamily = DanfordFontFamily,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
