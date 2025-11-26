package com.example.proyecto20.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto20.model.Timer
import com.example.proyecto20.model.TimerPhase
import com.example.proyecto20.model.TimerState
import com.example.proyecto20.ui.viewmodels.TimerViewModel
import kotlin.math.min

@Composable
fun TemporizadoresScreen() {
    val context = LocalContext.current
    val timerViewModel: TimerViewModel = viewModel(factory = TimerViewModel.Factory(context))
    val temporizadores by timerViewModel.temporizadores.collectAsState()
    
    var mostrarCrearTimer by remember { mutableStateOf(true) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título
        Text(
            text = "Temporizadores",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Toggle entre crear y ver lista
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                onClick = { mostrarCrearTimer = true },
                label = { Text("Crear") },
                selected = mostrarCrearTimer,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = DarkMatterPalette.Highlight,
                    selectedLabelColor = Color.Black
                )
            )
            FilterChip(
                onClick = { mostrarCrearTimer = false },
                label = { Text("Activos (${temporizadores.size})") },
                selected = !mostrarCrearTimer,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = DarkMatterPalette.Highlight,
                    selectedLabelColor = Color.Black
                )
            )
        }
        
        if (mostrarCrearTimer) {
            CrearTimerView(
                onCreateTimer = { nombre, minutos, segundos, descanso, repeticiones ->
                    timerViewModel.crearTemporizador(nombre, minutos, segundos, descanso, repeticiones)
                    mostrarCrearTimer = false
                }
            )
        } else {
            ListaTemporizadoresView(
                temporizadores = temporizadores,
                onIniciar = { timerViewModel.iniciarTemporizador(it) },
                onPausar = { timerViewModel.pausarTemporizador(it) },
                onReanudar = { timerViewModel.reanudarTemporizador(it) },
                onEliminar = { timerViewModel.eliminarTemporizador(it) }
            )
        }
    }
}

