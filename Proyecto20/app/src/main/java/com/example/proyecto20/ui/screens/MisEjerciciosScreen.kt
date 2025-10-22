// Ruta: app/src/main/java/com/example/proyecto20/ui/screens/MisEjerciciosScreen.kt

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyecto20.model.Ejercicio
import com.example.proyecto20.ui.navigation.AppRoutes
import com.example.proyecto20.ui.viewmodels.EjerciciosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisEjerciciosScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    ejerciciosViewModel: EjerciciosViewModel
) {
    // Recolectamos el StateFlow del ViewModel correctamente
    val ejercicios by ejerciciosViewModel.ejercicios.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo de Ejercicios") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(AppRoutes.ADD_EJERCICIO_SCREEN) }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Ejercicio")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(ejercicios) { ejercicio ->
                EjercicioItem(
                    ejercicio = ejercicio,
                    onClick = {
                        // Aseguramos que el id no sea nulo antes de navegar
                        navController.navigate(AppRoutes.EJERCICIO_DETAIL_SCREEN.replace("{ejercicioId}", ejercicio.id))
                    }
                )
            }
        }
    }
}

@Composable
fun EjercicioItem(ejercicio: Ejercicio, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(ejercicio.nombre, fontWeight = FontWeight.Bold)
            Text(ejercicio.musculoPrincipal, style = MaterialTheme.typography.bodySmall)
        }
    }
}
