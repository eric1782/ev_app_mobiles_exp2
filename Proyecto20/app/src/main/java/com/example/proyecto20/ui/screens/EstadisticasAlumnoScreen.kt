package com.example.proyecto20.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto20.R
import com.example.proyecto20.model.RegistroProgreso
import com.example.proyecto20.model.Usuario
import com.example.proyecto20.ui.navigation.AppRoutes
import com.example.proyecto20.ui.viewmodels.EstadisticasViewModel
import com.example.proyecto20.ui.viewmodels.EstadisticasViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

// --- Imports de Vico temporalmente comentados ---
// import com.patrykandpatrick.vico.compose.CartesianChart
// import com.patrykandpatrick.vico.compose.layer.rememberLineCartesianLayer
// import com.patrykandpatrick.vico.compose.rememberCartesianChart
// import com.patrykandpatrick.vico.core.CartesianChartModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisticasAlumnoScreen(
    alumnoId: String,
    onNavigateToDetail: (String) -> Unit,
    onNavigateBack: () -> Unit,
    navController: NavController,
    user: Usuario
) {
    val viewModel: EstadisticasViewModel = viewModel(factory = EstadisticasViewModelFactory(alumnoId))
    val historialAgrupado by viewModel.historialAgrupado.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarHistorialCompletoAgrupado()
    }

    val backgroundPainter = painterResource(id = R.drawable.login_background)
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
                    title = { Text("Mi Progreso") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
                        selected = true,
                        onClick = { /* Ya estamos aquí */ }
                    )
                }
            }
        ) { padding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding)) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (historialAgrupado.isEmpty()) {
                    Text(
                        text = "Aún no hay registros de progreso. ¡Pídele a tu entrenador que guarde tu primer entrenamiento!",
                        modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .align(Alignment.Center),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(historialAgrupado.keys.toList().sorted()) { ejercicioNombre ->
                        EjercicioEstadisticaItem(
                            nombre = ejercicioNombre,
                            onClick = { onNavigateToDetail(ejercicioNombre) }
                        )
                    }
                    }
                }
            }
        }
    }
}

@Composable
private fun EjercicioEstadisticaItem(nombre: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(nombre, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Ver detalle")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisticasDetalleScreen(
    alumnoId: String,
    ejercicioNombre: String,
    onNavigateBack: () -> Unit
) {
    val viewModel: EstadisticasViewModel = viewModel(factory = EstadisticasViewModelFactory(alumnoId))
    val historialAgrupado by viewModel.historialAgrupado.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    // val datosGrafico by viewModel.datosGrafico.collectAsState()

    // LaunchedEffect(historialAgrupado) {
    //     if (historialAgrupado.isNotEmpty()) {
    //         viewModel.prepararDatosParaGrafico(ejercicioNombre)
    //     }
    // }

    LaunchedEffect(Unit) {
        if (historialAgrupado.isEmpty()) {
            viewModel.cargarHistorialCompletoAgrupado()
        }
    }

    DarkMatterBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(ejercicioNombre) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { padding ->
            val historialDelEjercicio = historialAgrupado[ejercicioNombre] ?: emptyList()

            Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding)) {
                if (isLoading && historialDelEjercicio.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text("Evolución del Peso (kg)", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Card(
                                modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(vertical = 16.dp)
                        ) {
                                // Gráfico temporalmente deshabilitado hasta resolver imports de Vico
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Text("Gráfico temporalmente deshabilitado", style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }

                        item {
                            Text("Historial Completo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        }

                        items(historialDelEjercicio.sortedByDescending { it.timestamp }) { registro ->
                            HistorialItem(registro = registro)
                        }
                    }
                }
            }
        }
    }
}

// Función temporalmente comentada hasta resolver imports de Vico
/*
@Composable
fun CartesianChartHost(chartModel: CartesianChartModel) {
    CartesianChart(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(),
        ),
        model = chartModel,
        modifier = Modifier.padding(8.dp)
    )
}
*/

@Composable
private fun HistorialItem(registro: RegistroProgreso) {
    val dateFormatter = remember { SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("es", "ES")) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            registro.timestamp?.let { timestamp ->
                Text(
                    text = dateFormatter.format(timestamp.toDate()),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${registro.series} series", style = MaterialTheme.typography.bodyLarge)
                Text(registro.repeticiones, style = MaterialTheme.typography.bodyLarge)
                registro.peso?.let { Text("${it}kg", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold) }
                registro.rir?.let { Text("RIR ${it}", style = MaterialTheme.typography.bodyLarge) }
            }
            registro.comentario?.takeIf { it.isNotBlank() }?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.AutoMirrored.Filled.Comment,
                        contentDescription = "Comentario",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