@Composable
fun CrearTimerView(
    onCreateTimer: (String, Int, Int, Int, Int) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var minutosTrabajo by remember { mutableStateOf("1") }
    var segundosTrabajo by remember { mutableStateOf("0") }
    var segundosDescanso by remember { mutableStateOf("15") }
    var repeticiones by remember { mutableStateOf("4") }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2C2C2C)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Nombre del temporizador
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del temporizador", color = DarkMatterPalette.SecondaryText) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = DarkMatterPalette.Highlight,
                    unfocusedBorderColor = Color(0xFF555555)
                ),
                singleLine = true
            )
            
            // Sección "Entrenar"
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Entrenar:",
                    color = DarkMatterPalette.Highlight,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Minutos
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Minutos",
                            color = DarkMatterPalette.SecondaryText,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = minutosTrabajo,
                            onValueChange = { if (it.all { char -> char.isDigit() }) minutosTrabajo = it },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = DarkMatterPalette.Highlight,
                                unfocusedBorderColor = Color(0xFF555555)
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ),
                            singleLine = true
                        )
                    }
                    
                    Text(
                        text = ":",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Segundos
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Segundos",
                            color = DarkMatterPalette.SecondaryText,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = segundosTrabajo,
                            onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 2) segundosTrabajo = it },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = DarkMatterPalette.Highlight,
                                unfocusedBorderColor = Color(0xFF555555)
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ),
                            singleLine = true
                        )
                    }
                }
            }
            
            // Sección "Descanso"
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Descanso:",
                    color = DarkMatterPalette.PrimaryText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                OutlinedTextField(
                    value = segundosDescanso,
                    onValueChange = { if (it.all { char -> char.isDigit() }) segundosDescanso = it },
                    label = { Text("Segundos", color = DarkMatterPalette.SecondaryText) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = DarkMatterPalette.Highlight,
                        unfocusedBorderColor = Color(0xFF555555)
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    singleLine = true
                )
            }
            
            // Repeticiones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Repeticiones:",
                    color = DarkMatterPalette.PrimaryText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val actual = repeticiones.toIntOrNull() ?: 1
                            if (actual > 1) repeticiones = (actual - 1).toString()
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color(0xFF3A3A3A),
                                RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Decrementar",
                            tint = Color.White
                        )
                    }
                    
                    Text(
                        text = repeticiones,
                        color = DarkMatterPalette.Highlight,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(40.dp),
                        textAlign = TextAlign.Center
                    )
                    
                    IconButton(
                        onClick = {
                            val actual = repeticiones.toIntOrNull() ?: 1
                            repeticiones = (actual + 1).toString()
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color(0xFF3A3A3A),
                                RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Incrementar",
                            tint = Color.White
                        )
                    }
                }
            }
            
            // Botón crear
            Button(
                onClick = {
                    val nombreFinal = nombre.ifBlank { "Temporizador" }
                    val minutos = minutosTrabajo.toIntOrNull() ?: 0
                    val segundos = segundosTrabajo.toIntOrNull() ?: 0
                    val descanso = segundosDescanso.toIntOrNull() ?: 15
                    val reps = repeticiones.toIntOrNull() ?: 4
                    
                    if (minutos > 0 || segundos > 0) {
                        onCreateTimer(nombreFinal, minutos, segundos, descanso, reps)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = DarkMatterButtonColors()
            ) {
                Text("Crear Temporizador", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ListaTemporizadoresView(
    temporizadores: List<Timer>,
    onIniciar: (String) -> Unit,
    onPausar: (String) -> Unit,
    onReanudar: (String) -> Unit,
    onEliminar: (String) -> Unit
) {
    if (temporizadores.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 50.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay temporizadores activos",
                color = DarkMatterPalette.SecondaryText,
                fontSize = 16.sp
            )
        }
    } else {
        // Mostrar el primer temporizador activo o el primero en la lista
        val timerActivo = temporizadores.firstOrNull { it.estado == TimerState.RUNNING } 
            ?: temporizadores.firstOrNull { it.estado == TimerState.PAUSED }
            ?: temporizadores.first()
        
        TemporizadorVistaGrande(
            timer = timerActivo,
            onIniciar = { onIniciar(timerActivo.id) },
            onPausar = { onPausar(timerActivo.id) },
            onReanudar = { onReanudar(timerActivo.id) },
            onEliminar = { onEliminar(timerActivo.id) }
        )
        
        // Mostrar lista de otros temporizadores si hay más de uno
        if (temporizadores.size > 1) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Otros temporizadores",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                items(temporizadores.filter { it.id != timerActivo?.id }, key = { it.id }) { timer ->
                    TemporizadorCardCompacto(
                        timer = timer,
                        onIniciar = { onIniciar(timer.id) },
                        onPausar = { onPausar(timer.id) },
                        onReanudar = { onReanudar(timer.id) },
                        onEliminar = { onEliminar(timer.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun TemporizadorVistaGrande(
    timer: Timer,
    onIniciar: () -> Unit,
    onPausar: () -> Unit,
    onReanudar: () -> Unit,
    onEliminar: () -> Unit
) {
    val tiempoTotal = when (timer.faseActual) {
        TimerPhase.PREPARE -> 5 // 5 segundos de preparación
        TimerPhase.WORK -> timer.tiempoTrabajoSegundos
        TimerPhase.REST -> timer.tiempoDescansoSegundos
    }
    val progreso = if (tiempoTotal > 0) {
        (timer.tiempoRestanteSegundos.toFloat() / tiempoTotal.toFloat()).coerceIn(0f, 1f)
    } else 0f
    
    val minutos = timer.tiempoRestanteSegundos / 60
    val segundos = timer.tiempoRestanteSegundos % 60
    val tiempoFormateado = String.format("%02d:%02d", minutos, segundos)
    
    // Calcular tiempo total restante de la sesión
    val rondasRestantes = timer.repeticiones - timer.repeticionesCompletadas
    val tiempoTotalRestante = when {
        timer.estado == TimerState.IDLE -> {
            // Si está en IDLE, calcular tiempo total de la sesión completa
            (timer.repeticiones * timer.tiempoTrabajoSegundos) + 
            (timer.repeticiones * timer.tiempoDescansoSegundos)
        }
        timer.faseActual == TimerPhase.PREPARE -> {
            timer.tiempoRestanteSegundos + 
            (rondasRestantes * timer.tiempoTrabajoSegundos) + 
            (rondasRestantes * timer.tiempoDescansoSegundos)
        }
        timer.faseActual == TimerPhase.WORK -> {
            timer.tiempoRestanteSegundos + 
            (rondasRestantes - 1) * timer.tiempoTrabajoSegundos + 
            (rondasRestantes * timer.tiempoDescansoSegundos)
        }
        timer.faseActual == TimerPhase.REST -> {
            timer.tiempoRestanteSegundos + 
            (rondasRestantes * timer.tiempoTrabajoSegundos) + 
            ((rondasRestantes - 1) * timer.tiempoDescansoSegundos)
        }
        else -> 0
    }
    val minutosTotales = tiempoTotalRestante / 60
    val segundosTotales = tiempoTotalRestante % 60
    val tiempoTotalFormateado = String.format("%02d:%02d", minutosTotales, segundosTotales)
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar con nombre y tiempo total
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = timer.nombre.uppercase(),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = tiempoTotalFormateado,
                color = DarkMatterPalette.Highlight,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Mostrar fase actual o estado IDLE
        when {
            timer.estado == TimerState.IDLE -> {
                // Estado IDLE - mostrar tiempo de trabajo configurado
                val minutosTrabajo = timer.tiempoTrabajoSegundos / 60
                val segundosTrabajo = timer.tiempoTrabajoSegundos % 60
                val tiempoTrabajoFormateado = String.format("%02d:%02d", minutosTrabajo, segundosTrabajo)
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFF2C2C2C),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(vertical = 24.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "WORK:",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = tiempoTrabajoFormateado,
                            color = Color.White,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            timer.faseActual == TimerPhase.PREPARE -> {
                // Fase PREPARE (amarillo)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            DarkMatterPalette.Highlight,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(vertical = 24.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "PREPARE",
                            color = Color.Black,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = tiempoFormateado,
                            color = Color.Black,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            timer.faseActual == TimerPhase.WORK -> {
                // Fase WORK (verde lima)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFF81C784), // Verde lima
                            RoundedCornerShape(12.dp)
                        )
                        .padding(vertical = 24.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "WORK:",
                            color = Color.Black,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = tiempoFormateado,
                            color = Color.Black,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            timer.faseActual == TimerPhase.REST -> {
                // Fase REST (naranja)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFFFF9800), // Naranja
                            RoundedCornerShape(12.dp)
                        )
                        .padding(vertical = 24.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "REST:",
                            color = Color.Black,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = tiempoFormateado,
                            color = Color.Black,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        
        // Controles inferiores
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ROUNDS LEFT (izquierda)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${timer.repeticiones - timer.repeticionesCompletadas}",
                    color = Color(0xFF2196F3), // Azul
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ROUNDS LEFT",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
            
            // Botón circular grande con progreso (centro)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Círculo de progreso exterior
                    CircularProgressIndicator(
                        progress = { if (timer.estado == TimerState.RUNNING) 1f - progreso else 0f },
                        modifier = Modifier.size(100.dp),
                        color = DarkMatterPalette.Highlight,
                        strokeWidth = 8.dp,
                        strokeCap = StrokeCap.Round
                    )
                    // Botón circular
                    IconButton(
                        onClick = {
                            when (timer.estado) {
                                TimerState.IDLE -> onIniciar()
                                TimerState.RUNNING -> onPausar()
                                TimerState.PAUSED -> onReanudar()
                                else -> {}
                            }
                        },
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                Color(0xFF2C2C2C),
                                RoundedCornerShape(50)
                            )
                    ) {
                        Icon(
                            imageVector = when (timer.estado) {
                                TimerState.IDLE, TimerState.PAUSED -> Icons.Default.PlayArrow
                                TimerState.RUNNING -> Icons.Default.Pause
                                else -> Icons.Default.Check
                            },
                            contentDescription = null,
                            tint = DarkMatterPalette.Highlight,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when (timer.estado) {
                        TimerState.IDLE -> "START"
                        TimerState.RUNNING -> "PAUSE"
                        TimerState.PAUSED -> "RESUME"
                        else -> "DONE"
                    },
                    color = DarkMatterPalette.Highlight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Botón eliminar (derecha) - simplificado
            IconButton(
                onClick = onEliminar,
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Eliminar",
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "DELETE",
                        color = Color(0xFFFF5252),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun TemporizadorCardCompacto(
    timer: Timer,
    onIniciar: () -> Unit,
    onPausar: () -> Unit,
    onReanudar: () -> Unit,
    onEliminar: () -> Unit
) {
    val tiempoTotal = when (timer.faseActual) {
        TimerPhase.PREPARE -> 5
        TimerPhase.WORK -> timer.tiempoTrabajoSegundos
        TimerPhase.REST -> timer.tiempoDescansoSegundos
    }
    val progreso = if (tiempoTotal > 0) {
        (timer.tiempoRestanteSegundos.toFloat() / tiempoTotal.toFloat()).coerceIn(0f, 1f)
    } else 0f
    
    val minutos = timer.tiempoRestanteSegundos / 60
    val segundos = timer.tiempoRestanteSegundos % 60
    val tiempoFormateado = String.format("%02d:%02d", minutos, segundos)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2C2C2C)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Círculo de progreso
                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { 1f - progreso },
                        modifier = Modifier.size(100.dp),
                        color = when (timer.faseActual) {
                            TimerPhase.PREPARE -> DarkMatterPalette.Highlight
                            TimerPhase.WORK -> Color(0xFF4CAF50)
                            TimerPhase.REST -> Color(0xFFFF9800)
                        },
                        strokeWidth = 8.dp,
                        strokeCap = StrokeCap.Round
                    )
                    Text(
                        text = tiempoFormateado,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Información y controles
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = timer.nombre,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = onEliminar,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Eliminar",
                                tint = Color(0xFFFF5252),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    Text(
                        text = when (timer.estado) {
                            TimerState.IDLE -> "Listo para iniciar"
                            TimerState.RUNNING -> when (timer.faseActual) {
                                TimerPhase.PREPARE -> "Preparación"
                                TimerPhase.WORK -> "En trabajo"
                                TimerPhase.REST -> "Descanso"
                            }
                            TimerState.PAUSED -> "Pausado"
                            TimerState.COMPLETED -> "Completado"
                        },
                        color = DarkMatterPalette.SecondaryText,
                        fontSize = 14.sp
                    )
                    
                    Text(
                        text = "${timer.repeticionesCompletadas} / ${timer.repeticiones} rondas",
                        color = DarkMatterPalette.Highlight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Controles
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (timer.estado) {
                            TimerState.IDLE -> {
                                Button(
                                    onClick = onIniciar,
                                    colors = DarkMatterButtonColors(),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Iniciar")
                                }
                            }
                            TimerState.RUNNING -> {
                                Button(
                                    onClick = onPausar,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF3A3A3A),
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        Icons.Default.Pause,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Pausar")
                                }
                            }
                            TimerState.PAUSED -> {
                                Button(
                                    onClick = onReanudar,
                                    colors = DarkMatterButtonColors(),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Reanudar")
                                }
                            }
                            TimerState.COMPLETED -> {
                                Text(
                                    text = "Completado",
                                    color = Color(0xFF4CAF50),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

