package com.example.proyecto20.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable // <-- 1. Import necesario
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.proyecto20.R
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
    onNavigateToPlanificacion: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToAlumnos: () -> Unit,
    onNavigateToEjercicios: () -> Unit
) {
    val fechaSeleccionada by viewModel.fechaSeleccionada.collectAsState()
    val citasDelDia by viewModel.citasDelDia.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    var mostrarDatePicker by remember { mutableStateOf(false) }
    val backgroundPainter = painterResource(id = R.drawable.calendario_background)
    val overlayBrush = remember {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xB0000000),
                Color(0xD0000000)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
    ) {
        Image(
            painter = backgroundPainter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlayBrush)
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Calendario de Citas",
                            color = Color(0xFFFFEB3B),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFF1E1E1E),
                    contentColor = Color.White
                ) {
                    ModernNavigationBarItem(
                        icon = Icons.Default.Home,
                        label = "Hoy",
                        selected = false,
                        onClick = onNavigateToHome
                    )
                    ModernNavigationBarItem(
                        icon = Icons.Default.Person,
                        label = "Alumnos",
                        selected = false,
                        onClick = onNavigateToAlumnos
                    )
                    ModernNavigationBarItem(
                        icon = Icons.Default.CalendarMonth,
                        label = "Calendario",
                        selected = true,
                        onClick = { }
                    )
                    ModernNavigationBarItem(
                        icon = Icons.Default.FitnessCenter,
                        label = "Ejercicios",
                        selected = false,
                        onClick = onNavigateToEjercicios
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DateSelector(
                    fecha = fechaSeleccionada,
                    onDiaAnterior = { viewModel.cambiarDia(-1) },
                    onDiaSiguiente = { viewModel.cambiarDia(1) },
                    onSelectorFechaClick = { mostrarDatePicker = true }
                )

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFFFEB3B))
                    }
                } else if (citasDelDia.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay alumnos para este día.",
                            color = Color.White
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(citasDelDia, key = { it.alumnoId }) { cita ->
                            CitaCalendarioCard(
                                cita = cita,
                                onClick = { onNavigateToPlanificacion(cita.alumnoId) }
                            )
                        }
                    }
                }
            }

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
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Día anterior",
                tint = Color(0xFFFFEB3B)
            )
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
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Icon(
                Icons.Default.CalendarMonth,
                contentDescription = "Seleccionar fecha",
                modifier = Modifier.size(20.dp),
                tint = Color(0xFFFFEB3B)
            )
        }

        IconButton(onClick = onDiaSiguiente) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Día siguiente",
                tint = Color(0xFFFFEB3B)
            )
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
            .clickable(onClick = onClick), // <-- 5. Se hace que toda la tarjeta sea clicable
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2C2C2C)
        ),
        shape = MaterialTheme.shapes.medium
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
                modifier = Modifier.weight(1f),
                color = Color.White
            )
            AssistChip(
                onClick = { /* No-op, la acción está en la tarjeta */ },
                label = { Text(cita.detalle) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (cita.tipo == TipoAlumno.PRESENCIAL) Color(0x332196F3) else Color(
                        0x3328A745
                    ),
                    labelColor = Color.White
                )
            )
        }
    }
}
