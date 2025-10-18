package com.example.proyecto20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyecto20.data.MockData
import com.example.proyecto20.model.Ejercicio
import com.example.proyecto20.model.EjercicioRutina
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlumnoMainScreen(
    alumnoId: String,
    onNavigateToEjercicioDetail: (String) -> Unit
) {
    val alumno = MockData.todosLosUsuarios.find { it.id == alumnoId }

    if (alumno == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: Alumno no encontrado.")
        }
        return
    }

    // --- CORRECCIÓN 1: Obtener la rutina correctamente ---
    // Buscamos la rutina que le pertenece a este alumno en la lista global
    val rutina = MockData.todasLasRutinas.find { it.idAlumno == alumnoId }

    // Obtenemos el nombre del día de hoy en mayúsculas y español para buscar en el mapa
    val hoy = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es", "ES")).uppercase()

    // Buscamos los ejercicios que tocan para el día de hoy
    val ejerciciosDeHoy = rutina?.ejerciciosPorDia?.get(hoy) ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Hola, ${alumno.nombre}") })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Tu entrenamiento para hoy, $hoy:",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // --- CORRECCIÓN 3 y 6: Usar LazyColumn/items para Composables ---
            if (ejerciciosDeHoy.isNotEmpty()) {
                items(ejerciciosDeHoy) { ejercicioRutina ->
                    // Buscamos la información completa del ejercicio en el catálogo
                    val ejercicioInfo = MockData.todosLosEjercicios.find { it.id == ejercicioRutina.idEjercicio }

                    RutinaHoyCard(
                        ejercicioInfo = ejercicioInfo,
                        ejercicioRutina = ejercicioRutina,
                        onClick = {
                            if (ejercicioInfo != null) {
                                onNavigateToEjercicioDetail(ejercicioInfo.id)
                            }
                        }
                    )
                }
            } else {
                item {
                    Text(
                        "Hoy es tu día de descanso. ¡Disfrútalo!",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

/**
 * Tarjeta para mostrar un ejercicio de la rutina de hoy.
 */
@Composable
fun RutinaHoyCard(
    ejercicioInfo: Ejercicio?,
    ejercicioRutina: EjercicioRutina,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick, // Hacemos la tarjeta clickeable
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = ejercicioInfo?.nombre ?: "Ejercicio desconocido",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            EjercicioSimpleRow(label = "Series:", value = "${ejercicioRutina.series}")
            EjercicioSimpleRow(label = "Repeticiones:", value = ejercicioRutina.repeticiones)
            EjercicioSimpleRow(label = "Peso recomendado:", value = "${ejercicioRutina.pesoRecomendadoKg} kg")

            if (!ejercicioRutina.notas.isNullOrBlank()) {
                EjercicioSimpleRow(label = "Notas:", value = ejercicioRutina.notas)
            }
        }
    }
}

/**
 * Fila simple para mostrar una etiqueta y un valor.
 */
@Composable
fun EjercicioSimpleRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(160.dp) // Ancho fijo para alinear
        )
        Text(text = value)
    }
}

