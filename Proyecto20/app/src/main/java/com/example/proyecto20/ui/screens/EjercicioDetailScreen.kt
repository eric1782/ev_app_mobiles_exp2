// Ruta: app/src/main/java/com/example/proyecto20/ui/screens/EjercicioDetailScreen.kt

package com.example.proyecto20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.proyecto20.model.Ejercicio

// --- ViewModel futuro ---
// class EjercicioDetailViewModel(ejercicioId: String, repo: FirebaseRepository) : ViewModel() { ... }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EjercicioDetailScreen(
    ejercicioId: String,
    alumnoId: String, // Recibimos el ID, puede estar vacío si no aplica
    onNavigateBack: () -> Unit
) {
    var ejercicio by remember { mutableStateOf<Ejercicio?>(null) }

    LaunchedEffect(ejercicioId) {
        // Aquí iría la llamada al repositorio para cargar el ejercicio
        // ejercicio = FirebaseRepository.getEjercicioById(ejercicioId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(ejercicio?.nombre ?: "Detalle de Ejercicio") },
                navigationIcon = {
                    // ... icono de volver
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (ejercicio == null) {
                Text("Cargando datos del ejercicio...")
            } else {
                // Aquí mostrarías los detalles del ejercicio, video, etc.
                Text("Nombre: ${ejercicio!!.nombre}")
                Text("Descripción: ${ejercicio!!.descripcion}")
            }
        }
    }
}
