// Ruta: app/src/main/java/com/example/proyecto20/ui/viewmodels/AlumnoDetailViewModel.kt

package com.example.proyecto20.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto20.data.FirebaseRepository
import com.example.proyecto20.model.Ejercicio
import com.example.proyecto20.model.Usuario
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AlumnoDetailViewModel : ViewModel() {

    private val _alumno = MutableStateFlow<Usuario?>(null)
    val alumno = _alumno.asStateFlow()
    
    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AlumnoDetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AlumnoDetailViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    // Este es el Flow que contiene TODOS los ejercicios del catálogo
    private val catalogoEjercicios: StateFlow<List<Ejercicio>> =
        FirebaseRepository.getCatalogoEjerciciosFlow()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda = _textoBusqueda.asStateFlow()

    // Combinamos el catálogo y la búsqueda para obtener la lista filtrada
    val ejerciciosFiltrados: StateFlow<List<Ejercicio>> =
        combine(catalogoEjercicios, _textoBusqueda) { ejercicios, texto ->
            if (texto.isBlank()) {
                ejercicios
            } else {
                ejercicios.filter { it.nombre.contains(texto, ignoreCase = true) }
            }
        }.stateIn( // Convertimos el resultado en un StateFlow
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun cargarDatosAlumno(alumnoId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _alumno.value = FirebaseRepository.getUsuarioById(alumnoId)
            _isLoading.value = false
        }
    }

    fun onTextoBusquedaChange(nuevoTexto: String) {
        _textoBusqueda.value = nuevoTexto
    }

    // A implementar en el futuro
    fun agregarEjercicioARutina(ejercicio: Ejercicio) { /* ... */ }
    fun quitarEjercicioDeRutina(ejercicioId: String) { /* ... */ }
}
