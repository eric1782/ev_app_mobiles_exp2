// Ruta: app/src/main/java/com/example/proyecto20/ui/viewmodels/EjerciciosViewModel.kt

package com.example.proyecto20.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto20.data.FirebaseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EjerciciosViewModel : ViewModel() {

    // Usamos la función de Flow correcta del repositorio
    val ejercicios = FirebaseRepository.getCatalogoEjerciciosFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Usamos la función de suspend correcta del repositorio
    fun addEjercicio(nombre: String, descripcion: String, musculo: String, urlVideo: String) {
        viewModelScope.launch {
            FirebaseRepository.addEjercicio(nombre, descripcion, musculo, urlVideo)
        }
    }
}
