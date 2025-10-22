package com.example.proyecto20.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyecto20.model.Ejercicio
import com.example.proyecto20.model.EjercicioRutina
import com.example.proyecto20.ui.viewmodels.PlanificacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanificacionRutinaScreen(
    viewModel: PlanificacionViewModel,
    onNavigateBack: () -> Unit
) {
    // Estas son las propiedades que SÍ existen en el ViewModel que te he dado
    val dias = listOf("LUNES", "MARTES", "MIÉRCOLES", "JUEVES", "VIERNES", "SÁBADO", "DOMINGO")
    val diaSeleccionado by viewModel.diaSeleccionado.collectAsState()
    val ejerciciosDelDia by viewModel.ejerciciosDelDiaSeleccionado.collectAsState(initial = emptyList())
    val catalogoEjercicios by viewModel.catalogoEjercicios.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    var mostrarDialogoEjercicios by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Planificar Rutina") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    Button(onClick = { viewModel.guardarRutina() }, enabled = !isSaving) {
                        Text(if (isSaving) "Guardando..." else "Guardar")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Selector de Días
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(dias) { dia ->
                    FilterChip(
                        selected = (dia == diaSeleccionado),
                        onClick = { viewModel.seleccionarDia(dia) },
                        label = { Text(dia) }
                    )
                }
            }

            HorizontalDivider()

            if (diaSeleccionado != null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(ejerciciosDelDia, key = { it.ejercicioId }) { ejercicio ->
                            EjercicioRutinaCard(
                                ejercicio = ejercicio,
                                onDelete = { viewModel.eliminarEjercicioDeRutina(ejercicio.ejercicioId) }
                            )
                        }
                    }
                    FloatingActionButton(
                        onClick = { mostrarDialogoEjercicios = true },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
                    ) {
                        Icon(Icons.Default.Add, "Añadir ejercicio")
                    }
                }
            } else {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("Selecciona un día para ver o añadir ejercicios")
                }
            }
        }
    }

    if (mostrarDialogoEjercicios && diaSeleccionado != null) {
        DialogoAnadirEjercicio(
            ejercicios = catalogoEjercicios,
            onDismiss = { mostrarDialogoEjercicios = false },
            onEjercicioSelected = { ejercicio ->
                viewModel.anadirEjercicioARutina(ejercicio)
                mostrarDialogoEjercicios = false
            }
        )
    }
}

@Composable
fun EjercicioRutinaCard(ejercicio: EjercicioRutina, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(ejercicio.nombre, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Eliminar")
            }
        }
    }
}

@Composable
fun DialogoAnadirEjercicio(
    ejercicios: List<Ejercicio>,
    onDismiss: () -> Unit,
    onEjercicioSelected: (Ejercicio) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir Ejercicio") },
        text = {
            LazyColumn {
                items(ejercicios) { ejercicio ->
                    Text(
                        text = ejercicio.nombre,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEjercicioSelected(ejercicio) }
                            .padding(vertical = 12.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
