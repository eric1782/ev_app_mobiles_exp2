package com.example.proyecto20.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.proyecto20.model.TipoAlumno
import com.example.proyecto20.ui.viewmodels.CrearAlumnoState
import com.example.proyecto20.ui.viewmodels.CrearAlumnoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearAlumnoScreen(
    viewModel: CrearAlumnoViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = state) {
        when (val currentState = state) {
            is CrearAlumnoState.Success -> {
                Toast.makeText(context, "Alumno creado. Pass: ${currentState.passwordTemporal}", Toast.LENGTH_LONG).show()
                viewModel.clearState()
                onNavigateBack()
            }
            is CrearAlumnoState.Error -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_SHORT).show()
                viewModel.clearState()
            }
            else -> { /* No hacemos nada en Idle o Loading */ }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Nuevo Alumno") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        val isLoading = state is CrearAlumnoState.Loading
        Box(modifier = Modifier.fillMaxSize()) {
            CrearAlumnoForm(
                viewModel = viewModel,
                isEnabled = !isLoading,
                modifier = Modifier.padding(padding)
            )
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun CrearAlumnoForm(
    viewModel: CrearAlumnoViewModel,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    // Obtenemos los valores de los campos directamente del ViewModel
    val nombre by viewModel.nombre
    val email by viewModel.email
    val tipoAlumno by viewModel.tipoAlumno
    // --- ¡CORRECCIÓN! Usamos los campos de texto del ViewModel ---
    val pesoInput by viewModel.pesoInput
    val estaturaInput by viewModel.estaturaInput

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Datos del Alumno", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = nombre,
            onValueChange = { viewModel.nombre.value = it },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            enabled = isEnabled
        )
        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.email.value = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            enabled = isEnabled
        )

        Text("Datos Físicos (Opcional)", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // --- ¡CORRECCIÓN! El TextField se vincula a pesoInput ---
            OutlinedTextField(
                value = pesoInput,
                onValueChange = { viewModel.pesoInput.value = it },
                label = { Text("Peso (kg)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                enabled = isEnabled
            )
            // --- ¡CORRECCIÓN! El TextField se vincula a estaturaInput ---
            OutlinedTextField(
                value = estaturaInput,
                onValueChange = { viewModel.estaturaInput.value = it },
                label = { Text("Estatura (m)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                enabled = isEnabled
            )
        }

        Text("Modalidad", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { viewModel.onTipoAlumnoChange(TipoAlumno.PRESENCIAL) },
                modifier = Modifier.weight(1f),
                colors = if (tipoAlumno == TipoAlumno.PRESENCIAL) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.outlinedButtonColors(),
                enabled = isEnabled
            ) { Text("Presencial") }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                onClick = { viewModel.onTipoAlumnoChange(TipoAlumno.ONLINE) },
                modifier = Modifier.weight(1f),
                colors = if (tipoAlumno == TipoAlumno.ONLINE) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.outlinedButtonColors(),
                enabled = isEnabled
            ) { Text("Online") }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.guardarAlumno() },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            enabled = isEnabled
        ) {
            Text("Guardar Alumno")
        }
    }
}
