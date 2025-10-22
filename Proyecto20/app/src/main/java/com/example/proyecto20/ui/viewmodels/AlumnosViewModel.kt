package com.example.proyecto20.ui.viewmodels

import androidx.lifecycle.*
import com.example.proyecto20.data.FirebaseRepository
import com.example.proyecto20.model.Usuario
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

// --- VERSIÓN DEFINITIVA Y ROBUSTA DEL VIEWMODEL ---

class AlumnosViewModel(private val entrenadorId: String) : ViewModel() {

    // 1. Creamos un StateFlow privado que se suscribe al Flow del repositorio.
    //    Esto es robusto y reacciona a cambios en tiempo real.
    private val _alumnos: StateFlow<List<Usuario>> =
        FirebaseRepository.getAlumnosByEntrenadorFlow(entrenadorId) // <- Llama a la nueva función Flow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // 2. El StateFlow para la búsqueda de texto no cambia.
    val textoBusqueda = MutableStateFlow("")

    // 3. El StateFlow público que la UI observará.
    //    Combina la lista de alumnos con el texto de búsqueda.
    @OptIn(FlowPreview::class)
    val alumnosFiltrados: StateFlow<List<Usuario>> = textoBusqueda
        .debounce(300)
        .combine(_alumnos) { texto, alumnos ->
            if (texto.isBlank()) {
                alumnos
            } else {
                alumnos.filter { it.nombre.contains(texto, ignoreCase = true) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Ya no necesitamos init{} ni cargarAlumnos(). El Flow lo hace todo automático.

    fun onTextoBusquedaChange(nuevoTexto: String) {
        textoBusqueda.value = nuevoTexto
    }
}

// La Factory no cambia, sigue siendo correcta.
class AlumnosViewModelFactory(private val entrenadorId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlumnosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlumnosViewModel(entrenadorId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
