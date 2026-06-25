package com.app.beloz.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.beloz.ui.components.BelozColors
import com.app.beloz.ui.components.BelozTopAppBar

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun PermisosPage(navController: NavController) {
    Scaffold(
        containerColor = BelozColors.MintSurface,
        topBar = {
            BelozTopAppBar(
                title = "Permisos",
                subtitle = "Datos vinculados a tu cuenta",
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BelozColors.MintSurface)
                .padding(paddingValues)
                .padding(horizontal = 18.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            PermissionPanel(
                title = "Permisos solicitados",
                body = "Acceso a tu cuenta y a los datos necesarios para registro, pedidos, pagos y soporte."
            )
            PermissionPanel(
                title = "Datos vinculados",
                body = "Informacion de cuenta, nombre, correo electronico, historial de pedidos y metodos de pago."
            )
            PermissionPanel(
                title = "Tus derechos",
                body = "Puedes revisar, modificar o solicitar la eliminacion de tus datos personales desde este mismo apartado."
            )
        }
    }
}

@Composable
private fun PermissionPanel(title: String, body: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = BelozColors.Card,
        tonalElevation = 0.dp,
        shadowElevation = 3.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = BelozColors.Ink
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = BelozColors.MutedText
            )
        }
    }
}
