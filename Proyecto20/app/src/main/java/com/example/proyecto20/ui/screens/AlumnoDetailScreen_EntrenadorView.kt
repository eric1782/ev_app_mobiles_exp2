// Ruta: app/src/main/java/com/example/proyecto20/ui/screens/AlumnoDetailScreen_EntrenadorView.kt

package com.example.proyecto20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController // <-- IMPORTACIÓN NECESARIA
import com.example.proyecto20.ui.navigation.AppRoutes // <-- IMPORTACIÓN NECESARIA
import com.example.proyecto20.ui.viewmodels.AlumnoDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlumnoDetailScreen_EntrenadorView(
    alumnoId: String,
    viewModel: AlumnoDetailViewModel,
    navController: NavController, // <-- PARÁMETRO AÑADIDO Y NECESARIO
    onNavigateBack: () -> Unit
) {
    // Le decimos al ViewModel que cargue los datos cuando la pantalla se muestra por primera vez
    LaunchedEffect(key1 = alumnoId) {
        viewModel.cargarDatosAlumno(alumnoId)
    }

    val alumno by viewModel.alumno.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(alumno?.nombre ?: "Detalle del Alumno") },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally // Centrado mientras carga
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (alumno == null) {
                Text("No se pudieron cargar los datos del alumno.")
            } else {
                // Una vez cargado, usamos una Columna alineada al inicio
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Nombre: ${alumno!!.nombre}", style = MaterialTheme.typography.titleLarge)
                    Text("Email: ${alumno!!.email}", style = MaterialTheme.typography.bodyLarge)
                    Text("Peso: ${alumno!!.peso ?: "N/A"} kg", style = MaterialTheme.typography.bodyLarge)
                    Text("Estatura: ${alumno!!.estatura ?: "N/A"} m", style = MaterialTheme.typography.bodyLarge)
                    Text("Tipo: ${alumno!!.tipo}", style = MaterialTheme.typography.bodyLarge)

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- ¡EL BOTÓN CLAVE QUE FALTABA! ---
                    Button(
                        onClick = {
                            // Usamos el NavController para navegar a la pantalla de planificación
                            navController.navigate(
                                AppRoutes.PLANIFICACION_RUTINA_SCREEN.replace("{alumnoId}", alumnoId)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Gestionar Rutina")
                    }
                }
            }
        }
    }
}
