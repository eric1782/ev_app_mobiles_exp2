package com.example.proyecto20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyecto20.model.Ejercicio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEjercicioScreen(
    ejercicioInicial: Ejercicio? = null, // Parámetro opcional para la edición
    onNavigateBack: () -> Unit,
    onSave: (String, String, String, String?) -> Unit
) {
    // Usamos el ejercicioInicial para establecer los valores por defecto
    var nombre by remember { mutableStateOf(ejercicioInicial?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(ejercicioInicial?.descripcion ?: "") }
    var musculo by remember { mutableStateOf(ejercicioInicial?.musculoPrincipal ?: "") }
    var urlVideo by remember { mutableStateOf(ejercicioInicial?.urlVideo ?: "") }

    val isFormValid = nombre.isNotBlank() && descripcion.isNotBlank() && musculo.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                // Cambiamos el título si estamos editando o creando
                title = { Text(if (ejercicioInicial != null) "Editar Ejercicio" else "Añadir Ejercicio") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("GUARDAR") },
                icon = { Icon(Icons.Filled.Save, contentDescription = "Guardar") },
                onClick = {
                    if (isFormValid) {
                        onSave(nombre, descripcion, musculo, urlVideo.ifBlank { null })
                    }
                }
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
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre del Ejercicio*") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = musculo, onValueChange = { musculo = it }, label = { Text("Músculo Principal*") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción / Cómo realizarlo*") }, modifier = Modifier.fillMaxWidth().height(150.dp))
            OutlinedTextField(value = urlVideo, onValueChange = { urlVideo = it }, label = { Text("URL del video (Opcional)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        }
    }
}
