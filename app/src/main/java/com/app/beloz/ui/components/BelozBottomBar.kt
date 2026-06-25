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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.app.beloz.R
import com.app.beloz.ui.viewModel.CartViewModel

private data class BottomDockItem(
    val label: String,
    val icon: Int,
    val route: String
)

@Composable
fun BelozBottomBar(
    navController: NavController,
    cartViewModel: CartViewModel,
    modifier: Modifier = Modifier
) {
    val itemCount by cartViewModel.itemCount.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route.orEmpty()

    val leftItems = listOf(
        BottomDockItem("Inicio", R.drawable.ic_beloz_home, "home"),
        BottomDockItem("Locales", R.drawable.restaurante, "lista_restaurantes")
    )
    val rightItems = listOf(
        BottomDockItem("Carrito", R.drawable.carrito, "carrito"),
        BottomDockItem("Cuenta", R.drawable.user, "cuenta")
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .height(98.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp),
            shape = RoundedCornerShape(30.dp),
            color = Color.Transparent,
            shadowElevation = 12.dp
        ) {
            Row(
                modifier = Modifier
                    .background(
                        Brush.linearGradient(
                            listOf(BelozColors.Ink, Color(0xFF0D1815), BelozColors.Deep)
                        )
                    )
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                leftItems.forEach { item ->
                    DockItem(
                        item = item,
                        selected = currentRoute == item.route,
                        onClick = { navigateSingleTop(navController, item.route) }
                    )
                }

                Spacer(modifier = Modifier.width(76.dp))

                rightItems.forEach { item ->
                    DockItem(
                        item = item,
                        selected = currentRoute == item.route,
                        badgeCount = if (item.route == "carrito") itemCount else 0,
                        onClick = { navigateSingleTop(navController, item.route) }
                    )
                }
            }
        }

        AiDockButton(
            selected = currentRoute == "beloz_chat",
            onClick = { navigateSingleTop(navController, "beloz_chat") },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-2).dp)
        )
    }
}

@Composable
private fun DockItem(
    item: BottomDockItem,
    selected: Boolean,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(56.dp)
            .width(70.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = if (selected) Color(0x254DE59B) else Color.Transparent
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BadgedBox(
                badge = {
                    if (badgeCount > 0) {
                        Badge(containerColor = BelozColors.Gold, contentColor = BelozColors.Ink) {
                            Text(badgeCount.toString())
                        }
                    }
                }
            ) {
                Image(
                    painter = painterResource(id = item.icon),
                    contentDescription = item.label,
                    modifier = Modifier.size(if (selected) 31.dp else 28.dp)
                )
            }
            Text(
                text = item.label,
                color = if (selected) Color.White else Color(0xFFD9E8DE),
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Black else FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun AiDockButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .clickable(onClick = onClick),
            shape = CircleShape,
            color = if (selected) BelozColors.Gold else BelozColors.Green,
            shadowElevation = 10.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.chat_icon),
                    contentDescription = "Beloz AI",
                    modifier = Modifier.size(34.dp)
                )
            }
        }
        Text(
            text = "AI",
            color = Color.White,
            fontFamily = FontFamily.SansSerif,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier
                .background(BelozColors.Ink, RoundedCornerShape(100.dp))
                .padding(horizontal = 8.dp, vertical = 1.dp)
        )
    }
}

private fun navigateSingleTop(navController: NavController, route: String) {
    navController.navigate(route) {
        launchSingleTop = true
        if (route != "home") {
            restoreState = true
        }
    }
}
