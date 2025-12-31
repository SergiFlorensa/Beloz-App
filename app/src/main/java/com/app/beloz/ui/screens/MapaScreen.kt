package com.app.beloz.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaScreen(navController: NavController) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    Configuration.getInstance().userAgentValue = "BelozApp/1.0 (Android)"

    mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
    mapView.setMultiTouchControls(true)

    val mapController: IMapController = mapView.controller
    mapController.setZoom(12.0)
    mapController.setCenter(org.osmdroid.util.GeoPoint(41.1496, 1.1069))

    val startMarker = Marker(mapView)
    startMarker.position = org.osmdroid.util.GeoPoint(41.1496, 1.1069)
    startMarker.title = "Reus"
    mapView.overlays.add(startMarker)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF285346), titleContentColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())
        }
    }
}
