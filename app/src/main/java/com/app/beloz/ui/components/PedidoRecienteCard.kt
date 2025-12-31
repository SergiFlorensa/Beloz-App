package com.app.beloz.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.beloz.data.models.Pedido
import com.app.beloz.theme.DanfordFontFamily
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

fun formatFecha(fecha: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return try {
        val date = inputFormat.parse(fecha)
        outputFormat.format(date)
    } catch (e: ParseException) {
        fecha
    }
}


@Composable
fun PedidoRecienteCard(
    pedido: Pedido,
    onPedidoClick: (Pedido) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onPedidoClick(pedido) },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF285346)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Fecha: ${formatFecha(pedido.fecha)}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Total: €${String.format("%.2f", pedido.total)}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontFamily = DanfordFontFamily
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onPedidoClick(pedido) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small
            ) {
                Text("Ver detalles", color = Color.Black)
            }
        }
    }
}
