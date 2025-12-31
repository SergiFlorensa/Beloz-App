package com.app.beloz.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.beloz.data.models.Pedido
import com.app.beloz.theme.DanfordFontFamily

@Composable
fun PedidoItem(
    pedido: Pedido,
    onPedidoClick: (Pedido) -> Unit
) {
    Divider(color = Color(0xFFE0E0E0))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPedidoClick(pedido) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Fecha: ${formatFecha(pedido.fecha)}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color.Black
            )
            Text(
                text = "Total: €${String.format("%.2f", pedido.total)}",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = DanfordFontFamily,
                color = Color.DarkGray
            )
        }
        Icon(
            imageVector = Icons.Default.List,
            contentDescription = "Ver detalles",
            tint = Color.Gray
        )
    }
}
