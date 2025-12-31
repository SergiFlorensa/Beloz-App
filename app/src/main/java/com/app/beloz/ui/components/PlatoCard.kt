package com.app.beloz.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.app.beloz.R
import com.app.beloz.theme.DanfordFontFamily

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

    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            val painter = rememberAsyncImagePainter(
                model = imagePath?.let { "https://beloz-production.up.railway.app/images/$it" }
            )
            val painterState = painter.state

            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painter,
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                when (painterState) {
                    is AsyncImagePainter.State.Loading -> {
                        Image(
                            painter = painterResource(R.drawable.loading),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .align(Alignment.Center)
                        )
                    }
                    is AsyncImagePainter.State.Error -> {
                        Image(
                            painter = painterResource(R.drawable.loading),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .align(Alignment.Center)
                        )
                    }

                    else -> {}
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Text(
                    text = name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = DanfordFontFamily,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "€${String.format("%.2f", price)}",
                    fontSize = 16.sp,
                    color = Color(0xFF285346),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = {
                        if (quantity > 0) quantity--
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.less),
                        contentDescription = "Disminuir cantidad",
                        tint = Color.Black
                    )
                }

                Text(
                    text = quantity.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = Color.Black
                )

                IconButton(
                    onClick = { quantity++ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Aumentar cantidad",
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onAddToCart(quantity) },
                enabled = quantity > 0,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Añadir al Carrito", color = Color.Black)
            }
        }
    }
}
