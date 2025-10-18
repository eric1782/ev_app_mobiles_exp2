package com.example.proyecto20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyecto20.data.MockData
import com.example.proyecto20.model.Usuario
import com.example.proyecto20.ui.navigation.AppRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisAlumnosScreen(
    navController: NavController,
    entrenadorId: String
) {
    val misAlumnos = MockData.todosLosUsuarios.filter { it.idEntrenadorAsignado == entrenadorId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Alumnos (${misAlumnos.size})") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(AppRoutes.ADD_ALUMNO_SCREEN) }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Alumno")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(misAlumnos) { alumno ->
                // Esta llamada ahora funcionará porque AlumnoCard existe en ComponentesComunes.kt
                AlumnoCard(
                    alumno = alumno,
                    onClick = {
                        val route = AppRoutes.ALUMNO_DETAIL_SCREEN.replace("{alumnoId}", alumno.id)
                        navController.navigate(route)
                    }
                )
            }
        }
    }
}
