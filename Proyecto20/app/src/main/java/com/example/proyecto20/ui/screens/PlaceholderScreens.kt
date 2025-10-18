package com.example.proyecto20.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyecto20.ui.theme.Proyecto20Theme

// Un Composable genérico para usar como pantalla temporal (placeholder).
// Lo usamos para la pantalla del Entrenador, que aún no hemos construido.
@Composable
fun PlaceholderScreen(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, style = MaterialTheme.typography.headlineMedium)
    }
}

// Una vista previa solo para este componente
@Preview(showBackground = true)
@Composable
fun PlaceholderScreenPreview() {
    Proyecto20Theme {
        PlaceholderScreen(text = "Pantalla de Ejemplo")
    }
}
