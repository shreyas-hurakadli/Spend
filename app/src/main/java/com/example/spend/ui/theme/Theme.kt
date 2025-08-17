package com.example.spend.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF353839),
    secondary = Color(0xFF6200EE),
    tertiary = Color(0xFFF5F5F5),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color(0xFF303030),
    background = Color.White,
    onBackground = Color.Black
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF353839),
    secondary = Color(0xFF6200EE),
    tertiary = Color(0xFFF5F5F5),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color(0xFF303030),
    background = Color.White,
    onBackground = Color.Black
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