package com.example.proyecto20.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto20.data.FirebaseRepository
import com.example.proyecto20.model.Ejercicio
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
    fun addEjercicio(nombre: String, descripcion: String, musculo: String, urlVideo: String?) {
        viewModelScope.launch {
            val ejercicio = Ejercicio(
                nombre = nombre,
                descripcion = descripcion,
                musculoPrincipal = musculo,
                urlVideo = urlVideo ?: "" // Aseguramos que no sea nulo al guardar
            )
            // Llamamos a la función del repositorio que guarda en Firestore.
            FirebaseRepository.addEjercicio(ejercicio)
        }
    }
}
