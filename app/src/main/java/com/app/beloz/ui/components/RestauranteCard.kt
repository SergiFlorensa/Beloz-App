package com.app.beloz.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.app.beloz.R
import com.app.beloz.data.remote.ImageUrlResolver

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
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            val resolvedImage = ImageUrlResolver.resolve(imagePath)
            val hasImage = !resolvedImage.isNullOrBlank()
            val painter = rememberAsyncImagePainter(model = resolvedImage)
            val painterState = painter.state

            Box(
                modifier = Modifier
                    .height(168.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(BelozColors.SoftMint)
            ) {
                if (hasImage) {
                    Image(
                        painter = painter,
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                if (!hasImage || painterState is AsyncImagePainter.State.Loading || painterState is AsyncImagePainter.State.Error) {
                    Image(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.fooddelivery),
                        contentDescription = null,
                        modifier = Modifier
                            .size(86.dp)
                            .align(Alignment.Center)
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RestaurantMetaChip(text = "$waitTime min")
                    RestaurantMetaChip(text = priceLevel)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!recomendacionMotivo.isNullOrBlank()) {
                Surface(
                    color = BelozColors.SoftMint,
                    shape = RoundedCornerShape(100.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = recomendacionMotivo,
                        fontSize = 11.sp,
                        color = BelozColors.MutedGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Text(
                text = name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = BelozColors.Ink,
                fontFamily = FontFamily.SansSerif,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$typeOfFood · $country",
                fontSize = 14.sp,
                color = BelozColors.MutedText,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SoftInfoChip(text = "Valoracion $valoracion", modifier = Modifier.weight(1f))
                SoftInfoChip(text = "Relevancia $relevancia", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun RestaurantMetaChip(text: String) {
    Surface(
        color = Color(0xE610231E),
        shape = RoundedCornerShape(100.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun SoftInfoChip(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = BelozColors.SoftMint,
        shape = RoundedCornerShape(14.dp)
    ) {
        Text(
            text = text,
            color = BelozColors.MutedGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
