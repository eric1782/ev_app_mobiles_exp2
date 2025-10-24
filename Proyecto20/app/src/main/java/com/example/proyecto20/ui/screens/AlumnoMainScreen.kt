package com.example.proyecto20.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
// IMPORT CORREGIDO/AÑADIDO:
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto20.model.EjercicioRutina
import com.example.proyecto20.model.Usuario
import com.example.proyecto20.ui.navigation.AppRoutes
import com.example.proyecto20.ui.viewmodels.AuthViewModel
import com.example.proyecto20.ui.viewmodels.PlanificacionViewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlumnoMainScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    user: Usuario
) {
    val viewModel: PlanificacionViewModel = viewModel(factory = PlanificacionViewModel.Factory(user.id))
    val rutina by viewModel.rutina.collectAsState()
    val horaDelDia by viewModel.horaDelDiaSeleccionado.collectAsState()

    val diaDeHoy = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es", "ES")).uppercase()
    val entrenamientoDeHoy = rutina[diaDeHoy]

    // Nos aseguramos de que el día seleccionado en el ViewModel sea siempre el de hoy
    viewModel.seleccionarDia(diaDeHoy)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("¡Hola, ${user.nombre}!") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                actions = { TextButton(onClick = onLogout) { Text("SALIR", color = Color.White) } }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Hoy") },
                    label = { Text("Hoy") },
                    selected = true, // La pantalla principal siempre es "Hoy"
                    onClick = { /* No hace nada, ya estamos aquí */ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Rutina") },
                    label = { Text("Rutina") },
                    selected = false,
                    onClick = {
                        navController.navigate(
                            AppRoutes.VISUALIZACION_RUTINA_SCREEN.replace("{alumnoId}", user.id)
                        )
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Progreso") },
                    label = { Text("Progreso") },
                    selected = false,
                    onClick = {
                        navController.navigate(
                            AppRoutes.ESTADISTICAS_ALUMNO_SCREEN.replace("{alumnoId}", user.id)
                        )
                    }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        "Entrenamiento de Hoy:",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (!horaDelDia.isNullOrBlank()) {
                        Text(
                            "Tu cita de hoy es a las: $horaDelDia",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (entrenamientoDeHoy == null || entrenamientoDeHoy.ejercicios.isEmpty()) {
                    item {
                        Box(
                            Modifier
                                .fillParentMaxWidth()
                                .padding(vertical = 50.dp), contentAlignment = Alignment.Center
                        ) {
                            Text("Hoy tienes descanso. ¡A recuperar!")
                        }
                    }
                } else {
                    items(entrenamientoDeHoy.ejercicios) { ejercicio ->
                        EjercicioVistaItem(
                            ejercicio = ejercicio,
                            onClick = {
                                val route = "ejercicioDetailSoloLectura/${user.id}/${ejercicio.ejercicioId}" +
                                        "?series=${ejercicio.series}" +
                                        "&repeticiones=${ejercicio.repeticiones}" +
                                        "&peso=${ejercicio.peso?.toString() ?: ""}" +
                                        "&rir=${ejercicio.rir?.toString() ?: ""}"

                                navController.navigate(route)
                            }
                        )
                    }
                }
            }

            // El botón "Cambiar Contraseña" se mantiene, el de "Ver Rutina" ahora está en la barra inferior.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = { navController.navigate(AppRoutes.CAMBIAR_PASSWORD_SCREEN) }) {
                    Text("Cambiar Contraseña")
                }
            }
        }
    }
}

@Composable
fun EjercicioVistaItem(
    ejercicio: EjercicioRutina,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(ejercicio.nombre, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                val details = mutableListOf<String>()
                details.add("${ejercicio.series} series")
                details.add(ejercicio.repeticiones)
                ejercicio.peso?.let { details.add("${it}kg") }
                ejercicio.rir?.let { details.add("RIR $it") }

                Text(
                    text = details.joinToString("  •  "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                // AHORA ESTA LÍNEA ES VÁLIDA GRACIAS AL IMPORT
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ver detalle",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
