package com.app.beloz.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.app.beloz.ui.components.BelozColors
import com.app.beloz.ui.components.BelozTopAppBar
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapaScreen(navController: NavController) {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            Configuration.getInstance().userAgentValue = "BelozApp/1.0 (Android)"
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(12.0)
            controller.setCenter(GeoPoint(41.1496, 1.1069))

            val startMarker = Marker(this)
            startMarker.position = GeoPoint(41.1496, 1.1069)
            startMarker.title = "Reus"
            overlays.add(startMarker)
        }
    }
    val mapController: IMapController = mapView.controller
    mapController.setCenter(GeoPoint(41.1496, 1.1069))

    Scaffold(
        containerColor = BelozColors.MintSurface,
        topBar = {
            BelozTopAppBar(
                title = "Mapa",
                subtitle = "Restaurantes cerca de ti",
                navController = navController
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(18.dp),
                shape = RoundedCornerShape(24.dp),
                color = BelozColors.Card,
                shadowElevation = 5.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
                    Text(
                        text = "Zona inicial",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = BelozColors.Ink
                    )
                    Text(
                        text = "Reus y alrededores",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BelozColors.MutedText
                    )
                }
            }
        }
    }
}
