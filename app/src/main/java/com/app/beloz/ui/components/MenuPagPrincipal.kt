package com.app.beloz.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.app.beloz.R

private data class MenuAction(
    val title: String,
    val subtitle: String,
    val icon: Int,
    val route: String,
    val accent: Color
)

@Composable
fun MenuPagPrincipal(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    val menuActions = listOf(
        MenuAction("Locales", "Ver restaurantes", R.drawable.restaurante, "lista_restaurantes", Color(0xFF56E39F)),
        MenuAction("Pedidos", "Estado y historial", R.drawable.bolsa, "pedidos", Color(0xFFF7D560)),
        MenuAction("Cuenta", "Datos y privacidad", R.drawable.usuarios, "cuenta", Color(0xFF9FD9FF)),
        MenuAction("Carrito", "Revisar pedido", R.drawable.carrito, "carrito", Color(0xFFFFB26B))
    )

    Image(
        painter = painterResource(id = R.drawable.hamburguesa),
        contentDescription = "Menu",
        modifier = Modifier
            .size(36.dp)
            .clickable(onClick = { showDialog = true })
    )

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color.Transparent,
                shadowElevation = 10.dp
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF10231E),
                                    Color(0xFF19382F),
                                    Color(0xFF0E1714)
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(112.dp)
                            .clip(CircleShape)
                            .background(Color(0x264DE59B))
                    )

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Beloz",
                                    color = Color.White,
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Accesos principales",
                                    color = Color(0xFFCFE8DC),
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            TextButton(onClick = { showDialog = false }) {
                                Text(
                                    text = "Cerrar",
                                    color = Color(0xFF56E39F),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            menuActions.chunked(2).forEach { rowItems ->
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    rowItems.forEach { action ->
                                        MenuActionTile(
                                            action = action,
                                            onClick = {
                                                navController.navigate(action.route)
                                                showDialog = false
                                            },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .clickable {
                                    navController.navigate("beloz_chat")
                                    showDialog = false
                                },
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xFF56E39F)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.chat_icon),
                                    contentDescription = "Beloz AI",
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Beloz AI",
                                        color = Color(0xFF10231E),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "Pide con ayuda inteligente",
                                        color = Color(0xFF24493D),
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuActionTile(
    action: MenuAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(104.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = Color(0x1FFFFFFF)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(action.accent),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = action.icon),
                    contentDescription = action.title,
                    modifier = Modifier.size(23.dp)
                )
            }
            Column {
                Text(
                    text = action.title,
                    color = Color.White,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = action.subtitle,
                    color = Color(0xFFD9E8DE),
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
