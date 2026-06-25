package com.app.beloz.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

object BelozColors {
    val Ink = Color(0xFF10231E)
    val Deep = Color(0xFF12372D)
    val Green = Color(0xFF56E39F)
    val MutedGreen = Color(0xFF245B4B)
    val MintSurface = Color(0xFFF4FAF6)
    val Card = Color.White
    val SoftMint = Color(0xFFEAF8EF)
    val Gold = Color(0xFFF7D560)
    val Orange = Color(0xFFFFB24D)
    val MutedText = Color(0xFF5D7068)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BelozTopAppBar(
    title: String,
    navController: NavController,
    subtitle: String? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    color = BelozColors.Ink,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        color = BelozColors.MutedText,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = BelozColors.Ink
                )
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BelozColors.MintSurface,
            titleContentColor = BelozColors.Ink,
            navigationIconContentColor = BelozColors.Ink,
            actionIconContentColor = BelozColors.Ink
        )
    )
}
