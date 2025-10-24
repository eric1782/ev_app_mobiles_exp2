package com.example.proyecto20.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto20.model.RegistroProgreso
import com.example.proyecto20.ui.viewmodels.EjercicioDetailViewModel
import com.example.proyecto20.ui.viewmodels.EjercicioDetailViewModelFactory
import com.example.proyecto20.ui.viewmodels.EstadisticasViewModel
import com.example.proyecto20.ui.viewmodels.EstadisticasViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EjercicioDetailScreenSoloLectura(
    alumnoId: String,
    ejercicioId: String,
    series: String?,
    repeticiones: String?,
    peso: String?,
    rir: String?,
    onNavigateBack: () -> Unit
) {
    val detailViewModel: EjercicioDetailViewModel = viewModel(factory = EjercicioDetailViewModelFactory(ejercicioId))
    val ejercicio by detailViewModel.ejercicio.collectAsState()

    val estadisticasViewModel: EstadisticasViewModel = viewModel(factory = EstadisticasViewModelFactory(alumnoId))

    // Se carga el historial completo. La pantalla filtrará lo que necesite.
    LaunchedEffect(Unit) {
        estadisticasViewModel.cargarHistorialCompletoAgrupado()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(ejercicio?.nombre ?: "Detalle del Ejercicio") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } }
            )
        }
    ) { padding ->
        // Obtenemos el historial agrupado del ViewModel.
        val historialAgrupado by estadisticasViewModel.historialAgrupado.collectAsState()
        // Filtramos la lista que nos interesa usando el nombre del ejercicio actual.
        val historialFiltrado = historialAgrupado[ejercicio?.nombre] ?: emptyList()

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ejercicio?.let { ej ->
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Tu objetivo de hoy:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                                series?.let { Text("$it series", style = MaterialTheme.typography.bodyLarge) }
                                repeticiones?.let { Text(it, style = MaterialTheme.typography.bodyLarge) }
                                peso?.takeIf { it.isNotBlank() }?.let { Text("${it}kg", style = MaterialTheme.typography.bodyLarge) }
                                rir?.takeIf { it.isNotBlank() }?.let { Text("RIR $it", style = MaterialTheme.typography.bodyLarge) }
                            }
                        }
                    }
                }
                item {
                    Column {
                        Text("Músculo Principal: ${ej.musculoPrincipal}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(ej.descripcion, style = MaterialTheme.typography.bodyLarge)
                    }
                }

                if (historialFiltrado.isNotEmpty()) {
                    item {
                        Text("Historial de Progreso", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    }
                    // Ordenamos la lista filtrada por fecha descendente para mostrar lo más reciente primero.
                    items(historialFiltrado.sortedByDescending { it.timestamp }) { registro ->
                        HistorialItem(registro = registro)
                    }
                }

                ej.urlVideo?.let { url ->
                    if (url.isNotBlank()) {
                        item {
                            VideoPlayer(videoUrl = url, modifier = Modifier.height(250.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistorialItem(registro: RegistroProgreso) {
    // Usamos el mismo formato de fecha que en la pantalla de estadísticas para mantener la consistencia.
    val dateFormatter = remember { SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("es", "ES")) }
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
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
                        Icons.Default.Comment,
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

@Composable
fun VideoPlayer(videoUrl: String, modifier: Modifier = Modifier) {
    val videoId = videoUrl.substringAfterLast("v=", "").substringBefore("&")
    val embedUrl = "https://www.youtube.com/embed/$videoId"
    AndroidView(modifier = modifier.fillMaxWidth(), factory = { context ->
        WebView(context).apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            loadUrl(embedUrl)
        }
    })
}
