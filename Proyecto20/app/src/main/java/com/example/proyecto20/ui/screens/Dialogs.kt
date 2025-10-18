package com.example.proyecto20.ui.screens
import androidx.compose.material3.MenuAnchorType
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.proyecto20.data.MockData
import com.example.proyecto20.model.Ejercicio
import com.example.proyecto20.model.EjercicioRutina
import java.util.Locale

// --- DIÁLOGO PARA AÑADIR UN DÍA DE ENTRENAMIENTO ---
@Composable
fun AddDiaDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var nombreDiaState by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir Nuevo Día") },
        text = {
            OutlinedTextField(
                value = nombreDiaState,
                onValueChange = { nombreDiaState = it },
                label = { Text("Nombre del día (ej: Lunes)") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    // Guardamos el nombre del día en mayúsculas para consistencia
                    if (nombreDiaState.isNotBlank()) {
                        onSave(nombreDiaState.uppercase(Locale.getDefault()))
                    }
                },
                enabled = nombreDiaState.isNotBlank()
            ) { Text("Añadir") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

// --- DIÁLOGO PARA AÑADIR UN EJERCICIO A UN DÍA ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEjercicioDialog(
    dia: String,
    onDismiss: () -> Unit,
    onSave: (String, Double, Int, String) -> Unit
) {
    var pesoState by remember { mutableStateOf("") }
    var seriesState by remember { mutableStateOf("") }
    var repsState by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // --- CORRECCIÓN 1: Se usa 'todosLosEjercicios' ---
    val catalogo = MockData.todosLosEjercicios
    var selectedEjercicio by remember { mutableStateOf<Ejercicio?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir Ejercicio al $dia") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedEjercicio?.nombre ?: "Seleccionar ejercicio",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },

                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        catalogo.forEach { ejercicio ->
                            DropdownMenuItem(
                                text = { Text(ejercicio.nombre) },
                                onClick = {
                                    selectedEjercicio = ejercicio
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(value = pesoState, onValueChange = { pesoState = it }, label = { Text("Peso Recomendado (kg)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = seriesState, onValueChange = { seriesState = it }, label = { Text("Series") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = repsState, onValueChange = { repsState = it }, label = { Text("Repeticiones (ej: 10-12)") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val id = selectedEjercicio?.id
                    val peso = pesoState.toDoubleOrNull() ?: 0.0
                    val series = seriesState.toIntOrNull() ?: 0
                    if (id != null) { onSave(id, peso, series, repsState) }
                },
                enabled = selectedEjercicio != null
            ) { Text("Añadir") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

// --- DIÁLOGO PARA EDITAR UN EJERCICIO DE LA RUTINA ---
@Composable
fun EditEjercicioDialog(
    ejercicioRutina: EjercicioRutina,
    onDismiss: () -> Unit,
    onSave: (Double, Int, String) -> Unit
) {
    var pesoState by remember { mutableStateOf(ejercicioRutina.pesoRecomendadoKg.toString()) }
    var seriesState by remember { mutableStateOf(ejercicioRutina.series.toString()) }
    var repsState by remember { mutableStateOf(ejercicioRutina.repeticiones) }

    // --- CORRECCIÓN 2: Se usa 'todosLosEjercicios' ---
    val ejercicioInfo = MockData.todosLosEjercicios.find { it.id == ejercicioRutina.idEjercicio }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar ${ejercicioInfo?.nombre ?: "Ejercicio"}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = pesoState, onValueChange = { pesoState = it }, label = { Text("Peso Recomendado (kg)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = seriesState, onValueChange = { seriesState = it }, label = { Text("Series") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = repsState, onValueChange = { repsState = it }, label = { Text("Repeticiones (ej: 10-12)") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val peso = pesoState.toDoubleOrNull() ?: 0.0
                    val series = seriesState.toIntOrNull() ?: 0
                    onSave(peso, series, repsState)
                }
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
