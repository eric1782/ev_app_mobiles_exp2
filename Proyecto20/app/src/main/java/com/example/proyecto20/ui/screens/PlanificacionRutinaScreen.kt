package com.example.proyecto20.ui.screens

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto20.model.DiaEntrenamiento
import com.example.proyecto20.model.Ejercicio
import com.example.proyecto20.model.EjercicioRutina
import com.example.proyecto20.model.TipoAlumno
import com.example.proyecto20.ui.viewmodels.PlanificacionViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PlanificacionRutinaScreen(
    viewModel: PlanificacionViewModel,
    onNavigateBack: () -> Unit
) {
    val alumno by viewModel.alumno.collectAsState()
    val rutina by viewModel.rutina.collectAsState()
    val diaSeleccionado by viewModel.diaSeleccionado.collectAsState()
    val catalogoEjercicios by viewModel.catalogoEjercicios.collectAsState()
    val horaDelDia by viewModel.horaDelDiaSeleccionado.collectAsState()

    // --- ¡CAMBIOS EN LA LÓGICA DE LA INTERFAZ! ---
    // Usamos los estados del ViewModel para controlar el diálogo de hito.
    val showHitoDialog by viewModel.showDialog

    val context = LocalContext.current
    val diasSemana = listOf("LUNES", "MARTES", "MIÉRCOLES", "JUEVES", "VIERNES", "SÁBADO", "DOMINGO")

    var mostrarDialogoAddEjercicio by remember { mutableStateOf(false) }
    var ejercicioParaEditar by remember { mutableStateOf<EjercicioRutina?>(null) }
    var mostrarDialogoHora by remember { mutableStateOf(false) }


    DarkMatterBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = alumno?.nombre ?: "Planificando Rutina",
                                color = DarkMatterPalette.PrimaryText
                            )
                            if (alumno?.tipo == TipoAlumno.PRESENCIAL && !horaDelDia.isNullOrBlank()) {
                                Text(
                                    text = "Horario: $horaDelDia",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DarkMatterPalette.Highlight
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                "Volver",
                                tint = DarkMatterPalette.PrimaryText
                            )
                        }
                    },
                    colors = DarkMatterTopAppBarColors()
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFF1E1E1E),
                    contentColor = Color.White
                ) {
                    if (alumno?.tipo == TipoAlumno.PRESENCIAL) {
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    Icons.Default.Schedule,
                                    contentDescription = "Horario",
                                    tint = Color(0xFF2196F3)
                                )
                            },
                            label = {
                                Text(
                                    text = if (horaDelDia.isNullOrBlank()) "Horario" else "Cambiar",
                                    color = Color(0xFF2196F3),
                                    fontSize = 12.sp
                                )
                            },
                            selected = false,
                            onClick = { mostrarDialogoHora = true }
                        )
                    }
                    
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Añadir",
                                tint = Color(0xFF2196F3)
                            )
                        },
                        label = {
                            Text(
                                text = "Añadir",
                                color = Color(0xFF2196F3),
                                fontSize = 12.sp
                            )
                        },
                        selected = false,
                        onClick = { mostrarDialogoAddEjercicio = true }
                    )
                    
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Save,
                                contentDescription = "Guardar",
                                tint = DarkMatterPalette.Highlight
                            )
                        },
                        label = {
                            Text(
                                text = "Guardar",
                                color = DarkMatterPalette.Highlight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        selected = false,
                        onClick = {
                            viewModel.guardarRutinaCompleta { success ->
                                val message = if (success) "Rutina guardada con éxito" else "Error al guardar la rutina"
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (success) onNavigateBack()
                            }
                        }
                    )
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
            PrimaryTabRow(
                selectedTabIndex = diasSemana.indexOf(diaSeleccionado),
                containerColor = Color.Transparent,
                contentColor = DarkMatterPalette.PrimaryText
            ) {
                diasSemana.forEach { dia ->
                    val isSelected = dia == diaSeleccionado
                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.seleccionarDia(dia) },
                        text = { 
                            Text(
                                dia.take(1),
                                color = if (isSelected) DarkMatterPalette.Highlight else DarkMatterPalette.PrimaryText
                            )
                        }
                    )
                }
            }

            // --- ¡CAMBIO IMPORTANTE! ---
            // Se pasa el control del hito al ContenidoDia.
            ContenidoDia(
                dia = rutina[diaSeleccionado],
                onDeleteEjercicio = { ejercicioId ->
                    viewModel.eliminarEjercicioDeRutina(ejercicioId, diaSeleccionado)
                },
                onEditEjercicio = { ejercicio ->
                    ejercicioParaEditar = ejercicio
                },
                onRegistrarHito = {
                    // La vista solo notifica al ViewModel que se quiere registrar un hito para el día actual.
                    viewModel.onRegistrarHitoDiaClicked(diaSeleccionado)
                }
            )
        }

            if (mostrarDialogoAddEjercicio) {
            DialogoSeleccionarEjercicio(
                ejercicios = catalogoEjercicios,
                onDismiss = { mostrarDialogoAddEjercicio = false },
                onEjercicioSelected = { ejercicio ->
                    viewModel.agregarEjercicioARutina(ejercicio, diaSeleccionado)
                    mostrarDialogoAddEjercicio = false
                }
            )
        }

        ejercicioParaEditar?.let { ejercicio ->
            DialogoEditarEjercicioRutina(
                ejercicio = ejercicio,
                onDismiss = { ejercicioParaEditar = null },
                onSave = { ejercicioActualizado ->
                    viewModel.actualizarDetallesEjercicio(ejercicioActualizado, diaSeleccionado)
                    ejercicioParaEditar = null
                }
            )
        }

            if (mostrarDialogoHora) {
            DialogoAsignarHora(
                dia = diaSeleccionado,
                valorInicial = horaDelDia ?: "",
                onDismiss = { mostrarDialogoHora = false },
                onSave = { horaTexto ->
                    viewModel.asignarHoraPresencial(diaSeleccionado, horaTexto) { success ->
                        val message = if (success) "Horario guardado" else "Error al guardar"
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        mostrarDialogoHora = false
                    }
                }
            )
        }

        // --- ¡CAMBIO IMPORTANTE! ---
        // El diálogo ahora es controlado por el estado del ViewModel.
            if (showHitoDialog) {
            RegistrarHitoDialog(
                onDismiss = { viewModel.showDialog.value = false },
                onConfirm = { comentario ->
                    viewModel.registrarHitoDeProgresoPorDia(comentario) { success ->
                        val message = if (success) "Hito de progreso registrado." else "Error al registrar el hito."
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
        }
    }
}


@Composable
fun ContenidoDia(
    dia: DiaEntrenamiento?,
    onDeleteEjercicio: (String) -> Unit,
    onEditEjercicio: (EjercicioRutina) -> Unit,
    // --- ¡CAMBIO! --- Se añade un nuevo parámetro para el evento de registrar hito.
    onRegistrarHito: () -> Unit
) {
    if (dia == null || dia.ejercicios.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Sin entrenamiento asignado para este día.\nPulsa (+) para añadir ejercicios.",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = DarkMatterPalette.PrimaryText
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- ¡CAMBIO IMPORTANTE! ---
            // Se añade una cabecera antes de la lista de ejercicios con el nuevo botón.
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Ejercicios de hoy",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DarkMatterPalette.PrimaryText
                    )
                    Button(
                        onClick = onRegistrarHito,
                        colors = DarkMatterButtonColors()
                    ) {
                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("Registrar Hito")
                    }
                }
            }

            items(dia.ejercicios, key = { it.ejercicioId }) { ejercicioRutina ->
                EjercicioRutinaCard(
                    ejercicio = ejercicioRutina,
                    onDelete = { onDeleteEjercicio(ejercicioRutina.ejercicioId) },
                    onClick = { onEditEjercicio(ejercicioRutina) }
                )
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// --- ¡CAMBIO! --- Se crea un Composable reutilizable para el diálogo de hito.
@Composable
fun RegistrarHitoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var comentario by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Hito del Día") },
        text = {
            OutlinedTextField(
                value = comentario,
                onValueChange = { comentario = it },
                label = { Text("Añadir comentario (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(comentario) }
            ) {
                Text("Guardar Hito")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// El resto de tus composables (EjercicioRutinaCard, DialogoSeleccionarEjercicio, etc.)
// pueden permanecer exactamente como los tienes, no necesitan cambios.
// Pego los que ya tenías para que el archivo quede completo.

@Composable
fun EjercicioRutinaCard(
    ejercicio: EjercicioRutina,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    ejercicio.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF2196F3)
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                Text("Series: ${ejercicio.series}", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF2196F3))
                Text("Reps: ${ejercicio.repeticiones}", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF2196F3))
                ejercicio.peso?.let { Text("Peso: ${it}kg", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF2196F3)) }
                ejercicio.rir?.let { Text("RIR: $it", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF2196F3)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoSeleccionarEjercicio(
    ejercicios: List<Ejercicio>,
    onDismiss: () -> Unit,
    onEjercicioSelected: (Ejercicio) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir Ejercicio") },
        text = {
            LazyColumn {
                items(ejercicios) { ejercicio ->
                    ListItem(
                        headlineContent = { Text(ejercicio.nombre) },
                        supportingContent = { Text(ejercicio.musculoPrincipal) },
                        modifier = Modifier.clickable { onEjercicioSelected(ejercicio) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("CANCELAR") }
        }
    )
}

@Composable
fun DialogoEditarEjercicioRutina(
    ejercicio: EjercicioRutina,
    onDismiss: () -> Unit,
    onSave: (EjercicioRutina) -> Unit
) {
    var series by remember { mutableStateOf(ejercicio.series.toString()) }
    var repeticiones by remember { mutableStateOf(ejercicio.repeticiones) }
    var peso by remember { mutableStateOf(ejercicio.peso?.toString() ?: "") }
    var rir by remember { mutableStateOf(ejercicio.rir?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar: ${ejercicio.nombre}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = series,
                    onValueChange = { series = it.filter { c -> c.isDigit() } },
                    label = { Text("Series") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = repeticiones,
                    onValueChange = { repeticiones = it },
                    label = { Text("Repeticiones (ej. 10-12)") }
                )
                OutlinedTextField(
                    value = peso,
                    onValueChange = { peso = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Peso (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = rir,
                    onValueChange = { rir = it.filter { c -> c.isDigit() } },
                    label = { Text("RIR (Repeticiones en Reserva)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val ejercicioActualizado = ejercicio.copy(
                    series = series.toIntOrNull() ?: ejercicio.series,
                    repeticiones = repeticiones.ifBlank { ejercicio.repeticiones },
                    peso = peso.toDoubleOrNull(),
                    rir = rir.toIntOrNull()
                )
                onSave(ejercicioActualizado)
            }) {
                Text("GUARDAR")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCELAR")
            }
        }
    )
}

@Composable
fun DialogoAsignarHora(
    dia: String,
    valorInicial: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val partes = valorInicial.split(" - ").map { it.trim() }
    val horaInicioInicial = partes.getOrElse(0) { "" }
    val horaFinInicial = partes.getOrElse(1) { "" }

    var horaInicioTexto by remember { mutableStateOf(horaInicioInicial) }
    var horaFinTexto by remember { mutableStateOf(horaFinInicial) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Asignar Hora para $dia") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = horaInicioTexto,
                    onValueChange = { horaInicioTexto = it },
                    label = { Text("Hora Inicio") },
                    placeholder = { Text("Ej. 10:00") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = horaFinTexto,
                    onValueChange = { horaFinTexto = it },
                    label = { Text("Hora Fin") },
                    placeholder = { Text("Ej. 11:00") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val horaFinal = if (horaInicioTexto.isNotBlank() || horaFinTexto.isNotBlank()) {
                        "$horaInicioTexto - $horaFinTexto"
                    } else {
                        ""
                    }
                    onSave(horaFinal)
                }
            ) {
                Text("GUARDAR")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCELAR")
            }
        }
    )
}
