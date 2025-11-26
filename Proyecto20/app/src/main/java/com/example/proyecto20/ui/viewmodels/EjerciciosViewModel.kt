package com.example.proyecto20.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto20.data.FirebaseRepository
import com.example.proyecto20.model.Ejercicio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EjerciciosViewModel : ViewModel() {

    // El StateFlow que obtiene todos los ejercicios del catálogo para mostrarlos en la lista.
    val ejercicios: StateFlow<List<Ejercicio>> = FirebaseRepository.getCatalogoEjerciciosFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- ¡¡LÓGICA AÑADIDA AQUÍ!! ---
    // Esta es la función que será llamada por la pantalla AddEjercicioScreen.
    fun addEjercicio(
        nombre: String,
        descripcion: String,
        musculo: String,
        urlVideo: String? = null,
        urlGif: String? = null,
        urlImagen: String? = null,
        fuenteVideo: String = "manual",
        esDeAPI: Boolean = false
    ) {
        viewModelScope.launch {
            val ejercicio = Ejercicio(
                nombre = nombre,
                descripcion = descripcion,
                musculoPrincipal = musculo,
                urlVideo = urlVideo ?: "",
                urlGif = urlGif ?: "",
                urlImagen = urlImagen ?: "",
                fuenteVideo = fuenteVideo,
                esDeAPI = esDeAPI
            )
            // Debug: Verificar que los campos se están guardando
            android.util.Log.d("EjerciciosViewModel", "Guardando ejercicio: ${ejercicio.nombre}")
            android.util.Log.d("EjerciciosViewModel", "urlGif: ${ejercicio.urlGif}")
            android.util.Log.d("EjerciciosViewModel", "urlImagen: ${ejercicio.urlImagen}")
            android.util.Log.d("EjerciciosViewModel", "urlVideo: ${ejercicio.urlVideo}")
            // Llamamos a la función del repositorio que guarda en Firestore.
            FirebaseRepository.addEjercicio(ejercicio)
        }
    }
}
