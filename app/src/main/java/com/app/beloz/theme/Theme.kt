package com.app.beloz.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF56E39F),
    onPrimary = Color(0xFF10231E),
    secondary = Color(0xFFF7D560),
    onSecondary = Color(0xFF10231E),
    tertiary = Color(0xFFFFB24D),
    background = Color(0xFF10231E),
    surface = Color(0xFF12372D),
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF56E39F),
    onPrimary = Color(0xFF10231E),
    secondary = Color(0xFF245B4B),
    onSecondary = Color.White,
    tertiary = Color(0xFFFFB24D),
    onTertiary = Color(0xFF10231E),
    background = Color(0xFFF4FAF6),
    onBackground = Color(0xFF10231E),
    surface = Color.White,
    onSurface = Color(0xFF10231E),
    surfaceVariant = Color(0xFFEAF8EF),
    onSurfaceVariant = Color(0xFF5D7068)
)

@Composable
fun BelozTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val systemBarColor = Color(0xFFF4FAF6).toArgb()
            window.statusBarColor = systemBarColor
            window.navigationBarColor = systemBarColor
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = true
                isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
