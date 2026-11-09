package com.app.beloz.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun RestaurantePopularItem(
    imageUrl: String,
    onClick: () -> Unit,
    restaurantId: Int
) {
    Surface(
        modifier = Modifier.size(80.dp).clickable(
            role = Role.Button, onClickLabel = "Ver carta del restaurante $restaurantId", onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = BelozColors.Card
    ) {
        CatalogImage(imageUrl, "Restaurante $restaurantId", Modifier.size(80.dp))
    }
}
