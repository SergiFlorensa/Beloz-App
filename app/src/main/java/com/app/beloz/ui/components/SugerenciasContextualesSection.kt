package com.app.beloz.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.beloz.innovacion.contexto.dominio.SugerenciaContextual
import com.app.beloz.innovacion.contexto.presentacion.RecomendacionesContextoViewModel
import com.app.beloz.theme.DanfordFontFamily

@Composable
fun SugerenciasContextualesSection(
    modifier: Modifier = Modifier,
    viewModel: RecomendacionesContextoViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101C1A)),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Radar emocional",
                color = Color(0xFF71CD9D),
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = DanfordFontFamily)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = estado.descripcionContextual,
                color = Color.White,
                fontSize = 12.sp
            )
            if (estado.hayClima && estado.descripcionClima.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = estado.descripcionClima,
                    color = Color(0xFF9FE2BF),
                    fontSize = 12.sp
                )
            }
            if (estado.error != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = estado.error,
                    color = Color(0xFFFFC107),
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (estado.cargando) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color(0xFF71CD9D))
                }
            } else if (estado.sugerencias.isEmpty()) {
                Text(
                    text = "Sin recomendaciones todavía",
                    color = Color.LightGray,
                    fontSize = 13.sp
                )
            } else {
                estado.sugerencias.forEach { sugerencia ->
                    SugerenciaContextualItem(sugerencia)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            Button(
                onClick = { viewModel.refrescar() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Actualizar estado", color = Color.Black)
            }
        }
    }
}

@Composable
private fun SugerenciaContextualItem(sugerencia: SugerenciaContextual) {
    Column(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Text(
            text = sugerencia.titulo,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium.copy(fontFamily = DanfordFontFamily)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = sugerencia.descripcion, color = Color(0xFFE0E0E0), fontSize = 13.sp)
        if (sugerencia.etiquetas.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                sugerencia.etiquetas.forEach { etiqueta ->
                    Text(
                        text = "#${etiqueta}",
                        color = Color(0xFF71CD9D),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
