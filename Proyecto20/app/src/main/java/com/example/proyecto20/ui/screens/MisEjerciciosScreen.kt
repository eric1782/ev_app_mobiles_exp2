package com.example.proyecto20.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyecto20.data.MockData
import com.example.proyecto20.model.Ejercicio
import com.example.proyecto20.ui.navigation.AppRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisEjerciciosScreen(
    navController: NavController,
    onNavigateBack: () -> Unit // Parámetro añadido para volver atrás
) {
    val todosLosEjercicios = MockData.todosLosEjercicios

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo de Ejercicios") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        // --- AÑADIMOS EL BOTÓN FLOTANTE ---
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Navega a la pantalla para añadir un nuevo ejercicio
                    navController.navigate(AppRoutes.ADD_EJERCICIO_SCREEN)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Ejercicio")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(todosLosEjercicios) { ejercicio ->
                EjercicioCard(
                    ejercicio = ejercicio,
                    onClick = {
                        val route = AppRoutes.EJERCICIO_DETAIL_SCREEN.replace("{ejercicioId}", ejercicio.id)
                        navController.navigate(route)
                    }
                )
            }
        }
    }
}

@Composable
fun EjercicioCard(ejercicio: Ejercicio, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = ejercicio.nombre,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Músculo principal: ${ejercicio.musculoPrincipal}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
