package com.example.proyecto20.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyecto20.model.DiaEntrenamiento
import com.example.proyecto20.model.EjercicioRutina
import com.example.proyecto20.ui.viewmodels.PlanificacionViewModel

// Esta pantalla REUTILIZA el PlanificacionViewModel porque la lógica para CARGAR la rutina es la misma.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualizacionRutinaScreen(
    viewModel: PlanificacionViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEjercicioDetail: (String) -> Unit
) {
    // Esta línea AHORA debe funcionar porque el ViewModel SÍ expone 'rutina'.
    val rutina by viewModel.rutina.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Rutina") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (rutina.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Aún no tienes una rutina asignada.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Itera sobre cada día de entrenamiento en la rutina
                items(rutina, key = { it.dia }) { diaEntrenamiento ->
                    DiaRutinaCard(
                        dia = diaEntrenamiento.dia,
                        ejercicios = diaEntrenamiento.ejercicios,
                        onEjercicioClick = { ejercicioId ->
                            onNavigateToEjercicioDetail(ejercicioId)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DiaRutinaCard(
    dia: String,
    ejercicios: List<EjercicioRutina>,
    onEjercicioClick: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = dia,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            if (ejercicios.isEmpty()) {
                Text("Día de descanso.")
            } else {
                ejercicios.forEach { ejercicio ->
                    EjercicioVistaCard(
                        ejercicio = ejercicio,
                        onClick = { onEjercicioClick(ejercicio.ejercicioId) }
                    )
                }
            }
        }
    }
}

@Composable
fun EjercicioVistaCard(ejercicio: EjercicioRutina, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(ejercicio.nombre, style = MaterialTheme.typography.bodyLarge)
            val detalles = mutableListOf<String>()
            detalles.add("${ejercicio.series} series")
            detalles.add("${ejercicio.repeticiones} reps")
            ejercicio.rir?.let { detalles.add("RIR $it") }
            ejercicio.peso?.let { detalles.add("${it}kg") }
            Text(
                text = detalles.joinToString(" • "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Ver detalles")
    }
}

