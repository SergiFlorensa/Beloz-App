package com.app.beloz.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.beloz.ui.components.BelozColors
import com.app.beloz.ui.components.BelozTopAppBar

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun CookiesPage(navController: NavController) {
    var cookiesAccepted by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BelozColors.MintSurface,
        topBar = {
            BelozTopAppBar(
                title = "Cookies",
                subtitle = "Control de preferencias",
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
            InfoPanel(
                title = "Que son las cookies",
                text = "Las cookies guardan pequenas preferencias y estados de sesion para recordar idioma, acceso, opciones de navegacion y mejorar el rendimiento de la app."
            )

            InfoPanel(
                title = "Tipos que usamos",
                text = "Esenciales para el funcionamiento basico, rendimiento para analisis, funcionalidad para personalizar la experiencia y publicidad relacionada cuando sea aplicable."
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = BelozColors.Card,
                tonalElevation = 0.dp,
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Aceptar cookies opcionales",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = BelozColors.Ink
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Las esenciales permanecen activas para que Beloz funcione correctamente.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = BelozColors.MutedText
                        )
                    }
                    Switch(
                        checked = cookiesAccepted,
                        onCheckedChange = { cookiesAccepted = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BelozColors.Ink,
                            checkedTrackColor = BelozColors.Green,
                            uncheckedThumbColor = BelozColors.MutedText,
                            uncheckedTrackColor = BelozColors.SoftMint
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoPanel(title: String, text: String) {
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
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = BelozColors.MutedText,
                textAlign = TextAlign.Start
            )
        }
    }
}
