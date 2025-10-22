// Ruta: app/src/main/java/com/example/proyecto20/ui/screens/EntrenadorMainScreen.kt

package com.example.proyecto20.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyecto20.model.Cita // Asegúrate de tener este import
import com.example.proyecto20.ui.navigation.AppRoutes
import com.example.proyecto20.ui.viewmodels.AuthViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntrenadorMainScreen(
    navController: NavController,
    authViewModel: AuthViewModel, // Recibimos el ViewModel de autenticación
    onLogout: () -> Unit // Recibimos la función para cerrar sesión
) {
    // 1. OBTENEMOS LOS DATOS REALES DESDE FIREBASE
    val user by authViewModel.currentUser.collectAsState(initial = null)
    val entrenadorId = user?.id ?: "" // Obtenemos el ID del entrenador logueado

    // (Aquí iría la lógica para obtener las citas desde un ViewModel,
    // por ahora podemos simularlas o dejarlas vacías)
    val citasDeHoy = emptyList<Cita>() // Placeholder: Lista vacía por ahora

    Scaffold(
        topBar = {
            TopAppBar(
                // Mostramos el nombre del usuario real
                title = { Text(user?.nombre ?: "Entrenador") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                // Añadimos un botón de Logout en la TopAppBar
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("SALIR", color = MaterialTheme.colorScheme.onPrimary)
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
            LazyColumn(
                modifier = Modifier.weight(1f),
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

                if (citasDeHoy.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .padding(vertical = 50.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No tienes citas programadas para hoy.")
                        }
                    }
                } else {
                    items(citasDeHoy) { cita ->
                        // Este CitaCard funcionará cuando implementemos las citas
                        /*
                        CitaCard(
                            cita = cita,
                            onVerRutinaClick = {
                                navController.navigate(
                                    AppRoutes.ALUMNO_DETAIL_SCREEN.replace("{alumnoId}", cita.idAlumno)
                                )
                            }
                        )
                        */
                    }
                }
            }

            // 3. BARRA DE NAVEGACIÓN INFERIOR (como la tenías antes)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { navController.navigate(AppRoutes.MIS_ALUMNOS_SCREEN) }) {
                    Text("Alumnos")
                }
                Button(onClick = { navController.navigate(AppRoutes.CALENDARIO_ENTRENADOR_SCREEN) }) {
                    Text("Citas")
                }
                Button(onClick = { navController.navigate(AppRoutes.LISTA_EJERCICIOS_SCREEN) }) {
                    Text("Ejercicios")
                }
            }
        }
    }
}
