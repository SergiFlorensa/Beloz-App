package com.app.beloz.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PlatoCard(
    imagePath: String?,
    name: String,
    description: String,
    price: Double,
    onAddToCart: (quantity: Int) -> Unit,
    quantityInCart: Int = 0,
    onClick: () -> Unit
) {
    val validPrice = price.isFinite() && price > 0.0
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, BelozColors.MutedText.copy(alpha = 0.15f)),
        colors = CardDefaults.cardColors(containerColor = BelozColors.Card)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                Modifier.fillMaxWidth().clickable(role = Role.Button,
                    onClickLabel = "Ver detalle de $name", onClick = onClick),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(name, style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold, color = BelozColors.Ink)
                    if (description.isNotBlank()) {
                        Text(description, style = MaterialTheme.typography.bodySmall,
                            color = BelozColors.MutedText, maxLines = 3, overflow = TextOverflow.Ellipsis)
                    }
                }
                CatalogImage(imagePath, name, Modifier.size(84.dp))
            }
            Row(Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        if (validPrice) NumberFormat.getCurrencyInstance(Locale("es", "ES")).format(price)
                        else "Precio no disponible",
                        style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold,
                        color = BelozColors.Ink
                    )
                    if (quantityInCart > 0) {
                        Text("$quantityInCart en carrito", style = MaterialTheme.typography.labelSmall,
                            color = BelozColors.MutedGreen)
                    }
                }
                FilledTonalButton(
                    onClick = { onAddToCart(1) }, enabled = validPrice,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = BelozColors.SoftMint, contentColor = BelozColors.Ink),
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Añadir")
                }
            }
        }
    }
}
