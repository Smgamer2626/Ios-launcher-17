package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AppleBlueDark,
    onPrimary = Color.White,
    primaryContainer = AppleIndigoDark,
    onPrimaryContainer = Color.White,
    secondary = AppleTealDark,
    onSecondary = Color.Black,
    background = AppleSystemBackgroundDark,
    onBackground = Color.White,
    surface = AppleSecondaryBackgroundDark,
    onSurface = Color.White,
    surfaceVariant = AppleTertiaryBackgroundDark,
    onSurfaceVariant = Color(0xFFEBEBF5)
)

private val LightColorScheme = lightColorScheme(
    primary = AppleBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE5F0FF),
    onPrimaryContainer = AppleBlue,
    secondary = AppleTeal,
    onSecondary = Color.White,
    background = AppleSystemBackgroundLight,
    onBackground = Color.Black,
    surface = AppleSecondaryBackgroundLight,
    onSurface = Color.Black,
    surfaceVariant = AppleTertiaryBackgroundLight,
    onSurfaceVariant = Color(0xFF3C3C43)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
