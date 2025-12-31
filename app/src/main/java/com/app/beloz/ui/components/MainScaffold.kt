package com.app.beloz.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.app.beloz.ui.viewModel.CartViewModel

@Composable
fun MainScaffold(
    navController: NavController,
    cartViewModel: CartViewModel,
    topBar: @Composable () -> Unit = {},
    content: @Composable (paddingValues: androidx.compose.foundation.layout.PaddingValues) -> Unit
) {
    Scaffold(
        topBar = topBar,
        content = { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                content(paddingValues)
                FloatingCartIcon(navController = navController, cartViewModel = cartViewModel)
            }
        }
    )
}
