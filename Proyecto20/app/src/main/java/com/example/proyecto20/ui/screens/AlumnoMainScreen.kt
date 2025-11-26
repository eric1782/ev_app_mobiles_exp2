package com.example.proyecto20.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto20.R
import com.example.proyecto20.data.FirebaseRepository
import com.example.proyecto20.model.EjercicioRutina
import com.example.proyecto20.model.Usuario
import com.example.proyecto20.ui.navigation.AppRoutes
import com.example.proyecto20.ui.viewmodels.AuthViewModel
import com.example.proyecto20.ui.viewmodels.PlanificacionViewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlumnoMainScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    user: Usuario
) {
    val viewModel: PlanificacionViewModel = viewModel(factory = PlanificacionViewModel.Factory(user.id))
    val rutina by viewModel.rutina.collectAsState()
    val horaDelDia by viewModel.horaDelDiaSeleccionado.collectAsState()

    val diaDeHoy = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es", "ES")).uppercase()
    val entrenamientoDeHoy = rutina[diaDeHoy]

    // Nos aseguramos de que el día seleccionado en el ViewModel sea siempre el de hoy
    viewModel.seleccionarDia(diaDeHoy)

    var menuExpanded by remember { mutableStateOf(false) } // Estado para controlar el menú desplegable
    var mostrarAyuda by remember { mutableStateOf(false) }
    var mostrarContacto by remember { mutableStateOf(false) }
    var entrenadorInfo by remember { mutableStateOf<Usuario?>(null) }

    LaunchedEffect(user.idEntrenador) {
        user.idEntrenador?.let { entrenadorId ->
            val entrenador = FirebaseRepository.getUsuarioById(entrenadorId)
            entrenadorInfo = entrenador
        }
    }

    val backgroundPainter = painterResource(id = R.drawable.mis_alumnos_background)
    val overlayBrush = remember {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xB0000000),
                Color(0xD0000000)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
    ) {
        // Imagen de fondo
        Image(
            painter = backgroundPainter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Overlay oscuro para mejorar la legibilidad del texto
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlayBrush)
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    ),
                    title = { Text("¡Hola, ${user.nombre}!") },
                    navigationIcon = {
                        Box {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menú")
                            }
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Cerrar sesión") },
                                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                                    onClick = {
                                        menuExpanded = false
                                        onLogout()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Ayuda") },
                                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null) },
                                    onClick = {
                                        menuExpanded = false
                                        mostrarAyuda = true
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Contacto (WhatsApp entrenador)") },
                                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                                    enabled = entrenadorInfo != null,
                                    onClick = {
                                        menuExpanded = false
                                        mostrarContacto = true
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Cambiar contraseña") },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                    onClick = {
                                        menuExpanded = false
                                        navController.navigate(AppRoutes.CAMBIAR_PASSWORD_SCREEN)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Mis datos") },
                                    leadingIcon = { Icon(Icons.Default.ManageAccounts, contentDescription = null) },
                                    onClick = {
                                        menuExpanded = false
                                        navController.navigate(AppRoutes.MIS_DATOS_SCREEN.replace("{userId}", user.id)) {
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFF1E1E1E),
                    contentColor = Color.White
                ) {
                    ModernNavigationBarItem(
                        icon = Icons.Default.Home,
                        label = "Hoy",
                        selected = true,
                        onClick = { /* No hace nada, ya estamos aquí */ }
                    )
                    ModernNavigationBarItem(
                        icon = Icons.Default.DateRange,
                        label = "Rutina",
                        selected = false,
                        onClick = {
                            navController.navigate(
                                AppRoutes.VISUALIZACION_RUTINA_SCREEN.replace("{alumnoId}", user.id)
                            )
                        }
                    )
                    ModernNavigationBarItem(
                        icon = Icons.Default.BarChart,
                        label = "Progreso",
                        selected = false,
                        onClick = {
                            navController.navigate(
                                AppRoutes.ESTADISTICAS_ALUMNO_SCREEN.replace("{alumnoId}", user.id)
                            )
                        }
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            "Entrenamiento de Hoy:",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (!horaDelDia.isNullOrBlank()) {
                            Text(
                                "Tu cita de hoy es a las: $horaDelDia",
                                style = MaterialTheme.typography.titleMedium,
                                color = DarkMatterPalette.Highlight
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                if (entrenamientoDeHoy == null || entrenamientoDeHoy.ejercicios.isEmpty()) {
                    item {
                        Box(
                            Modifier
                                .fillParentMaxWidth()
                                .padding(vertical = 50.dp), contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Hoy tienes descanso. ¡A recuperar!",
                                color = Color.White
                            )
                        }

    if (mostrarAyuda) {
        AlertDialog(
            onDismissRequest = { mostrarAyuda = false },
            confirmButton = {
                TextButton(onClick = { mostrarAyuda = false }) {
                    Text("Entendido")
                }
            },
            title = { Text("Ayuda") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("¿Necesitas ayuda?")
                    Text("Correo: soporte@proyecto20.com")
                    Text("Teléfono: +34 600 123 456")
                }
            }
        )
    }

    if (mostrarContacto) {
        val entrenador = entrenadorInfo
        AlertDialog(
            onDismissRequest = { mostrarContacto = false },
            confirmButton = {
                TextButton(onClick = { mostrarContacto = false }) {
                    Text("Cerrar")
                }
            },
            title = { Text("Contacto de tu entrenador") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (entrenador != null) {
                        Text("Entrenador: ${entrenador.nombre} ${entrenador.apellido}")
                        Text("WhatsApp: ${entrenador.whatsapp ?: "No registrado"}")
                        Text("Correo: ${entrenador.email}")
                    } else {
                        Text("No se encontró la información de contacto del entrenador.")
                    }
                }
            }
        )
    }
                    }
                } else {
                    items(entrenamientoDeHoy.ejercicios) { ejercicio ->
                        EjercicioVistaItem(
                            ejercicio = ejercicio,
                            onClick = {
                                val route = "ejercicioDetailSoloLectura/${user.id}/${ejercicio.ejercicioId}" +
                                        "?series=${ejercicio.series}" +
                                        "&repeticiones=${ejercicio.repeticiones}" +
                                        "&peso=${ejercicio.peso?.toString() ?: ""}" +
                                        "&rir=${ejercicio.rir?.toString() ?: ""}"

                                navController.navigate(route)
                            }
                        )
                    }
                }
                }
                // --- 3. EL ROW CON EL BOTÓN HA SIDO ELIMINADO DE AQUÍ ---
            }
        }
    }

    if (mostrarAyuda) {
        AlertDialog(
            onDismissRequest = { mostrarAyuda = false },
            confirmButton = {
                TextButton(onClick = { mostrarAyuda = false }) {
                    Text("Entendido")
                }
            },
            title = { Text("Ayuda") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("¿Necesitas ayuda?")
                    Text("Correo: soporte@proyecto20.com")
                    Text("Teléfono: +34 600 123 456")
                }
            }
        )
    }

    if (mostrarContacto) {
        val entrenador = entrenadorInfo
        AlertDialog(
            onDismissRequest = { mostrarContacto = false },
            confirmButton = {
                TextButton(onClick = { mostrarContacto = false }) {
                    Text("Cerrar")
                }
            },
            title = { Text("Contacto de tu entrenador") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (entrenador != null) {
                        Text("Entrenador: ${entrenador.nombre} ${entrenador.apellido}")
                        Text("WhatsApp: ${entrenador.whatsapp ?: "No registrado"}")
                        Text("Correo: ${entrenador.email}")
                    } else {
                        Text("No se encontró la información de contacto del entrenador.")
                    }
                }
            }
        )
    }
}

@Composable
fun EjercicioVistaItem(
    ejercicio: EjercicioRutina,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(ejercicio.nombre, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                val details = mutableListOf<String>()
                details.add("${ejercicio.series} series")
                details.add(ejercicio.repeticiones)
                ejercicio.peso?.let { details.add("${it}kg") }
                ejercicio.rir?.let { details.add("RIR $it") }

                Text(
                    text = details.joinToString("  •  "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ver detalle",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
