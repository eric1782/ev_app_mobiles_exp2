package com.example.proyecto20.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyecto20.model.EjercicioRutina
import com.example.proyecto20.ui.viewmodels.PlanificacionViewModel
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualizacionRutinaScreen(
    viewModel: PlanificacionViewModel,
    onNavigateBack: () -> Unit,
    // CAMBIAMOS LA FIRMA DE LA LAMBDA
    onNavigateToEjercicioDetail: (EjercicioRutina) -> Unit
) {
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Aún no tienes una rutina asignada.")
            }
        } else {
            val diasOrdenados = listOf("LUNES", "MARTES", "MIÉRCOLES", "JUEVES", "VIERNES", "SÁBADO", "DOMINGO")
            val diasDeEntrenamiento = rutina.values.sortedBy { diasOrdenados.indexOf(it.dia.uppercase()) }

            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items = diasDeEntrenamiento, key = { it.dia }) { diaEntrenamiento ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = diaEntrenamiento.dia,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            if (diaEntrenamiento.ejercicios.isEmpty()) {
                                Text("Día de descanso.")
                            } else {
                                diaEntrenamiento.ejercicios.forEach { ejercicio ->
                                    EjercicioVistaItem(
                                        ejercicio = ejercicio,
                                        // PASAMOS EL OBJETO COMPLETO
                                        onClick = { onNavigateToEjercicioDetail(ejercicio) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

