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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
fun PlatoCard(
    imagePath: String?,
    name: String,
    description: String,
    price: Double,
    onAddToCart: (quantity: Int) -> Unit,
    onClick: () -> Unit
) {
    var quantity by remember { mutableStateOf(0) }
    val resolvedImage = ImageUrlResolver.resolve(imagePath)
    val hasImage = !resolvedImage.isNullOrBlank()
    val painter = rememberAsyncImagePainter(model = resolvedImage)
    val painterState = painter.state

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
            Box(
                modifier = Modifier
                    .height(176.dp)
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
                        painter = painterResource(R.drawable.orderfood),
                        contentDescription = null,
                        modifier = Modifier
                            .size(86.dp)
                            .align(Alignment.Center)
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp),
                    shape = RoundedCornerShape(100.dp),
                    color = BelozColors.Gold
                ) {
                    Text(
                        text = "${String.format("%.2f", price)} EUR",
                        color = BelozColors.Ink,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                color = BelozColors.Ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                fontSize = 13.sp,
                color = BelozColors.MutedText,
                lineHeight = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuantityStepper(
                    quantity = quantity,
                    onMinus = { if (quantity > 0) quantity-- },
                    onPlus = { quantity++ }
                )

                Button(
                    onClick = { onAddToCart(quantity) },
                    enabled = quantity > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BelozColors.Green,
                        contentColor = BelozColors.Ink,
                        disabledContainerColor = BelozColors.SoftMint,
                        disabledContentColor = BelozColors.MutedText
                    ),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.height(46.dp)
                ) {
                    Text(text = "Anadir", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun QuantityStepper(
    quantity: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = BelozColors.SoftMint
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onMinus, modifier = Modifier.size(38.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.less),
                    contentDescription = "Disminuir cantidad",
                    tint = BelozColors.Ink
                )
            }
            Surface(shape = CircleShape, color = Color.White, modifier = Modifier.size(34.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = quantity.toString(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = BelozColors.Ink
                    )
                }
            }
            IconButton(onClick = onPlus, modifier = Modifier.size(38.dp)) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Aumentar cantidad",
                    tint = BelozColors.Ink
                )
            }
        }
    }
}
