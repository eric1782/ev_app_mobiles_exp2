package com.example.proyecto20.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto20.R
import com.example.proyecto20.data.api.InformacionCaloriasEjercicio
import com.example.proyecto20.model.EjercicioRutina
import com.example.proyecto20.model.Usuario
import com.example.proyecto20.ui.navigation.AppRoutes
import com.example.proyecto20.ui.viewmodels.CalculadoraCaloriasViewModel
import com.example.proyecto20.ui.viewmodels.PlanificacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualizacionRutinaScreen(
    viewModel: PlanificacionViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEjercicioDetail: (EjercicioRutina) -> Unit,
    navController: NavController,
    user: Usuario
) {
    val rutina by viewModel.rutina.collectAsState()
    val calculadoraCaloriasViewModel: CalculadoraCaloriasViewModel = viewModel()
    val caloriasPorDia by calculadoraCaloriasViewModel.caloriasPorDia.collectAsState()
    val estaCargandoCalorias by calculadoraCaloriasViewModel.estaCargando.collectAsState()
    
    // Calcular calorías cuando cambia la rutina
    LaunchedEffect(rutina) {
        if (rutina.isNotEmpty()) {
            val rutinaLista = rutina.values.toList()
            calculadoraCaloriasViewModel.calcularCaloriasRutina(rutinaLista, user)
        }
    }

    val backgroundPainter = painterResource(id = R.drawable.ejercicio_background)
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
                    title = { Text("Mi Rutina") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
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
                        selected = false,
                        onClick = {
                            navController.navigate(AppRoutes.HOME_SCREEN) {
                                launchSingleTop = true
                                popUpTo(AppRoutes.HOME_SCREEN) { inclusive = true }
                            }
                        }
                    )
                    ModernNavigationBarItem(
                        icon = Icons.Default.DateRange,
                        label = "Rutina",
                        selected = true,
                        onClick = { /* Ya estamos aquí */ }
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
            if (rutina.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Aún no tienes una rutina asignada.",
                        color = Color.White
                    )
                }
            } else {
                val diasOrdenados = listOf("LUNES", "MARTES", "MIÉRCOLES", "JUEVES", "VIERNES", "SÁBADO", "DOMINGO")
                val diasDeEntrenamiento = rutina.values.sortedBy { diasOrdenados.indexOf(it.dia.uppercase()) }

                LazyColumn(
                    modifier = Modifier.padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(items = diasDeEntrenamiento, key = { it.dia }) { diaEntrenamiento ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF2C2C2C)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = diaEntrenamiento.dia,
                                        color = DarkMatterPalette.Highlight,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    // Mostrar calorías totales del día
                                    when {
                                        caloriasPorDia[diaEntrenamiento.dia] != null -> {
                                            val infoCalorias = caloriasPorDia[diaEntrenamiento.dia]!!
                                            Text(
                                                text = "${infoCalorias.caloriasTotales.toInt()} kcal",
                                                color = Color(0xFFFF6B6B),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        estaCargandoCalorias -> {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                color = Color(0xFFFF6B6B),
                                                strokeWidth = 2.dp
                                            )
                                        }
                                        else -> {
                                            // No mostrar nada si no está cargando y no hay datos
                                        }
                                    }
                                }
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = Color(0xFF3A3A3A)
                                )
                                if (diaEntrenamiento.ejercicios.isEmpty()) {
                                    Text(
                                        "Día de descanso.",
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                } else {
                                    diaEntrenamiento.ejercicios.forEachIndexed { index, ejercicio ->
                                        val caloriasEjercicio = caloriasPorDia[diaEntrenamiento.dia]?.ejercicios?.getOrNull(index)
                                        EjercicioRutinaItem(
                                            ejercicio = ejercicio,
                                            caloriasEjercicio = caloriasEjercicio,
                                            onClick = { onNavigateToEjercicioDetail(ejercicio) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EjercicioRutinaItem(
    ejercicio: EjercicioRutina,
    caloriasEjercicio: InformacionCaloriasEjercicio? = null,
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
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    ejercicio.nombre,
                    color = DarkMatterPalette.Highlight,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                // Mostrar calorías del ejercicio
                caloriasEjercicio?.let {
                    Text(
                        text = "${it.caloriasTotales.toInt()} kcal",
                        color = Color(0xFFFF6B6B),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Series: ${ejercicio.series}",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = "Reps: ${ejercicio.repeticiones}",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
            if (ejercicio.peso != null || ejercicio.rir != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ejercicio.peso?.let {
                        Text(
                            text = "Peso: ${it}kg",
                            color = Color(0xFFCCCCCC),
                            fontSize = 12.sp
                        )
                    }
                    ejercicio.rir?.let {
                        Text(
                            text = "RIR: $it",
                            color = Color(0xFFCCCCCC),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

