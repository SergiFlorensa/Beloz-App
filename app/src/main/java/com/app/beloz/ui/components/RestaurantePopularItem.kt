package com.app.beloz.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

@Composable
fun RestaurantePopularItem(
    imageUrl: String,
    onClick: () -> Unit,
    restaurantId: Int
) {
    Surface(
        modifier = Modifier
            .width(80.dp)
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(15.dp),
        color = Color.LightGray.copy(alpha = 0.2f),
        shadowElevation = 6.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberImagePainter(imageUrl),
                contentDescription = "Logo del restaurante",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
