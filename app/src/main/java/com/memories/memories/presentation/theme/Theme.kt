package com.memories.memories.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = AmberDark,
    onPrimary = Cream,
    primaryContainer = AmberLight,
    onPrimaryContainer = Brown,
    secondary = Brown,
    onSecondary = Cream,
    secondaryContainer = BrownSoft,
    onSecondaryContainer = Cream,
    tertiary = Green,
    background = Cream,
    onBackground = Brown,
    surface = SurfaceWarm,
    onSurface = Brown,
    surfaceVariant = AmberLight,
    outline = Border,
    error = Red
)

@Composable
fun MemoriesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}
