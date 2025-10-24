package com.example.proyecto20.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto20.data.FirebaseRepository
import com.example.proyecto20.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

// El nombre correcto que estás usando en tu proyecto
class AlumnosViewModel(private val entrenadorId: String) : ViewModel() {

    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda = _textoBusqueda.asStateFlow()

    private val _alumnos = MutableStateFlow<List<Usuario>>(emptyList())

    // ¡NUEVO! Estado para controlar la pantalla de carga
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    // Cambiamos el nombre de 'alumnosFiltrados' a simplemente 'alumnos'
    // para que coincida con la UI que te proporcioné.
    val alumnos = combine(_alumnos, _textoBusqueda) { alumnos, busqueda ->
        if (busqueda.isBlank()) {
            alumnos
        } else {
            alumnos.filter { it.nombre.contains(busqueda, ignoreCase = true) }
        }
    }

    init {
        cargarAlumnos()
    }

    private fun cargarAlumnos() {
        viewModelScope.launch {
            _isLoading.value = true // Inicia la carga
            try {
                // Usamos la nueva función suspendida
                _alumnos.value = FirebaseRepository.getAlumnosByEntrenadorSuspend(entrenadorId)
            } finally {
                _isLoading.value = false // Finaliza la carga, incluso si hay error
            }
        }
    }

    fun onTextoBusquedaChange(texto: String) {
        _textoBusqueda.value = texto
    }
}

// Cambiamos el nombre aquí también por consistencia
class AlumnosViewModelFactory(private val entrenadorId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlumnosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlumnosViewModel(entrenadorId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
