package com.app.beloz.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.beloz.R
import com.app.beloz.ui.components.BelozColors
import com.app.beloz.ui.components.BelozTopAppBar
import com.app.beloz.ui.components.EstructuraCuentaItems

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun PagPrivacidad(navController: NavController) {
    Scaffold(
        containerColor = BelozColors.MintSurface,
        topBar = {
            BelozTopAppBar(
                title = "Privacidad",
                subtitle = "Preferencias y derechos",
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BelozColors.MintSurface)
                .padding(paddingValues)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Gestiona como se usa tu informacion dentro de Beloz.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = BelozColors.Ink
            )
            Text(
                text = "Aqui puedes revisar permisos, cookies, politica de privacidad y solicitar la eliminacion de tu cuenta.",
                style = MaterialTheme.typography.bodyMedium,
                color = BelozColors.MutedText
            )
            Spacer(modifier = Modifier.height(2.dp))

            EstructuraCuentaItems(
                icon = R.drawable.cookie,
                label = "Gestion de cookies",
                onTap = { navController.navigate("cookies") },
                isEditable = false
            )
            EstructuraCuentaItems(
                icon = R.drawable.privacidad,
                label = "Permisos y datos",
                onTap = { navController.navigate("permisos") }
            )
            EstructuraCuentaItems(
                icon = R.drawable.politica,
                label = "Politica de privacidad",
                onTap = { navController.navigate("politicas") },
                isEditable = false
            )
            EstructuraCuentaItems(
                icon = R.drawable.eliminar,
                label = "Eliminar cuenta",
                onTap = { navController.navigate("eliminarcuenta") }
            )
        }
    }
}
