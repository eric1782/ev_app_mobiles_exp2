package com.example.proyecto20.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

private val DarkGradientColors = listOf(
    Color(0xFF0B0D13),
    Color(0xFF111624),
    Color(0xFF0B0D13)
)

/**
 * Wrapper para pantallas secundarias sin fotografía de fondo. Aplica un gradiente oscuro
 * que combina con la línea visual de las imágenes principales.
 */
@Composable
fun GradientSurface(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = DarkGradientColors
                )
            )
    ) {
        content()
    }
}

