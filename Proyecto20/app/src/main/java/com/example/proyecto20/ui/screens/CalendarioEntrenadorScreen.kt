package com.example.proyecto20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyecto20.model.BloqueHorario
import com.example.proyecto20.ui.viewmodels.CalendarioViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioEntrenadorScreen(
    viewModel: CalendarioViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToRutina: (String) -> Unit
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val todasLasCitas by viewModel.citas.collectAsState()

    // Filtramos la lista completa de citas para mostrar solo las del día seleccionado.
    val citasDelDia = todasLasCitas.filter { bloque ->
        try {
            // Asumimos que `horaInicio` es un String en formato ISO, como "2024-10-28T09:00".
            val fechaDelBloque = LocalDate.parse(bloque.horaInicio.substring(0, 10))
            fechaDelBloque == selectedDate
        } catch (e: Exception) {
            // Si el formato de la fecha es incorrecto, se ignora este bloque.
            false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendario de Citas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            DateSelector(
                currentDate = selectedDate,
                onDateChange = { newDate -> viewModel.onDateSelected(newDate) }
            )
            HorizontalDivider()
            if (citasDelDia.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
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
                    items(citasDelDia, key = { it.id }) { cita ->
                        CitaCard(
                            cita = cita,
                            onClick = {
                                cita.idAlumno?.let { id -> onNavigateToRutina(id) }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DateSelector(currentDate: LocalDate, onDateChange: (LocalDate) -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "ES"))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        IconButton(onClick = { onDateChange(currentDate.minusDays(1)) }) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Día anterior")
        }
        Text(
            text = currentDate.format(formatter).replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = { onDateChange(currentDate.plusDays(1)) }) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Día siguiente")
        }
    }
}

@Composable
fun CitaCard(cita: BloqueHorario, onClick: () -> Unit) {
    val horaInicioFormatted = try { cita.horaInicio.substring(11, 16) } catch (_: Exception) { "" }
    val horaFinFormatted = try { cita.horaFin.substring(11, 16) } catch (_: Exception) { "" }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { if (cita.idAlumno != null) onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (cita.disponible) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (cita.disponible) "Horario Disponible" else (cita.idAlumno ?: "Cita Ocupada"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$horaInicioFormatted - $horaFinFormatted",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (!cita.disponible && cita.idAlumno != null) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Ver rutina")
            }
        }
    }
}
