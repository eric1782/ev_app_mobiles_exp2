package com.example.proyecto20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
fun AlumnoDetailScreen_EntrenadorView(
    alumnoId: String,
    onNavigateBack: () -> Unit
) {
    val alumno = MockData.todosLosUsuarios.find { it.id == alumnoId }

    if (alumno == null) {
        // ... (código para alumno no encontrado)
        return
    }

    // Usamos una variable local en lugar de 'by remember' para evitar el smart cast impossible.
    val rutina = remember { MockData.todasLasRutinas.find { it.idAlumno == alumnoId } }

    var ejercicioAEditar by remember { mutableStateOf<Pair<String, EjercicioRutina>?>(null) }
    var diaParaAnadirEjercicio by remember { mutableStateOf<String?>(null) }
    var showAddDiaDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(alumno.nombre) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDiaDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir día de entrenamiento")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text("Email: ${alumno.email}", style = MaterialTheme.typography.bodyLarge)
                Text("Tipo de Cliente: ${alumno.tipoCliente ?: "No especificado"}", style = MaterialTheme.typography.bodyLarge)
            }

            item {
                // Ahora llama a la versión oficial de ComponentesComunes.kt
                SeccionDiasEntrenamiento(alumno = alumno)
            }

            if (rutina != null) {
                item {
                    Text("Rutina Asignada", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }

                // Usamos una copia local de rutina para el smart cast.
                val rutinaValida = rutina
                items(rutinaValida.ejerciciosPorDia.keys.toList()) { dia ->
                    DiaEntrenamientoCard(
                        dia = dia,
                        ejercicios = rutinaValida.ejerciciosPorDia[dia] ?: emptyList(),
                        onEditClick = { ejercicio ->
                            ejercicioAEditar = Pair(dia, ejercicio)
                        },
                        onAddClick = {
                            diaParaAnadirEjercicio = dia
                        }
                    )
                }
            } else {
                item {
                    Text("Este alumno todavía no tiene una rutina asignada.")
                }
            }
        }
    }

    // Aquí iría la lógica para mostrar diálogos...
}

@Preview(showBackground = true)
@Composable
fun AlumnoDetailScreen_EntrenadorViewPreview() {
    Proyecto20Theme {
        AlumnoDetailScreen_EntrenadorView(alumnoId = "user002", onNavigateBack = {})
    }
}
