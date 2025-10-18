package com.example.proyecto20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyecto20.data.MockData
import com.example.proyecto20.model.EjercicioRutina
import com.example.proyecto20.ui.theme.Proyecto20Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EjercicioDetailScreen(
    ejercicioId: String,
    onNavigateBack: () -> Unit
) {
    // 1. Buscamos la información GENERAL del ejercicio en el catálogo.
    val ejercicio = MockData.todosLosEjercicios.find { it.id == ejercicioId }

    // 2. Buscamos los detalles ESPECÍFICOS de este ejercicio DENTRO de una rutina.
    // Esto es más complejo porque el ejercicio puede estar en cualquier rutina.
    // Esta función busca en todas las rutinas y devuelve el primer EjercicioRutina que coincida.
    fun encontrarEjercicioEnRutinas(id: String): EjercicioRutina? {
        for (rutina in MockData.todasLasRutinas) {
            for (ejerciciosDelDia in rutina.ejerciciosPorDia.values) {
                val ejercicioEncontrado = ejerciciosDelDia.find { it.idEjercicio == id }
                if (ejercicioEncontrado != null) {
                    return ejercicioEncontrado
                }
            }
        }
        return null
    }

    // Llamamos a la función para obtener los detalles de la rutina (series, reps, peso).
    val detallesRutina = encontrarEjercicioEnRutinas(ejercicioId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(ejercicio?.nombre ?: "Detalle del Ejercicio") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (ejercicio == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Ejercicio no encontrado.")
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección de Video (simulada)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Aquí iría el reproductor de video para '${ejercicio.urlVideo}'")
                }
            }

            // Sección de descripción
            item {
                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = ejercicio.descripcion,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // --- CORRECCIÓN ---
            // Sección de detalles de la rutina (si se encontraron)
            if (detallesRutina != null) {
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = "En tu rutina",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    InfoRow(label = "Series", value = detallesRutina.series.toString())
                    InfoRow(label = "Repeticiones", value = detallesRutina.repeticiones)
                    InfoRow(label = "Peso Recomendado", value = "${detallesRutina.pesoRecomendadoKg} kg")
                    if (!detallesRutina.notas.isNullOrBlank()) {
                        InfoRow(label = "Notas", value = detallesRutina.notas)
                    }
                }
            }
        }
    }
}

/**
 * Un Composable auxiliar para mostrar una fila de información (etiqueta y valor).
 */
@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(180.dp) // Ancho fijo para alinear
        )
        Text(text = value)
    }
}


@Preview(showBackground = true)
@Composable
fun EjercicioDetailScreenPreview() {
    Proyecto20Theme {
        // Probamos con un ejercicio que sabemos que está en una rutina (Press de Banca)
        EjercicioDetailScreen(ejercicioId = "ej001", onNavigateBack = {})
    }
}
