package com.example.proyecto20.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // 1. IMPORT NECESARIO
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyecto20.model.TipoAlumno
import com.example.proyecto20.model.Usuario
import com.example.proyecto20.ui.viewmodels.AlumnosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisAlumnosScreen(
    viewModel: AlumnosViewModel,
    onNavigateToCrearAlumno: () -> Unit,
    onNavigateToPlanificacion: (String) -> Unit,
    onNavigateBack: () -> Unit // 2. SE AÑADE EL NUEVO PARÁMETRO
) {
    val alumnos by viewModel.alumnos.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Alumnos") },
                // 3. SE AÑADE EL ICONO DE NAVEGACIÓN PARA VOLVER
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCrearAlumno) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Alumno")
            }
        }
    ) { padding ->
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            alumnos.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Aún no tienes alumnos. Pulsa (+) para añadir uno.")
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding), // 4. SE APLICA EL PADDING A TODOS LOS ELEMENTOS
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(alumnos) { alumno ->
                        AlumnoCard(
                            alumno = alumno,
                            onClick = { onNavigateToPlanificacion(alumno.id) }
                        )
                    }
                }
            }
        }
    }
}

// --- El resto del archivo no necesita cambios ---
@Composable
fun AlumnoCard(
    alumno: Usuario,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Fila superior: Nombre y Tipo de Alumno (Online/Presencial)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = alumno.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                // Chip para indicar el tipo de alumno
                AssistChip(
                    onClick = { /* No hace nada, es solo informativo */ },
                    label = { Text(alumno.tipo.name) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (alumno.tipo == TipoAlumno.PRESENCIAL) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            }

            // Correo del alumno
            Text(
                text = alumno.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Días de entrenamiento
            Text("Días de Entrenamiento:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            if (alumno.rutina.isEmpty()) {
                Text("Sin rutina asignada", style = MaterialTheme.typography.bodySmall)
            } else {
                DiasEntrenamientoRow(dias = alumno.rutina.map { it.dia })
            }
        }
    }
}

@Composable
fun DiasEntrenamientoRow(dias: List<String>) {
    // Mapeo de nombre completo a abreviatura
    val mapaDias = mapOf(
        "LUNES" to "Lu",
        "MARTES" to "Ma",
        "MIÉRCOLES" to "Mi",
        "JUEVES" to "Ju",
        "VIERNES" to "Vi",
        "SÁBADO" to "Sá",
        "DOMINGO" to "Do"
    )

    // Orden de los días para mostrarlos siempre igual
    val ordenDias = listOf("LUNES", "MARTES", "MIÉRCOLES", "JUEVES", "VIERNES", "SÁBADO", "DOMINGO")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ordenDias.forEach { diaCompleto ->
            val abreviatura = mapaDias[diaCompleto] ?: ""
            val tieneEntrenamiento = dias.contains(diaCompleto)

            Text(
                text = abreviatura,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (tieneEntrenamiento) FontWeight.Bold else FontWeight.Normal,
                color = if (tieneEntrenamiento) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f)
            )
        }
    }
}
