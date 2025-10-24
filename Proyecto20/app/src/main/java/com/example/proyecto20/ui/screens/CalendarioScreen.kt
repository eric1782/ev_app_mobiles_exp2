package com.example.proyecto20.ui.screens

import androidx.compose.foundation.clickable // <-- 1. Import necesario
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.proyecto20.model.TipoAlumno
import com.example.proyecto20.ui.viewmodels.CalendarioViewModel
import com.example.proyecto20.ui.viewmodels.CitaCalendario
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioScreen(
    viewModel: CalendarioViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPlanificacion: (String) -> Unit // <-- 2. Se añade el nuevo parámetro de navegación
) {
    val fechaSeleccionada by viewModel.fechaSeleccionada.collectAsState()
    val citasDelDia by viewModel.citasDelDia.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    var mostrarDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendario de Citas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Selector de fecha
            DateSelector(
                fecha = fechaSeleccionada,
                onDiaAnterior = { viewModel.cambiarDia(-1) },
                onDiaSiguiente = { viewModel.cambiarDia(1) },
                onSelectorFechaClick = { mostrarDatePicker = true }
            )

            // Contenido principal
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (citasDelDia.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay alumnos para este día.")
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(citasDelDia, key = { it.alumnoId }) { cita ->
                        // <-- 3. Se pasa la acción de navegación a la tarjeta
                        CitaCalendarioCard(
                            cita = cita,
                            onClick = { onNavigateToPlanificacion(cita.alumnoId) }
                        )
                    }
                }
            }
        }

        // Diálogo para seleccionar la fecha
        if (mostrarDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = fechaSeleccionada.toEpochDay() * 24 * 60 * 60 * 1000
            )
            DatePickerDialog(
                onDismissRequest = { mostrarDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModel.seleccionarFecha(
                                LocalDate.ofEpochDay(millis / (1000 * 60 * 60 * 24))
                            )
                        }
                        mostrarDatePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDatePicker = false }) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
fun DateSelector(
    fecha: LocalDate,
    onDiaAnterior: () -> Unit,
    onDiaSiguiente: () -> Unit,
    onSelectorFechaClick: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "ES"))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onDiaAnterior) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Día anterior")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.clickable { onSelectorFechaClick() } // Hacer clic en la fecha también abre el picker
        ) {
            Text(
                text = fecha.format(formatter).replaceFirstChar { it.titlecase(Locale.getDefault()) },
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Icon(Icons.Default.CalendarMonth, "Seleccionar fecha", modifier = Modifier.size(20.dp))
        }

        IconButton(onClick = onDiaSiguiente) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Día siguiente")
        }
    }
}

@Composable
fun CitaCalendarioCard(
    cita: CitaCalendario,
    onClick: () -> Unit // <-- 4. La tarjeta ahora recibe un parámetro onClick
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick) // <-- 5. Se hace que toda la tarjeta sea clicable
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = cita.nombreAlumno,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            AssistChip(
                onClick = { /* No-op, la acción está en la tarjeta */ },
                label = { Text(cita.detalle) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (cita.tipo == TipoAlumno.PRESENCIAL) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }
    }
}
