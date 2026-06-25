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
fun PoliticasPage(navController: NavController) {
    Scaffold(
        containerColor = BelozColors.MintSurface,
        topBar = {
            BelozTopAppBar(
                title = "Privacidad",
                subtitle = "Uso y proteccion de tus datos",
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
            PolicyPanel(
                title = "Nuestra politica",
                body = "Tu privacidad es importante. En esta seccion explicamos como recopilamos, usamos y protegemos tus datos personales dentro de Beloz."
            )
            PolicyPanel(
                title = "Datos recopilados",
                body = "Nombre, correo electronico, telefono, historial de pedidos y metodos de pago asociados a la cuenta."
            )
            PolicyPanel(
                title = "Uso de los datos",
                body = "Procesamos la informacion para iniciar sesion, preparar pedidos, gestionar pagos, mejorar la experiencia y ofrecer soporte cuando sea necesario."
            )
            PolicyPanel(
                title = "Derechos RGPD",
                body = "Puedes acceder, rectificar, eliminar o restringir el tratamiento de tus datos personales cuando lo necesites desde las opciones de privacidad."
            )
        }
    }
}

@Composable
private fun PolicyPanel(title: String, body: String) {
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
