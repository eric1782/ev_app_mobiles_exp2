package com.example.proyecto20.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
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

    DarkMatterBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                DarkMatterTopBar(
                    title = ejercicio?.nombre ?: "Detalle del Ejercicio",
                    onNavigateBack = onNavigateBack
                )
            }
        ) { padding ->
            val historialAgrupado by estadisticasViewModel.historialAgrupado.collectAsState()
            val historialFiltrado = historialAgrupado[ejercicio?.nombre] ?: emptyList()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ejercicio?.let { ej ->
                    // Mostrar GIF, Imagen o Video (prioridad: GIF > Imagen > Video) - PRIMERO
                    val mediaUrl = when {
                        ej.urlGif.isNotBlank() -> ej.urlGif to "GIF"
                        ej.urlImagen.isNotBlank() -> ej.urlImagen to "Imagen"
                        ej.urlVideo.isNotBlank() -> ej.urlVideo to "Video"
                        else -> null to null
                    }

                    // Log para debug
                    mediaUrl.first?.let { url ->
                        android.util.Log.d("EjercicioDetailScreen", "Cargando media: ${mediaUrl.second}, URL: $url")
                        item(key = "media_${url}") { // Key estable para evitar recomposiciones innecesarias
                            when (mediaUrl.second) {
                                "GIF", "Imagen" -> {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                                    ) {
                                        val context = LocalContext.current
                                        
                                        // Log detallado de la URL
                                        LaunchedEffect(url) {
                                            android.util.Log.d("EjercicioDetailScreen", "=== INICIANDO CARGA DE IMAGEN ===")
                                            android.util.Log.d("EjercicioDetailScreen", "URL completa: $url")
                                            android.util.Log.d("EjercicioDetailScreen", "URL válida: ${url.startsWith("http")}")
                                            android.util.Log.d("EjercicioDetailScreen", "Es HTTPS: ${url.startsWith("https")}")
                                        }
                                        
                                        // Usar AsyncImage en lugar de rememberAsyncImagePainter para mejor manejo del ciclo de vida
                                        AsyncImage(
                                            model = ImageRequest.Builder(context)
                                                .data(url)
                                                .crossfade(true)
                                                .memoryCachePolicy(CachePolicy.ENABLED)
                                                .diskCachePolicy(CachePolicy.ENABLED)
                                                .networkCachePolicy(CachePolicy.ENABLED)
                                                .listener(
                                                    onStart = { 
                                                        android.util.Log.d("EjercicioDetailScreen", "Coil: Iniciando carga de imagen")
                                                    },
                                                    onSuccess = { _, _ ->
                                                        android.util.Log.d("EjercicioDetailScreen", "Coil: Imagen cargada exitosamente")
                                                    },
                                                    onError = { _, result ->
                                                        android.util.Log.e("EjercicioDetailScreen", "Coil: Error al cargar - ${result.throwable?.message}", result.throwable)
                                                    },
                                                    onCancel = {
                                                        android.util.Log.w("EjercicioDetailScreen", "Coil: Carga cancelada")
                                                    }
                                                )
                                                .build(),
                                            contentDescription = "Visualización del ejercicio",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(300.dp)
                                                .clip(RoundedCornerShape(12.dp)),
                                            contentScale = ContentScale.Fit,
                                            placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                                            error = painterResource(android.R.drawable.ic_menu_report_image),
                                            onLoading = { state ->
                                                android.util.Log.d("EjercicioDetailScreen", "Estado: Loading")
                                            },
                                            onSuccess = { state ->
                                                android.util.Log.d("EjercicioDetailScreen", "Estado: Success")
                                            },
                                            onError = { state ->
                                                val errorMessage = state.result.throwable?.message ?: "Error desconocido"
                                                android.util.Log.e("EjercicioDetailScreen", "Estado: Error - $errorMessage", state.result.throwable)
                                            }
                                        )
                                    }
                                }
                                "Video" -> {
                                    VideoPlayer(videoUrl = url, modifier = Modifier.height(250.dp))
                                }
                            }
                        }
                    }

                    // "Tu objetivo de hoy" - SEGUNDO
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Tu objetivo de hoy:",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkMatterPalette.Highlight
                                )
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    series?.let { Text("$it series", style = MaterialTheme.typography.bodyLarge, color = DarkMatterPalette.PrimaryText) }
                                    repeticiones?.let { Text(it, style = MaterialTheme.typography.bodyLarge, color = DarkMatterPalette.PrimaryText) }
                                    peso?.takeIf { it.isNotBlank() }?.let {
                                        Text("${it}kg", style = MaterialTheme.typography.bodyLarge, color = DarkMatterPalette.PrimaryText)
                                    }
                                    rir?.takeIf { it.isNotBlank() }?.let {
                                        Text("RIR $it", style = MaterialTheme.typography.bodyLarge, color = DarkMatterPalette.PrimaryText)
                                    }
                                }
                            }
                        }
                    }

                    // Descripción del ejercicio - TERCERO
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "Músculo Principal: ${ej.musculoPrincipal}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DarkMatterPalette.Highlight
                            )
                            Text(
                                ej.descripcion,
                                style = MaterialTheme.typography.bodyLarge,
                                color = DarkMatterPalette.PrimaryText
                            )
                        }
                    }

                    if (historialFiltrado.isNotEmpty()) {
                        item {
                            Text(
                                "Historial de Progreso",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp),
                                color = DarkMatterPalette.Highlight
                            )
                        }
                        items(historialFiltrado.sortedByDescending { it.timestamp }) { registro ->
                            HistorialItem(registro = registro)
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            registro.timestamp?.let { timestamp ->
                Text(
                    text = dateFormatter.format(timestamp.toDate()),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = DarkMatterPalette.Highlight
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${registro.series} series", style = MaterialTheme.typography.bodyLarge, color = DarkMatterPalette.PrimaryText)
                Text(registro.repeticiones, style = MaterialTheme.typography.bodyLarge, color = DarkMatterPalette.PrimaryText)
                registro.peso?.let { Text("${it}kg", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = DarkMatterPalette.PrimaryText) }
                registro.rir?.let { Text("RIR ${it}", style = MaterialTheme.typography.bodyLarge, color = DarkMatterPalette.PrimaryText) }
            }
            registro.comentario?.takeIf { it.isNotBlank() }?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.AutoMirrored.Filled.Comment,
                        contentDescription = "Comentario",
                        modifier = Modifier.size(16.dp),
                        tint = DarkMatterPalette.SecondaryText
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkMatterPalette.SecondaryText
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
