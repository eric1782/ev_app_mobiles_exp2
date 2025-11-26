package com.example.proyecto20.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto20.R
import com.example.proyecto20.model.TipoAlumno
import com.example.proyecto20.model.Usuario
import com.example.proyecto20.ui.navigation.AppRoutes
import com.example.proyecto20.ui.viewmodels.AuthState
import com.example.proyecto20.ui.viewmodels.AuthViewModel
import com.example.proyecto20.ui.viewmodels.CalendarioViewModel
import com.example.proyecto20.ui.viewmodels.CitaCalendario
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EntrenadorMainScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val authState by authViewModel.authState.collectAsState()
    var user: Usuario? = null

    if (authState is AuthState.Authenticated) {
        user = (authState as AuthState.Authenticated).user
    }

    val calendarioViewModel: CalendarioViewModel = viewModel(
        factory = CalendarioViewModel.Factory(user?.id ?: "")
    )

    val fechaSeleccionada by calendarioViewModel.fechaSeleccionada.collectAsState()
    val citasDeHoy by calendarioViewModel.citasDelDia.collectAsState(initial = emptyList())
    val isLoading by calendarioViewModel.isLoading.collectAsState()
    var menuExpanded by remember { mutableStateOf(false) }
    var mostrarDialogoAyuda by remember { mutableStateOf(false) }
    val backgroundPainter = painterResource(id = R.drawable.login_background)
    val overlayBrush = remember {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xB0000000),
                Color(0xD0000000)
            )
        )
    }
    
    // Obtener el día de la semana actual
    val diaDeLaSemana = remember(fechaSeleccionada) {
        fechaSeleccionada.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
            .replaceFirstChar { it.titlecase(Locale.getDefault()) }
    }
    
    // Estado del pager para las dos páginas (Agenda y Temporizadores)
    val pagerState = rememberPagerState(pageCount = { 2 }, initialPage = 0)

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
                navigationIcon = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menú",
                                tint = Color.White
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Cerrar sesión") },
                                leadingIcon = {
                                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                                },
                                onClick = {
                                    menuExpanded = false
                                    onLogout()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Ayuda") },
                                leadingIcon = {
                                    Icon(Icons.Outlined.SupportAgent, contentDescription = null)
                                },
                                onClick = {
                                    menuExpanded = false
                                    mostrarDialogoAyuda = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Cambiar contraseña") },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null)
                                },
                                onClick = {
                                    menuExpanded = false
                                    navController.navigate(AppRoutes.CAMBIAR_PASSWORD_SCREEN)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Mis datos") },
                                leadingIcon = {
                                    Icon(Icons.Default.ManageAccounts, contentDescription = null)
                                },
                                enabled = user != null,
                                onClick = {
                                    menuExpanded = false
                                    user?.id?.let { id ->
                                        navController.navigate(AppRoutes.MIS_DATOS_SCREEN.replace("{userId}", id)) {
                                            launchSingleTop = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                },
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Hola,",
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            text = user?.nombre ?: "",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFEB3B)
                        )
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.width(48.dp))
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
                        onClick = { }
                    )
                    ModernNavigationBarItem(
                        icon = Icons.Default.Person,
                        label = "Alumnos",
                        selected = false,
                        onClick = { navController.navigate(AppRoutes.MIS_ALUMNOS_SCREEN) }
                    )
                    ModernNavigationBarItem(
                        icon = Icons.Default.CalendarMonth,
                        label = "Calendario",
                        selected = false,
                        onClick = { navController.navigate(AppRoutes.CALENDARIO_SCREEN) }
                    )
                    ModernNavigationBarItem(
                        icon = Icons.Default.FitnessCenter,
                        label = "Ejercicios",
                        selected = false,
                        onClick = { navController.navigate(AppRoutes.LISTA_EJERCICIOS_SCREEN) }
                    )
                }
            }
    ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> AgendaPage(
                            diaDeLaSemana = diaDeLaSemana,
                            isLoading = isLoading,
                            citasDeHoy = citasDeHoy,
                            onCambiarDia = { dias -> calendarioViewModel.cambiarDia(dias) },
                            onNavigateToPlanificacion = { alumnoId ->
                                navController.navigate(
                                    AppRoutes.PLANIFICACION_RUTINA_SCREEN.replace("{alumnoId}", alumnoId)
                                )
                            }
                        )
                        1 -> TemporizadoresScreen()
                    }
                }
                
                // Indicadores de página (fijos en la parte inferior)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, top = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(2) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 10.dp else 8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (isSelected) Color(0xFFFFEB3B) else Color(0x66FFFFFF)
                                )
                        )
                        if (index < 1) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogoAyuda) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoAyuda = false },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoAyuda = false }) {
                    Text("Entendido")
                }
            },
            title = { Text("Ayuda") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("¿Necesitas asistencia? Contáctanos:")
                    Text("Correo: soporte@proyecto20.com")
                    Text("Teléfono: +34 600 123 456")
                    Text("Horario: Lunes a Viernes 9:00 - 18:00")
                }
            }
        )
    }
}

@Composable
fun AgendaPage(
    diaDeLaSemana: String,
    isLoading: Boolean,
    citasDeHoy: List<CitaCalendario>,
    onCambiarDia: (Long) -> Unit,
    onNavigateToPlanificacion: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Hora de desbloquear el potencial.",
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        // Sección "Tus rutinas de hoy"
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = Color(0xFFFFEB3B),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tus alumnos de hoy",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Selector de día
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onCambiarDia(-1) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Día anterior",
                        tint = Color(0xFFFFEB3B)
                    )
                }
                Text(
                    text = diaDeLaSemana,
                    color = Color(0xFFFFEB3B),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = { onCambiarDia(1) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Día siguiente",
                        tint = Color(0xFFFFEB3B)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        when {
            isLoading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(vertical = 50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFFFEB3B))
                    }
                }
            }
            citasDeHoy.isEmpty() -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(vertical = 50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No tienes citas programadas para hoy.",
                            color = Color.White
                        )
                    }
                }
            }
            else -> {
                items(citasDeHoy) { cita ->
                    RutinaCard(
                        cita = cita,
                        onClick = { onNavigateToPlanificacion(cita.alumnoId) }
                    )
                }
            }
        }
    }
}

@Composable
fun RutinaCard(
    cita: CitaCalendario,
    onClick: () -> Unit
) {
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2C2C2C)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Foto de perfil circular
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(30.dp)) // 50% del tamaño = círculo perfecto
                        .background(Color(0xFF3A3A3A)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
                
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
                            text = cita.nombreAlumno,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp)) // 50% del tamaño = círculo perfecto
                                    .background(Color(0xFF2196F3))
                            )
                            Text(
                                text = cita.detalle,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                    
                    Text(
                        text = "Lorem ipsum dolor sit amet consectetur. Tortor aenean suspendisse pretium nunc non facilisi.",
                        color = Color(0xFFCCCCCC),
                        fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = Color(0xFF2196F3),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (cita.tipo == TipoAlumno.PRESENCIAL) "Presencial" else "Online",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = Color(0xFF2196F3),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${cita.numEjercicios} ejercicios",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
