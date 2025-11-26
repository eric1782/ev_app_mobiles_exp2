// Ruta: app/src/main/java/com/example/proyecto20/ui/screens/MisEjerciciosScreen.kt

package com.example.proyecto20.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyecto20.model.Ejercicio
import com.example.proyecto20.ui.navigation.AppRoutes
import com.example.proyecto20.ui.viewmodels.EjerciciosViewModel
import com.example.proyecto20.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisEjerciciosScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    ejerciciosViewModel: EjerciciosViewModel
) {
    val ejercicios by ejerciciosViewModel.ejercicios.collectAsState()
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
        Image(
            painter = backgroundPainter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlayBrush)
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Catálogo de Ejercicios",
                            color = Color(0xFFFFEB3B),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(AppRoutes.ADD_EJERCICIO_SCREEN) },
                    containerColor = Color(0xFFFFEB3B),
                    contentColor = Color.Black
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Ejercicio")
                }
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
                            }
                        }
                    )
                    ModernNavigationBarItem(
                        icon = Icons.Default.Person,
                        label = "Alumnos",
                        selected = false,
                        onClick = {
                            navController.navigate(AppRoutes.MIS_ALUMNOS_SCREEN) {
                                launchSingleTop = true
                            }
                        }
                    )
                    ModernNavigationBarItem(
                        icon = Icons.Default.CalendarMonth,
                        label = "Calendario",
                        selected = false,
                        onClick = {
                            navController.navigate(AppRoutes.CALENDARIO_SCREEN) {
                                launchSingleTop = true
                            }
                        }
                    )
                    ModernNavigationBarItem(
                        icon = Icons.Default.FitnessCenter,
                        label = "Ejercicios",
                        selected = true,
                        onClick = { }
                    )
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(ejercicios) { ejercicio ->
                    EjercicioItem(
                        ejercicio = ejercicio,
                        onClick = {
                            navController.navigate(
                                AppRoutes.EJERCICIO_DETAIL_SCREEN.replace("{ejercicioId}", ejercicio.id)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EjercicioItem(ejercicio: Ejercicio, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C)),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                ejercicio.nombre,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                ejercicio.musculoPrincipal,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFBBBBBB)
            )
        }
    }
}
