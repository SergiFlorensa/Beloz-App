package com.app.beloz.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                .padding(top = 16.dp, start = 12.dp, end = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    MenuPagPrincipal(navController = navController)
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            Box(modifier = Modifier.weight(1f)) {
                Buscador(
                    navController = navController, // Pasar navController
                    onQueryChanged = { query ->
                        viewModel.buscarRestaurantes(query)
                    },
                    searchResults = searchResults.map { it.name },
                    searchResultsIds = searchResultsIds,
                    searchResultKeywords = searchResults.map { it.typeOfFood },
                    onResultClick = { selectedResultId ->
                        navController.navigate("platos_restaurante/$selectedResultId")
                    }
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    IconoLocalizacion(iconSize = 26.dp) {
                        navController.navigate("map")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}
