package com.app.beloz.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.app.beloz.data.remote.ImageUrlResolver

@Suppress("UNUSED_PARAMETER")
@Composable
fun RestauranteCard(
    imagePath: String?,
    name: String,
    waitTime: Int,
    priceLevel: String,
    typeOfFood: String,
    country: String,
    valoracion: Double,
    relevancia: Int,
    recomendacionMotivo: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(
            role = Role.Button, onClickLabel = "Ver carta de $name", onClick = onClick
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, BelozColors.MutedText.copy(alpha = 0.15f)),
        colors = CardDefaults.cardColors(containerColor = BelozColors.Card)
    ) {
        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CatalogImage(imagePath, name, Modifier.size(88.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(name, style = MaterialTheme.typography.titleMedium, color = BelozColors.Ink,
                    fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold,
                    maxLines = 3, overflow = TextOverflow.Ellipsis)
                Text(listOf(typeOfFood, country).filter { it.isNotBlank() }.joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall, color = BelozColors.MutedText)
                Text(
                    listOf(
                        com.app.beloz.innovacion.decision.RestaurantDecision.priceLabel(priceLevel),
                        if (waitTime > 0) "Espera aprox. $waitTime min" else "Espera sin indicar"
                    ).joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall, color = BelozColors.Ink
                )
                if (!recomendacionMotivo.isNullOrBlank()) {
                    Text(recomendacionMotivo, style = MaterialTheme.typography.bodySmall,
                        color = BelozColors.MutedGreen)
                }
            }
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null,
                tint = BelozColors.MutedText, modifier = Modifier.size(20.dp).align(Alignment.CenterVertically))
        }
    }
}

@Composable
internal fun CatalogImage(imagePath: String?, name: String, modifier: Modifier = Modifier) {
    val resolved = ImageUrlResolver.resolve(imagePath)
    val painter = rememberAsyncImagePainter(resolved)
    Box(modifier.clip(RoundedCornerShape(6.dp)).background(BelozColors.MintSurface),
        contentAlignment = Alignment.Center) {
        if (resolved != null) {
            Image(painter, contentDescription = name, contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize())
        }
        when {
            resolved == null || painter.state is AsyncImagePainter.State.Error ->
                Text("Sin foto", style = MaterialTheme.typography.labelSmall, color = BelozColors.MutedText)
            painter.state is AsyncImagePainter.State.Loading ->
                CircularProgressIndicator(Modifier.size(20.dp), color = BelozColors.Green, strokeWidth = 2.dp)
        }
    }
}
