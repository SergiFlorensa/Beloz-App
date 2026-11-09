package com.app.beloz.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

private data class MenuAction(val title: String, val icon: ImageVector, val route: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuPagPrincipal(navController: NavController) {
    var open by remember { mutableStateOf(false) }
    IconButton(onClick = { open = true }) {
        Icon(Icons.Default.Menu, contentDescription = "Abrir menú", tint = BelozColors.Ink)
    }
    if (open) {
        ModalBottomSheet(onDismissRequest = { open = false }, containerColor = BelozColors.Card) {
            Text("Tu Beloz", style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
            listOf(
                MenuAction("Restaurantes", Icons.Default.List, "lista_restaurantes"),
                MenuAction("Mis pedidos", Icons.Default.ShoppingCart, "pedidos"),
                MenuAction("Cuenta y preferencias", Icons.Default.Person, "cuenta"),
                MenuAction("Mi carrito", Icons.Default.ShoppingCart, "carrito"),
                MenuAction("Beloz AI", Icons.Default.Search, "beloz_chat")
            ).forEach { action ->
                ListItem(
                    headlineContent = { Text(action.title) },
                    leadingContent = { Icon(action.icon, null, tint = BelozColors.MutedGreen) },
                    trailingContent = { Icon(Icons.AutoMirrored.Filled.ArrowForward, null) },
                    colors = ListItemDefaults.colors(containerColor = BelozColors.Card),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp).clickable {
                        open = false
                        navController.navigate(action.route) { launchSingleTop = true }
                    }
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
