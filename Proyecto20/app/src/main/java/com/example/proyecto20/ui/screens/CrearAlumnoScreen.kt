package com.example.proyecto20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyecto20.model.EjercicioRutina
import com.example.proyecto20.ui.theme.Proyecto20Theme
import java.text.DecimalFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearAlumnoScreen(
    onNavigateBack: () -> Unit
) {
    var nombreState by remember { mutableStateOf("") }
    var emailState by remember { mutableStateOf("") }
    var passwordState by remember { mutableStateOf("") }
    var pesoState by remember { mutableStateOf("") }
    var estaturaState by remember { mutableStateOf("") }

    val rutinaEnConstruccion = remember { mutableStateMapOf<String, MutableList<EjercicioRutina>>() }

    var showAddDiaDialog by remember { mutableStateOf(false) }
    var diaParaAnadirEjercicio by remember { mutableStateOf<String?>(null) }
    var recomposeTrigger by remember { mutableStateOf(false) }

    val pesoKg = pesoState.toDoubleOrNull() ?: 0.0
    val estaturaCm = estaturaState.toDoubleOrNull() ?: 0.0
    val (imc, categoriaIMC) = calcularIMC(pesoKg, estaturaCm)
    val colorIMC = obtenerColorIMC(categoriaIMC)
    val df = DecimalFormat("#.##")

    if (showAddDiaDialog) {
        AddDiaDialog( // Importado de Dialogs.kt
            onDismiss = { showAddDiaDialog = false },
            onSave = { nuevoDia: String ->
                rutinaEnConstruccion.putIfAbsent(nuevoDia, mutableListOf())
                showAddDiaDialog = false
            }
        )
    }

    diaParaAnadirEjercicio?.let { diaActual ->
        AddEjercicioDialog( // Importado de Dialogs.kt
            dia = diaActual,
            onDismiss = { diaParaAnadirEjercicio = null },
            onSave = { idEjercicio, peso, series, reps ->
                val nuevoEjercicio = EjercicioRutina(idEjercicio, series, reps, peso)
                rutinaEnConstruccion[diaActual]?.add(nuevoEjercicio)
                recomposeTrigger = !recomposeTrigger
                diaParaAnadirEjercicio = null
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Nuevo Alumno") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } }
            )
        }
    ) { paddingValues ->
        val diasDeRutina = remember(rutinaEnConstruccion.keys, recomposeTrigger) {
            rutinaEnConstruccion.keys.toList()
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- SECCIONES DE DATOS Y PERFIL (sin cambios) ---
            item {
                Text("Datos del Alumno", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
                OutlinedTextField(value = nombreState, onValueChange = { nombreState = it }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = emailState, onValueChange = { emailState = it }, label = { Text("Correo Electrónico") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), singleLine = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = passwordState, onValueChange = { passwordState = it }, label = { Text("Contraseña Temporal") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), visualTransformation = PasswordVisualTransformation(), singleLine = true)
            }
            item {
                Text("Perfil Físico", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(value = pesoState, onValueChange = { pesoState = it }, label = { Text("Peso (kg)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = estaturaState, onValueChange = { estaturaState = it }, label = { Text("Estatura (cm)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
                if (imc > 0) {
                    Spacer(Modifier.height(16.dp))
                    Text("IMC: ${df.format(imc)} - $categoriaIMC", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = colorIMC)
                }
            }

            // --- SECCIÓN DE RUTINA ---
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Configuración de Rutina", style = MaterialTheme.typography.titleLarge)
                    Button(onClick = { showAddDiaDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir Día")
                        Spacer(Modifier.width(8.dp))
                        Text("Añadir Día")
                    }
                }
            }

            // --- LISTA DE DÍAS ---
            items(diasDeRutina) { dia ->
                DiaEntrenamientoCard( // Importado de ComponentesComunes.kt
                    dia = dia,
                    ejercicios = rutinaEnConstruccion[dia] ?: emptyList(),
                    onAddClick = { diaParaAnadirEjercicio = dia },
                    onEditClick = { /* TODO: Implementar edición en esta pantalla si se desea */ }
                )
            }

            // --- BOTÓN FINAL ---
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        println("Guardando Alumno: Nombre=${nombreState}, Email=${emailState}")
                        println("Rutina: $rutinaEnConstruccion")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = nombreState.isNotBlank() && emailState.isNotBlank()
                ) {
                    Text("GUARDAR ALUMNO")
                }
            }
        }
    }
}

// --- FUNCIONES DE AYUDA (NO HAY COMPONENTES DE UI AQUÍ) ---

fun calcularIMC(pesoEnKg: Double, alturaEnCm: Double): Pair<Double, String> {
    if (alturaEnCm <= 0 || pesoEnKg <= 0) { return Pair(0.0, "Datos inválidos") }
    val alturaEnMetros = alturaEnCm / 100
    val imc = pesoEnKg / (alturaEnMetros * alturaEnMetros)
    val categoria = when {
        imc < 18.5 -> "Bajo peso"
        imc < 25 -> "Normal"
        imc < 30 -> "Sobrepeso"
        else -> "Obesidad"
    }
    return Pair(imc, categoria)
}

@Composable
fun obtenerColorIMC(categoria: String): Color {
    return when (categoria) {
        "Bajo peso" -> Color(0xFF3498DB)
        "Normal" -> Color(0xFF2ECC71)
        "Sobrepeso" -> Color(0xFFF1C40F)
        "Obesidad" -> Color(0xFFE74C3C)
        else -> MaterialTheme.colorScheme.onSurface
    }
}

@Preview(showBackground = true)
@Composable
fun CrearAlumnoScreenPreview() {
    Proyecto20Theme {
        CrearAlumnoScreen(onNavigateBack = {})
    }
}

