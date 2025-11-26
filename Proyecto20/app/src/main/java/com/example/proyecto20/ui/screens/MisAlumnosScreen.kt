package com.example.proyecto20.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // 1. IMPORT NECESARIO
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyecto20.model.TipoAlumno
import com.example.proyecto20.model.Usuario
import com.example.proyecto20.ui.viewmodels.AlumnosViewModel
import com.example.proyecto20.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisAlumnosScreen(
    viewModel: AlumnosViewModel,
    onNavigateToCrearAlumno: () -> Unit,
    onNavigateToPlanificacion: (String) -> Unit,
    onNavigateBack: () -> Unit, // 2. SE AÑADE EL NUEVO PARÁMETRO
    onNavigateToHome: () -> Unit,
    onNavigateToCalendario: () -> Unit,
    onNavigateToEjercicios: () -> Unit
) {
    val alumnos by viewModel.alumnos.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val backgroundPainter = painterResource(id = R.drawable.estudiantes_background)
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
                            text = "Mis Alumnos",
                            color = Color(0xFFFFEB3B),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onNavigateToCrearAlumno,
                    containerColor = Color(0xFFFFEB3B),
                    contentColor = Color.Black
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Alumno")
                }
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
                        selected = true,
                        onClick = { }
                    )
                    ModernNavigationBarItem(
                        icon = Icons.Default.CalendarMonth,
                        label = "Calendario",
                        selected = false,
                        onClick = onNavigateToCalendario
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
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFFFEB3B))
                    }
                }
                alumnos.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Aún no tienes alumnos. Pulsa (+) para añadir uno.",
                            color = Color.White
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 20.dp, vertical = 16.dp), // 4. SE APLICA EL PADDING A TODOS LOS ELEMENTOS
                        contentPadding = PaddingValues(vertical = 8.dp),
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C))
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
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                // Chip para indicar el tipo de alumno
                AssistChip(
                    onClick = { /* No hace nada, es solo informativo */ },
                    label = { Text(alumno.tipo.name) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (alumno.tipo == TipoAlumno.PRESENCIAL) Color(0x332196F3) else Color(
                            0x3328A745
                        ),
                        labelColor = Color.White
                    )
                )
            }

            // Correo del alumno
            Text(
                text = alumno.email,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFBBBBBB)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Días de entrenamiento
            Text(
                "Días de Entrenamiento:",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            if (alumno.rutina.isEmpty()) {
                Text(
                    "Sin rutina asignada",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFBBBBBB)
                )
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
                color = if (tieneEntrenamiento) Color(0xFFFFEB3B) else Color.Gray.copy(alpha = 0.5f)
            )
        }
    }
}
