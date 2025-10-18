package com.example.proyecto20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyecto20.data.MockData
import com.example.proyecto20.model.Ejercicio
import com.example.proyecto20.model.EjercicioRutina
import com.example.proyecto20.model.Usuario
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun SeccionDiasEntrenamiento(alumno: Usuario) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Días de Entrenamiento", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        if (alumno.diasEntrenamiento.isNullOrEmpty()) {
            Text("No hay días de entrenamiento asignados.")
        } else {
            val diasOrdenados = alumno.diasEntrenamiento.sortedBy { it.value }
            Text(diasOrdenados.joinToString(", ") { it.getDisplayName(TextStyle.FULL, Locale("es", "ES")) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaEntrenamientoCard(
    dia: String,
    ejercicios: List<EjercicioRutina>,
    onEditClick: (EjercicioRutina) -> Unit,
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(dia, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                IconButton(onClick = onAddClick) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir ejercicio a $dia")
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            if (ejercicios.isEmpty()) {
                Text("Día de descanso o sin ejercicios asignados.")
            } else {
                ejercicios.forEach { ejercicioRutina ->
                    val ejercicioInfo = MockData.todosLosEjercicios.find { it.id == ejercicioRutina.idEjercicio }
                    EjercicioRow(
                        ejercicioInfo = ejercicioInfo,
                        ejercicioRutina = ejercicioRutina,
                        onEditClick = { onEditClick(ejercicioRutina) }
                    )
                }
            }
        }
    }
}

@Composable
fun EjercicioRow(
    ejercicioInfo: Ejercicio?,
    ejercicioRutina: EjercicioRutina,
    onEditClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(ejercicioInfo?.nombre ?: "Ejercicio desconocido", fontWeight = FontWeight.Bold)
            Text(
                "${ejercicioRutina.series} series x ${ejercicioRutina.repeticiones} reps con ${ejercicioRutina.pesoRecomendadoKg} kg",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        IconButton(onClick = onEditClick) {
            Icon(Icons.Default.Edit, contentDescription = "Editar ejercicio")
        }
    }
}

// --- CÓDIGO AÑADIDO ---
/**
 * Tarjeta que muestra la información básica de un alumno.
 * Es reutilizable en la pantalla principal del entrenador y en MisAlumnosScreen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlumnoCard(
    alumno: Usuario,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = alumno.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = alumno.email,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
