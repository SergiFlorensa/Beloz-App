package com.app.beloz.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.app.beloz.ui.viewModel.CartViewModel

private data class DockDestination(val title: String, val route: String, val icon: ImageVector)

@Composable
fun BelozBottomBar(navController: NavController, cartViewModel: CartViewModel, modifier: Modifier = Modifier) {
    val count by cartViewModel.itemCount.collectAsState()
    val entry by navController.currentBackStackEntryAsState()
    val current = entry?.destination?.route?.substringBefore("?")
    val destinations = remember { listOf(
        DockDestination("Inicio", "home", Icons.Default.Home),
        DockDestination("Locales", "lista_restaurantes", Icons.Default.List),
        DockDestination("Beloz AI", "beloz_chat", Icons.AutoMirrored.Filled.Send),
        DockDestination("Carrito", "carrito", Icons.Default.ShoppingCart),
        DockDestination("Cuenta", "cuenta", Icons.Default.Person)
    ) }
    NavigationBar(modifier, containerColor = BelozColors.Card, tonalElevation = 0.dp) {
        destinations.forEach { item ->
            NavigationBarItem(
                selected = current == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo("home") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    BadgedBox(badge = {
                        if (item.route == "carrito" && count > 0) {
                            Badge(containerColor = BelozColors.Gold, contentColor = BelozColors.Ink) {
                                Text(if (count > 99) "99+" else count.toString())
                            }
                        }
                    }) { Icon(item.icon, null, Modifier.size(24.dp)) }
                },
                label = { Text(item.title, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BelozColors.MutedGreen, selectedTextColor = BelozColors.Ink,
                    indicatorColor = BelozColors.SoftMint,
                    unselectedIconColor = BelozColors.MutedText, unselectedTextColor = BelozColors.MutedText
                )
            )
        }
    }
}
