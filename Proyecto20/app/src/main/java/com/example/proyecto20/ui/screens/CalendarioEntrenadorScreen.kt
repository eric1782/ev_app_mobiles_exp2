package com.example.proyecto20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyecto20.model.CitaMostrable
import com.example.proyecto20.util.GestorDeCitas
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioEntrenadorScreen(
    // --- CAMBIO 1: Añadimos el ID del entrenador como parámetro ---
    entrenadorId: String,
    onNavigateBack: () -> Unit,
    onNavigateToRutina: (alumnoId: String) -> Unit
) {
    var fechaSeleccionada by remember { mutableStateOf(LocalDate.now()) }

    // --- CAMBIO 2: Usamos el ID del entrenador que recibimos ---
    val citasDelDia = remember(fechaSeleccionada, entrenadorId) {
        GestorDeCitas.obtenerCitasParaDia(entrenadorId, fechaSeleccionada)
    }

    var mostrarDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendario Diario") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            DateNavigationBar(
                currentDate = fechaSeleccionada,
                onPreviousDay = { fechaSeleccionada = fechaSeleccionada.minusDays(1) },
                onNextDay = { fechaSeleccionada = fechaSeleccionada.plusDays(1) },
                onCalendarClick = { mostrarDatePicker = true }
            )

            if (citasDelDia.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay citas programadas para este día.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(citasDelDia) { cita ->
                        CitaCard(
                            cita = cita,
                            onVerRutinaClick = { onNavigateToRutina(cita.idAlumno) }
                        )
                    }
                }
            }
        }
        //mostrarDatePicker es para visualizar el icono de calendario, donde puedes seleccionar una fecha del mes
        if (mostrarDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = fechaSeleccionada.atStartOfDay().toInstant(java.time.ZoneOffset.UTC).toEpochMilli()
            )
            DatePickerDialog(
                onDismissRequest = { mostrarDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            fechaSeleccionada = LocalDate.ofInstant(java.time.Instant.ofEpochMilli(millis), java.time.ZoneOffset.UTC)
                        }
                        mostrarDatePicker = false
                    }) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDatePicker = false }) { Text("Cancelar") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}


// DateNavigationBar y CitaCard no cambian, por lo que los incluyo tal cual.

@Composable
fun DateNavigationBar(
    currentDate: LocalDate,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onCalendarClick: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "ES"))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPreviousDay) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Día anterior")
        }

        Text(
            text = currentDate.format(formatter).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Row {
            IconButton(onClick = onCalendarClick) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha")
            }
            IconButton(onClick = onNextDay) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Día siguiente")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitaCard(
    cita: CitaMostrable,
    onVerRutinaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = cita.nombreAlumno,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Hora: ${cita.horaInicio} - ${cita.horaFin}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Button(onClick = onVerRutinaClick) {
                Text("Ver Rutina")
            }
        }
    }
}

