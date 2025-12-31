package com.app.beloz.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.app.beloz.theme.DanfordFontFamily // Asegúrate de importar el DanfordFontFamily

@Composable
fun DetallePedidoItem(
    nombre: String,
    cantidad: Int,
    precio: Double
) {
    Divider(color = Color(0xFFE0E0E0))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = nombre,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = DanfordFontFamily
                ),
                color = Color.Black
            )
            Text(
                text = "Cantidad: $cantidad",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = DanfordFontFamily
                ),
                color = Color.DarkGray
            )
        }
        Text(
            text = "€${String.format("%.2f", precio)}",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontFamily = DanfordFontFamily
            ),
            color = Color.Black
        )
    }
}
