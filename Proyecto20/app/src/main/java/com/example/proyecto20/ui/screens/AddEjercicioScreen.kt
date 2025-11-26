package com.example.proyecto20.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.proyecto20.data.FirebaseStorageService
import com.example.proyecto20.data.api.Foto
import com.example.proyecto20.model.Ejercicio
import com.example.proyecto20.ui.viewmodels.BuscadorImagenesViewModel
import com.example.proyecto20.ui.viewmodels.EjerciciosViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEjercicioScreen(
    ejercicioInicial: Ejercicio? = null,
    entrenadorId: String,
    onNavigateBack: () -> Unit,
    onSave: (
        nombre: String,
        descripcion: String,
        musculo: String,
        urlVideo: String?,
        urlGif: String?,
        urlImagen: String?,
        fuenteVideo: String,
        esDeAPI: Boolean
    ) -> Unit
) {
    val viewModel: EjerciciosViewModel = viewModel()
    val buscadorImagenesViewModel: BuscadorImagenesViewModel = viewModel()
    val scope = rememberCoroutineScope()
    
    // Estados del formulario
    var nombre by remember { mutableStateOf(ejercicioInicial?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(ejercicioInicial?.descripcion ?: "") }
    var musculo by remember { mutableStateOf(ejercicioInicial?.musculoPrincipal ?: "") }
    var urlVideo by remember { mutableStateOf(ejercicioInicial?.urlVideo ?: "") }
    var urlGif by remember { mutableStateOf(ejercicioInicial?.urlGif ?: "") }
    var urlImagen by remember { mutableStateOf(ejercicioInicial?.urlImagen ?: "") }
    var fuenteVideo by remember { mutableStateOf(ejercicioInicial?.fuenteVideo?.ifBlank { "manual" } ?: "manual") }
    var esDeAPI by remember { mutableStateOf(ejercicioInicial?.esDeAPI ?: false) }
    
    // Estados para UI
    var isUploading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var mostrarBuscadorImagenes by remember { mutableStateOf(false) }
    var terminoBusquedaImagenes by remember { mutableStateOf("") }
    
    // Estados del buscador de imágenes
    val fotos by buscadorImagenesViewModel.fotos.collectAsState()
    val estaCargandoImagenes by buscadorImagenesViewModel.estaCargando.collectAsState()
    val errorImagenes by buscadorImagenesViewModel.error.collectAsState()
    
    // Launcher para seleccionar archivo
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                isUploading = true
                errorMessage = null
                try {
                    val downloadUrl = FirebaseStorageService.uploadVideoOrGif(it, entrenadorId)
                    // Determinar si es video o imagen basado en la extensión
                    val fileName = it.toString().lowercase()
                    when {
                        fileName.contains(".gif") -> {
                            urlGif = downloadUrl
                            fuenteVideo = "upload"
                        }
                        fileName.contains(".mp4") || fileName.contains(".mov") || fileName.contains(".avi") -> {
                            urlVideo = downloadUrl
                            fuenteVideo = "upload"
                        }
                        else -> {
                            urlImagen = downloadUrl
                            fuenteVideo = "upload"
                        }
                    }
                } catch (e: Exception) {
                    errorMessage = "Error al subir archivo: ${e.message}"
                } finally {
                    isUploading = false
                }
            }
        }
    }
    
    val isFormValid = nombre.isNotBlank() && descripcion.isNotBlank() && musculo.isNotBlank()
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = DarkMatterPalette.PrimaryText,
        unfocusedTextColor = DarkMatterPalette.PrimaryText,
        focusedLabelColor = DarkMatterPalette.Highlight,
        unfocusedLabelColor = DarkMatterPalette.SecondaryText,
        focusedBorderColor = DarkMatterPalette.Highlight,
        unfocusedBorderColor = DarkMatterPalette.SecondaryText,
        cursorColor = DarkMatterPalette.Highlight,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent
    )
    
    DarkMatterBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                DarkMatterTopBar(
                    title = if (ejercicioInicial != null) "Editar Ejercicio" else "Añadir Ejercicio",
                    onNavigateBack = onNavigateBack
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text("GUARDAR") },
                    icon = { Icon(Icons.Filled.Save, contentDescription = "Guardar") },
                    onClick = {
                        if (isFormValid) {
                            onSave(
                                nombre,
                                descripcion,
                                musculo,
                                urlVideo.ifBlank { null },
                                urlGif.ifBlank { null },
                                urlImagen.ifBlank { null },
                                fuenteVideo,
                                esDeAPI
                            )
                        }
                    },
                    containerColor = DarkMatterPalette.Highlight,
                    contentColor = Color.Black
                )
            },
            floatingActionButtonPosition = FabPosition.Center
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Campos básicos
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del Ejercicio*") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = textFieldColors
                )
                OutlinedTextField(
                    value = musculo,
                    onValueChange = { musculo = it },
                    label = { Text("Músculo Principal*") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = textFieldColors
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción / Cómo realizarlo*") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    colors = textFieldColors
                )
                
                // Sección de Video/GIF
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2C2C2C)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Imagen/Video/GIF",
                            color = DarkMatterPalette.Highlight,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        // Preview (prioridad: GIF > Imagen > Video)
                        val previewUrl = when {
                            urlGif.isNotBlank() -> urlGif
                            urlImagen.isNotBlank() -> urlImagen
                            urlVideo.isNotBlank() -> urlVideo
                            else -> null
                        }
                        
                        if (previewUrl != null) {
                            val context = LocalContext.current
                            
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = ImageRequest.Builder(context)
                                            .data(previewUrl)
                                            .build()
                                    ),
                                    contentDescription = "Preview",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentScale = ContentScale.Fit
                                )
                                
                                // Botón para limpiar la imagen/video seleccionado
                                IconButton(
                                    onClick = {
                                        urlGif = ""
                                        urlImagen = ""
                                        urlVideo = ""
                                    },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Eliminar",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .background(
                                                Color.Black.copy(alpha = 0.6f),
                                                shape = RoundedCornerShape(50)
                                            )
                                            .padding(4.dp)
                                    )
                                }
                            }
                        }
                        
                        // Botón de acción
                        Button(
                            onClick = { filePickerLauncher.launch("*/*") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isUploading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            if (isUploading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White
                                )
                            } else {
                                Icon(Icons.Default.Upload, contentDescription = null)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Subir Video/GIF")
                        }
                        
                        // URL de imagen (opcional)
                        OutlinedTextField(
                            value = urlImagen,
                            onValueChange = { 
                                urlImagen = it
                            },
                            label = { Text("URL de la imagen (Opcional)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = textFieldColors,
                            placeholder = { Text("O escribe la URL manualmente") },
                            enabled = urlGif.isBlank() && urlVideo.isBlank()
                        )
                        
                        // URL de video (opcional)
                        OutlinedTextField(
                            value = urlVideo,
                            onValueChange = { 
                                urlVideo = it
                                if (it.isNotBlank()) fuenteVideo = "manual"
                            },
                            label = { Text("URL del video (Opcional)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = textFieldColors,
                            placeholder = { Text("O escribe la URL manualmente") },
                            enabled = urlGif.isBlank() && urlImagen.isBlank()
                        )
                        
                        // Mensaje de error
                        errorMessage?.let {
                            Text(
                                it,
                                color = Color.Red,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                
                // Sección de Búsqueda de Imágenes
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2C2C2C)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Buscar Imagen",
                                color = DarkMatterPalette.Highlight,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = { mostrarBuscadorImagenes = !mostrarBuscadorImagenes }
                            ) {
                                Icon(
                                    if (mostrarBuscadorImagenes) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (mostrarBuscadorImagenes) "Ocultar" else "Mostrar",
                                    tint = DarkMatterPalette.Highlight
                                )
                            }
                        }
                        
                        if (mostrarBuscadorImagenes) {
                            // Campo de búsqueda
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = terminoBusquedaImagenes,
                                    onValueChange = { terminoBusquedaImagenes = it },
                                    label = { Text("Buscar imagen (ej: push up, squat)") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    colors = textFieldColors,
                                    trailingIcon = {
                                        if (estaCargandoImagenes) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                color = DarkMatterPalette.Highlight
                                            )
                                        }
                                    }
                                )
                                Button(
                                    onClick = {
                                        if (terminoBusquedaImagenes.isNotBlank()) {
                                            buscadorImagenesViewModel.buscarImagenes(terminoBusquedaImagenes)
                                        }
                                    },
                                    enabled = !estaCargandoImagenes && terminoBusquedaImagenes.isNotBlank()
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = null)
                                }
                            }
                            
                            // Mensaje de error
                            errorImagenes?.let {
                                Text(
                                    it,
                                    color = Color.Red,
                                    fontSize = 12.sp
                                )
                            }
                            
                            // Grid de imágenes
                            if (fotos.isNotEmpty()) {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.height(400.dp)
                                ) {
                                    items(fotos) { foto ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(150.dp)
                                                .clickable {
                                                    // Usar la URL large para mejor calidad, o medium si no está disponible
                                                    urlImagen = foto.src.large.ifBlank { foto.src.medium }
                                                    mostrarBuscadorImagenes = false
                                                },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            val context = LocalContext.current
                                            Image(
                                                painter = rememberAsyncImagePainter(
                                                    model = ImageRequest.Builder(context)
                                                        .data(foto.src.medium)
                                                        .build()
                                                ),
                                                contentDescription = "Imagen de ejercicio",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }
                                }
                            } else if (!estaCargandoImagenes && terminoBusquedaImagenes.isBlank()) {
                                Text(
                                    "Escribe un término de búsqueda y presiona buscar",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
