package com.example.spend.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF8C6EFF),
    secondary = Color(0xFFFBD472),
    tertiary = Color(0xFFFEFEFE),
    onPrimary = Color(0xFFFEFEFE),
    onSecondary = Color.Black,
    background = Color(0xFFFEFEFE),
    onBackground = Color.DarkGray
)

@Composable
fun SpendTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}