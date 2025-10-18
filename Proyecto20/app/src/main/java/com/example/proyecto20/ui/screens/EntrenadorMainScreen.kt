package com.example.proyecto20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyecto20.data.MockData
import com.example.proyecto20.ui.navigation.AppRoutes

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
                title = { Text(entrenador?.nombre ?: "Entrenador") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Tus Alumnos",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { navController.navigate(AppRoutes.MIS_ALUMNOS_SCREEN.replace("{entrenadorId}", entrenadorId)) }) {
                    Text("Ver todos los alumnos")
                }
            }

            // Muestra los primeros alumnos a modo de resumen
            if (misAlumnos.isEmpty()) {
                item {
                    Text("Aún no tienes alumnos asignados.")
                }
            } else {
                items(misAlumnos.take(3)) { alumno ->
                    // Esta llamada ahora funcionará
                    AlumnoCard(
                        alumno = alumno,
                        onClick = { onAlumnoClick(alumno.id) }
                    )
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            }

            item {
                Text(
                    "Herramientas",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { navController.navigate(AppRoutes.LISTA_EJERCICIOS_SCREEN) }) {
                    Text("Ver Catálogo de Ejercicios")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { navController.navigate(AppRoutes.CALENDARIO_ENTRENADOR_SCREEN) }) {
                    Text("Ver Calendario de Citas")
                }
            }
        }
    }
}
