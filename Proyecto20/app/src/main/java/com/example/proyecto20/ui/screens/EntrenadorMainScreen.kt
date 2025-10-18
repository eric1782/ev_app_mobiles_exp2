package com.example.proyecto20.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyecto20.data.MockData
import com.example.proyecto20.ui.navigation.AppRoutes
import com.example.proyecto20.util.GestorDeCitas
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntrenadorMainScreen(
    navController: NavController,
    entrenadorId: String,
    onAlumnoClick: (String) -> Unit,
    onAddAlumnoClick: () -> Unit
) {
    val entrenador = MockData.todosLosUsuarios.find { it.id == entrenadorId }
    val misAlumnos = MockData.todosLosUsuarios.filter { it.idEntrenadorAsignado == entrenadorId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(entrenador?.nombre ?: "Entrenador") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Color de fondo
                    titleContentColor = MaterialTheme.colorScheme.onPrimary, // Color del título
                )
            )
        }
    ) { paddingValues ->
        // 1. Usamos una Column como contenedor principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Aplicamos el padding del Scaffold aquí
        ) {
            // 2. La LazyColumn ocupa todo el espacio disponible gracias a .weight(1f)
            LazyColumn(
                modifier = Modifier
                    .weight(1f), // <-- ESTA ES LA CLAVE
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        "Tus citas de hoy:",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

// --- Lógica para obtener las citas de hoy ---
                // Asumimos que GestorDeCitas.obtenerCitasParaDia ya existe.
                val citasDeHoy = GestorDeCitas.obtenerCitasParaDia(entrenadorId, LocalDate.now())
                // --- Fin de la lógica ---

                if (citasDeHoy.isEmpty()) {
                    item {
                        // Mensaje centrado cuando no hay citas
                        Box(
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .padding(vertical = 50.dp), // Un poco de espacio vertical
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Es tu día libre, ¡descansa!")
                        }
                    }
                } else {
                    // Usamos el CitaCard que definiremos abajo
                    items(citasDeHoy) { cita ->
                        CitaCard(
                            cita = cita,
                            // Al hacer clic, navega al perfil del alumno correspondiente
                            onVerRutinaClick = { onAlumnoClick(cita.idAlumno) }
                        )
                    }
                }

            }

            // 3. Contenedor fijo para los botones en la parte inferior, ahora en horizontal
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp), // Padding para que no estén pegados a los bordes
                horizontalArrangement = Arrangement.SpaceAround, // Distribuye los botones uniformemente
                verticalAlignment = Alignment.CenterVertically // Centra los botones verticalmente en la fila
            ) {
                Button(onClick = { navController.navigate(AppRoutes.MIS_ALUMNOS_SCREEN.replace("{entrenadorId}", entrenadorId)) }) {
                    Text("Alumnos") // Texto acortado para mejor ajuste
                }
                Button(
                    onClick = { navController.navigate(AppRoutes.LISTA_EJERCICIOS_SCREEN) },
                ) {
                    Text("Ejercicios") // Texto acortado
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    // --- CORRECCIÓN EN LA NAVEGACIÓN ---
                    // Ahora construimos la ruta con el ID del entrenador.
                    val route = AppRoutes.CALENDARIO_ENTRENADOR_SCREEN.replace("{entrenadorId}", entrenadorId)
                    navController.navigate(route)
                }) {
                    Text("Ver Calendario de Citas")
                Button(
                    onClick = { navController.navigate(AppRoutes.CALENDARIO_ENTRENADOR_SCREEN) },
                ) {
                    Text("Citas") // Texto acortado
                }
            }

        }
    }
}
