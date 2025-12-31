package com.app.beloz.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.app.beloz.R

@Composable
fun IconoLocalizacion(
    iconSize: Dp = 40.dp,
    onPressed: () -> Unit
) {
    val iconPath = R.drawable.locationmap

    IconButton(
        modifier = Modifier.size(iconSize),
        onClick = onPressed
    ) {
        Image(
            painter = painterResource(id = iconPath),
            contentDescription = "Ubicación",
            modifier = Modifier.size(iconSize)
        )
    }
}
