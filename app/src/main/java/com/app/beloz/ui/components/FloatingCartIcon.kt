package com.app.beloz.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.beloz.R
import com.app.beloz.ui.viewModel.CartViewModel

@Composable
fun FloatingCartIcon(navController: NavController, cartViewModel: CartViewModel) {
    val itemCount by cartViewModel.itemCount.collectAsState()

    if (itemCount > 0) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(56.dp)
                    .shadow(4.dp, CircleShape)
                    .background(Color.White, CircleShape)
                    .clickable {
                        navController.navigate("carrito")
                    },
                contentAlignment = Alignment.Center
            ) {
                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = Color(0xFFFFA500),
                            contentColor = Color.White
                        ) {
                            Text(text = itemCount.toString())
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.carrito),
                        contentDescription = "Carrito",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}
