package com.example.spend.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF353839),
    secondary = Color(0xFF8C6EFF),
    tertiary = Color(0xFFF5F5F5),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color(0xFF303030),
    background = Color(0xFFF4F5F7),
    onBackground = Color.Black,
    surface = Color(0xFFF4F5F7),
    onSurface = Color.Black,
    primaryContainer = Color(0xFF8C6EFF),
    onPrimaryContainer = Color.White,
    inverseSurface = Color(0xFF353839),
    inverseOnSurface = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF353839),
    secondary = Color(0xFF8C6EFF),
    tertiary = Color(0xFFF5F5F5),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color(0xFF303030),
    background = Color(0xFFF4F5F7),
    onBackground = Color.Black,
    surface = Color(0xFFF4F5F7),
    onSurface = Color.Black,
    primaryContainer = Color(0xFF8C6EFF),
    onPrimaryContainer = Color.White,
    inverseSurface = Color(0xFF353839),
    inverseOnSurface = Color.White
)

@Composable
fun SpendTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}