package com.example.proyecto20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController // <-- ¡IMPORTANTE! Añadir este import
import com.example.proyecto20.model.Ejercicio
import com.example.proyecto20.ui.navigation.AppRoutes
import com.example.proyecto20.ui.viewmodels.EjercicioDetailViewModel
import com.example.proyecto20.ui.viewmodels.EjercicioDetailViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EjercicioDetailScreen(
    ejercicioId: String,
    navController: NavHostController, // <-- ¡CORRECCIÓN! Aceptamos el NavController
    onNavigateBack: () -> Unit
) {
    val viewModel: EjercicioDetailViewModel = viewModel(
        factory = EjercicioDetailViewModelFactory(ejercicioId)
    )

    val ejercicio by viewModel.ejercicio.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(ejercicio?.nombre ?: "Detalle de Ejercicio") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("EDITAR") },
                icon = { Icon(Icons.Default.Edit, contentDescription = "Editar") },
                onClick = {
                    // ¡CORRECCIÓN! Usamos el navController para navegar a la pantalla de edición
                    ejercicio?.let {
                        navController.navigate(AppRoutes.EDIT_EJERCICIO_SCREEN.replace("{ejercicioId}", it.id))
                    }
                }
            )
        }
    ) { padding ->
        if (ejercicio == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
                Text("Cargando datos del ejercicio...", modifier = Modifier.padding(top = 60.dp))
            }
        } else {
            DetallesDelEjercicio(ejercicio = ejercicio!!, padding = padding)
        }
    }
}

@Composable
private fun DetallesDelEjercicio(ejercicio: Ejercicio, padding: PaddingValues) {
    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (ejercicio.urlVideo.isNotBlank()) {
            Text("URL del Video: ${ejercicio.urlVideo}", style = MaterialTheme.typography.bodyMedium)
        }
        Text(ejercicio.nombre, style = MaterialTheme.typography.headlineMedium)
        HorizontalDivider()
        Text("Músculo Principal", style = MaterialTheme.typography.titleMedium)
        Text(ejercicio.musculoPrincipal, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Descripción / Cómo realizarlo", style = MaterialTheme.typography.titleMedium)
        Text(ejercicio.descripcion, style = MaterialTheme.typography.bodyLarge)
    }
}
