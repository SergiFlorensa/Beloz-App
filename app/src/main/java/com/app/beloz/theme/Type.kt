package com.app.beloz.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Retain the shared typeface API for existing screens.
val DanfordFontFamily = FontFamily.SansSerif
private fun type(size: Int, height: Int, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = FontFamily.SansSerif, fontWeight = weight,
    fontSize = size.sp, lineHeight = height.sp, letterSpacing = 0.sp
)
val Typography = Typography(
    displayLarge = type(40, 48, FontWeight.Bold),
    displayMedium = type(36, 44, FontWeight.Bold),
    displaySmall = type(32, 40, FontWeight.Bold),
    headlineLarge = type(28, 36, FontWeight.Bold),
    headlineMedium = type(24, 32, FontWeight.Bold),
    headlineSmall = type(22, 28, FontWeight.SemiBold),
    titleLarge = type(20, 28, FontWeight.SemiBold),
    titleMedium = type(16, 24, FontWeight.SemiBold),
    titleSmall = type(14, 20, FontWeight.SemiBold),
    bodyLarge = type(16, 24), bodyMedium = type(14, 21), bodySmall = type(12, 18),
    labelLarge = type(14, 20, FontWeight.SemiBold),
    labelMedium = type(12, 18, FontWeight.Medium),
    labelSmall = type(11, 16, FontWeight.Medium)
)
