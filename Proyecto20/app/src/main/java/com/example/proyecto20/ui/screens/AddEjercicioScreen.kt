package com.example.proyecto20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save // <-- ¡IMPORTACIÓN AÑADIDA!
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEjercicioScreen(
    onNavigateBack: () -> Unit,
    onSave: (String, String, String, String?) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var musculo by remember { mutableStateOf("") }
    var urlVideo by remember { mutableStateOf("") }

    val isFormValid = nombre.isNotBlank() && descripcion.isNotBlank() && musculo.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir Nuevo Ejercicio") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("GUARDAR EJERCICIO") },
                icon = { Icon(Icons.Filled.Save, contentDescription = "Guardar") },
                onClick = {
                    if (isFormValid) {
                        onSave(nombre, descripcion, musculo, urlVideo.ifBlank { null })
                    }
                },
                // El color del FAB se ajusta automáticamente según si está habilitado o no
                // al cambiar la condición del onClick, no es necesario cambiarlo manualmente.
                // Esta es la forma más simple y recomendada.
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Rellena los detalles del nuevo ejercicio para el catálogo.", style = MaterialTheme.typography.bodyMedium)
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre del Ejercicio*") }, modifier = Modifier.fillMaxWidth(), singleLine = true, isError = nombre.isBlank() && nombre.isNotEmpty())
            OutlinedTextField(value = musculo, onValueChange = { musculo = it }, label = { Text("Músculo Principal*") }, modifier = Modifier.fillMaxWidth(), singleLine = true, isError = musculo.isBlank() && musculo.isNotEmpty())
            OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción / Cómo realizarlo*") }, modifier = Modifier
                .fillMaxWidth()
                .height(150.dp), isError = descripcion.isBlank() && descripcion.isNotEmpty())
            OutlinedTextField(value = urlVideo, onValueChange = { urlVideo = it }, label = { Text("URL del video (Opcional)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Text("* Campos obligatorios", style = MaterialTheme.typography.bodySmall)
        }
    }
}
