package com.example.proyecto20.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // <-- 1. Import necesario para la lista
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel // <-- 2. Import necesario para el ViewModel
import androidx.navigation.NavController
import com.example.proyecto20.model.Usuario
import com.example.proyecto20.ui.navigation.AppRoutes
import com.example.proyecto20.ui.viewmodels.AuthState
import com.example.proyecto20.ui.viewmodels.AuthViewModel
import com.example.proyecto20.ui.viewmodels.CalendarioViewModel // <-- 3. Import del ViewModel que usaremos

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntrenadorMainScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val authState by authViewModel.authState.collectAsState()
    var user: Usuario? = null

    if (authState is AuthState.Authenticated) {
        user = (authState as AuthState.Authenticated).user
    }

    // --- ¡¡INICIO DE LA MODIFICACIÓN!! ---

    // 4. Creamos una instancia del CalendarioViewModel.
    //    Este ViewModel necesita el ID del entrenador para funcionar.
    //    Si el user no está listo, pasamos un string vacío para evitar un crash.
    val calendarioViewModel: CalendarioViewModel = viewModel(
        factory = CalendarioViewModel.Factory(user?.id ?: "")
    )

    // 5. Obtenemos la lista de citas para el día seleccionado (que por defecto es HOY).
    val citasDeHoy by calendarioViewModel.citasDelDia.collectAsState(initial = emptyList())
    val isLoading by calendarioViewModel.isLoading.collectAsState()

    // --- ¡¡FIN DE LA MODIFICACIÓN!! ---

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(user?.nombre ?: "Entrenador") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("SALIR", color = Color.White)
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

                // --- ¡¡LÓGICA DE LA LISTA ACTUALIZADA!! ---
                when {
                    isLoading -> {
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxWidth().padding(vertical = 50.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    citasDeHoy.isEmpty() -> {
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxWidth().padding(vertical = 50.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No tienes citas programadas para hoy.")
                            }
                        }
                    }
                    else -> {
                        // 6. Mostramos la lista de citas de hoy usando el mismo CitaCalendarioCard
                        items(citasDeHoy) { cita ->
                            // Hacemos que la tarjeta sea clicable para ir a la planificación
                            CitaCalendarioCard(cita = cita, onClick = {
                                navController.navigate(
                                    AppRoutes.PLANIFICACION_RUTINA_SCREEN.replace("{alumnoId}", cita.alumnoId)
                                )
                            })
                        }
                    }
                }
            }

            // Barra de navegación inferior
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
                Button(
                    onClick = { navController.navigate(AppRoutes.CALENDARIO_SCREEN) },
                    enabled = true
                ) {
                    Text("Calendario")
                }
                Button(onClick = { navController.navigate(AppRoutes.LISTA_EJERCICIOS_SCREEN) }) {
                    Text("Ejercicios")
                }
            }
        }
    }
}
