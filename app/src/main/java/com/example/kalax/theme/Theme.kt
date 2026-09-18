package com.example.kalax.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KalaColorScheme = lightColorScheme(
    primary = KalaDeepAccent,
    onPrimary = Color.White,
    primaryContainer = KalaAccent,
    onPrimaryContainer = KalaTextPrimary,
    secondary = KalaAccent,
    onSecondary = KalaTextPrimary,
    secondaryContainer = KalaCard,
    onSecondaryContainer = KalaTextPrimary,
    tertiary = KalaAccent,
    onTertiary = Color.White,
    background = KalaBg,
    onBackground = KalaTextPrimary,
    surface = KalaCard,
    onSurface = KalaTextPrimary,
    surfaceVariant = KalaSurfaceVariant,
    onSurfaceVariant = KalaTextSecondary,
    outline = KalaAccent,
    outlineVariant = KalaCard,
)

@Composable
fun KALAXTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = KalaColorScheme,
        typography = Typography,
        content = content
    )
}
