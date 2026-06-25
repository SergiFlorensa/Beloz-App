package com.app.beloz.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.beloz.data.models.Pedido
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
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onPedidoClick(pedido) },
        color = BelozColors.Card,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 3.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = BelozColors.SoftMint
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = BelozColors.MutedGreen,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(
                            text = "Pedido reciente",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = BelozColors.Ink
                        )
                        Text(
                            text = formatFecha(pedido.fecha),
                            style = MaterialTheme.typography.bodyMedium,
                            color = BelozColors.MutedText
                        )
                    }
                }
                Text(
                    text = "${String.format("%.2f", pedido.total)} EUR",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = BelozColors.Ink
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onPedidoClick(pedido) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = BelozColors.Green,
                    contentColor = BelozColors.Ink
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Ver detalles", fontWeight = FontWeight.Black)
            }
        }
    }
}
