// Ruta: app/src/main/java/com/example/proyecto20/ui/screens/ComponentesComunes.kt

package com.example.proyecto20.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
// --- INICIO CORRECCIÓN 1: IMPORT NECESARIO ---
import androidx.compose.foundation.layout.RowScope
// --- FIN CORRECCIÓN 1 ---
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object DarkMatterPalette {
    val Highlight = Color(0xFFFFEB3B)
    val PrimaryText = Color(0xFFEFEFEF)
    val SecondaryText = Color(0xFFBDBDBD)
    val Divider = Color(0x26FFFFFF)
}

@Composable
fun DarkMatterButtonColors(): ButtonColors = ButtonDefaults.buttonColors(
    containerColor = DarkMatterPalette.Highlight,
    contentColor = Color.Black,
    disabledContainerColor = DarkMatterPalette.Highlight.copy(alpha = 0.35f),
    disabledContentColor = Color.Black.copy(alpha = 0.6f)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkMatterTopAppBarColors(): TopAppBarColors = TopAppBarDefaults.topAppBarColors(
    containerColor = Color.Transparent,
    titleContentColor = DarkMatterPalette.Highlight,
    navigationIconContentColor = Color.White,
    actionIconContentColor = Color.White
)

private val DarkGradientColors = listOf(
    Color(0xFF141414),
    Color(0xFF0E0E0E),
    Color(0xFF060606)
)

@Composable
fun DarkMatterBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val gradientBrush = remember {
        Brush.verticalGradient(DarkGradientColors)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradientBrush)
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkMatterTopBar(
    title: String,
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    centerTitle: Boolean = false
) {
    val topBarColors = DarkMatterTopAppBarColors()

    val navigationIcon: @Composable (() -> Unit) = {
        if (onNavigateBack != null) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }
        }
    }

    val titleContent: @Composable () -> Unit = {
        Text(
            text = title,
            color = DarkMatterPalette.Highlight,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
    }

    if (centerTitle) {
        CenterAlignedTopAppBar(
            modifier = modifier,
            title = titleContent,
            navigationIcon = navigationIcon,
            actions = actions,
            colors = topBarColors
        )
    } else {
        TopAppBar(
            modifier = modifier,
            title = titleContent,
            navigationIcon = navigationIcon,
            actions = actions,
            colors = topBarColors
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
// --- INICIO CORRECCIÓN 2: AÑADIR CONTEXTO RowScope ---
@Composable
fun RowScope.ModernNavigationBarItem(
// --- FIN CORRECCIÓN 2 ---
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    iconContentDescription: String? = null
) {
    // Animación de escala cuando está seleccionado
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.15f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "scale_animation"
    )

    // Colores personalizados sin background blanco
    val colors = NavigationBarItemDefaults.colors(
        selectedIconColor = DarkMatterPalette.Highlight,
        selectedTextColor = DarkMatterPalette.Highlight,
        indicatorColor = Color.Transparent, // Sin background blanco
        unselectedIconColor = Color(0xFF2196F3),
        unselectedTextColor = Color(0xFF2196F3)
    )

    NavigationBarItem(
        icon = {
            Box(modifier = Modifier.scale(scale)) {
                Icon(
                    imageVector = icon,
                    contentDescription = iconContentDescription ?: label
                    // El 'tint' es manejado por 'colors'
                )
            }
        },
        label = {
            Text(
                text = label,
                fontSize = 11.sp
                // El 'color' es manejado por 'colors'
            )
        },
        selected = selected,
        onClick = onClick,
        colors = colors
    )
}
