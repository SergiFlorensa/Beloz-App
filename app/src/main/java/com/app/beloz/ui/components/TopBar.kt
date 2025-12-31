package com.app.beloz.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.beloz.ui.viewModel.RestaurantesViewModel

@Composable
fun TopBar(navController: NavController, viewModel: RestaurantesViewModel) {
    val searchResults by viewModel.restaurantes.collectAsState()
    val searchResultsIds = searchResults.map { it.restauranteId.toString() }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(top = 20.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MenuPagPrincipal(navController = navController)


            Box(modifier = Modifier.weight(1f)) {
                Buscador(
                    navController = navController, // Pasar navController
                    onQueryChanged = { query ->
                        viewModel.buscarRestaurantes(query)
                    },
                    searchResults = searchResults.map { it.name },
                    searchResultsIds = searchResultsIds,
                    onResultClick = { selectedResultId ->
                        navController.navigate("platos_restaurante/$selectedResultId")
                    }
                )
            }


            IconoLocalizacion(iconSize = 30.dp) {
                navController.navigate("map")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
