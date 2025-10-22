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
import androidx.compose.ui.unit.dp
import com.example.proyecto20.model.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisAlumnosScreen(
    alumnos: List<Usuario>,
    textoBusqueda: String,
    onTextoBusquedaChange: (String) -> Unit,
    onAddAlumnoClick: () -> Unit,
    onAlumnoClick: (String) -> Unit, // Recibe el ID del alumno (String)
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Alumnos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddAlumnoClick) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Alumno")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = onTextoBusquedaChange,
                label = { Text("Buscar alumno...") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (alumnos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tienes alumnos todavía.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(alumnos) { alumno ->
                        AlumnoItem(
                            alumno = alumno,
                            // --- ¡AQUÍ ESTÁ LA CLAVE! ---
                            // Al hacer clic, se llama a onAlumnoClick pasando el ID del alumno.
                            onItemClick = { onAlumnoClick(alumno.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AlumnoItem(
    alumno: Usuario,
    onItemClick: () -> Unit // Es una función que no toma parámetros aquí
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            // El modificador clickable hace que toda la tarjeta sea interactiva.
            .clickable(onClick = onItemClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = alumno.nombre, style = MaterialTheme.typography.titleMedium)
                Text(text = alumno.email, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
