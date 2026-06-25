package com.app.beloz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.beloz.ui.components.BelozBottomBar
import com.app.beloz.ui.components.EstablecimientoSection
import com.app.beloz.ui.components.HomeCommandCenter
import com.app.beloz.ui.components.TablaPaises
import com.app.beloz.ui.components.MenuFiltros
import com.app.beloz.ui.components.RestaurantesPopulares
import com.app.beloz.ui.components.TopBar
import com.app.beloz.ui.components.SugerenciasContextualesSection
import com.app.beloz.ui.viewModel.CartViewModel
import com.app.beloz.ui.viewModel.RestaurantesViewModel

@Composable
fun PagPrincipal(navController: NavController,cartViewModel: CartViewModel) {
    val viewModel: RestaurantesViewModel = viewModel()
    Scaffold(
        topBar = {
            TopBar(navController = navController, viewModel = viewModel)
        },
        bottomBar = {
            BelozBottomBar(navController = navController, cartViewModel = cartViewModel)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4FAF6))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HomeCommandCenter(navController = navController)
                Spacer(modifier = Modifier.height(10.dp))
                MenuFiltros(navController = navController)
                Spacer(modifier = Modifier.height(10.dp))
                SugerenciasContextualesSection(navController = navController)
                Spacer(modifier = Modifier.height(16.dp))
                EstablecimientoSection(navController = navController)
                Spacer(modifier = Modifier.height(16.dp))
                TablaPaises(navController = navController)
                Spacer(modifier = Modifier.height(16.dp))
                RestaurantesPopulares(navController = navController)
                Spacer(modifier = Modifier.height(132.dp))


            }
        }
    }
}


