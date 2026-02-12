package com.example.spend.ui.theme

import androidx.compose.material3.MaterialTheme
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
    background = Color.White,
    onBackground = Color.Black,
    surface = Color(0xFFF4F5F7),
    onSurface = Color.Black,
    primaryContainer = Color(0xFF8C6EFF),
    onPrimaryContainer = Color.White,
    inverseSurface = Color(0xFF353839),
    inverseOnSurface = Color.White,
    error = Color(0xFFF44336),
    onError = Color.White
)

@Composable
fun SpendTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}