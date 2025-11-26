package com.example.proyecto20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController // <-- ¡IMPORTANTE! Añadir este import
import com.example.proyecto20.model.Ejercicio
import com.example.proyecto20.ui.navigation.AppRoutes
import com.example.proyecto20.ui.viewmodels.EjercicioDetailViewModel
import com.example.proyecto20.ui.viewmodels.EjercicioDetailViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EjercicioDetailScreen(
    ejercicioId: String,
    navController: NavHostController, // <-- ¡CORRECCIÓN! Aceptamos el NavController
    onNavigateBack: () -> Unit
) {
    val viewModel: EjercicioDetailViewModel = viewModel(
        factory = EjercicioDetailViewModelFactory(ejercicioId)
    )

    val ejercicio by viewModel.ejercicio.collectAsState()

    DarkMatterBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                DarkMatterTopBar(
                    title = ejercicio?.nombre ?: "Detalle de Ejercicio",
                    onNavigateBack = onNavigateBack
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text("EDITAR") },
                    icon = { Icon(Icons.Default.Edit, contentDescription = "Editar") },
                    onClick = {
                        ejercicio?.let {
                            navController.navigate(AppRoutes.EDIT_EJERCICIO_SCREEN.replace("{ejercicioId}", it.id))
                        }
                    },
                    containerColor = DarkMatterPalette.Highlight,
                    contentColor = Color.Black
                )
            }
        ) { padding ->
            if (ejercicio == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DarkMatterPalette.Highlight)
                    Text(
                        "Cargando datos del ejercicio...",
                        modifier = Modifier.padding(top = 60.dp),
                        color = DarkMatterPalette.SecondaryText
                    )
                }
            } else {
                DetallesDelEjercicio(ejercicio = ejercicio!!, padding = padding)
            }
        }
    }
}

@Composable
private fun DetallesDelEjercicio(ejercicio: Ejercicio, padding: PaddingValues) {
    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (ejercicio.urlVideo.isNotBlank()) {
            Text(
                "URL del Video: ${ejercicio.urlVideo}",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkMatterPalette.SecondaryText
            )
        }
        Text(
            ejercicio.nombre,
            style = MaterialTheme.typography.headlineMedium,
            color = DarkMatterPalette.Highlight
        )
        HorizontalDivider(color = DarkMatterPalette.Divider)
        Text(
            "Músculo Principal",
            style = MaterialTheme.typography.titleMedium,
            color = DarkMatterPalette.Highlight
        )
        Text(
            ejercicio.musculoPrincipal,
            style = MaterialTheme.typography.bodyLarge,
            color = DarkMatterPalette.PrimaryText
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Descripción / Cómo realizarlo",
            style = MaterialTheme.typography.titleMedium,
            color = DarkMatterPalette.Highlight
        )
        Text(
            ejercicio.descripcion,
            style = MaterialTheme.typography.bodyLarge,
            color = DarkMatterPalette.PrimaryText
        )
    }
}
