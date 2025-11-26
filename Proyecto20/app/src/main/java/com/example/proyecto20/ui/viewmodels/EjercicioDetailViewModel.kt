package com.example.proyecto20.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto20.data.FirebaseRepository
import com.example.proyecto20.model.Ejercicio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EjercicioDetailViewModel(ejercicioId: String) : ViewModel() {

    private val _ejercicio = MutableStateFlow<Ejercicio?>(null)
    val ejercicio = _ejercicio.asStateFlow()

    init {
        viewModelScope.launch {
            val ejercicioObtenido = FirebaseRepository.getEjercicioById(ejercicioId)
            // Debug: Verificar que los campos se están recuperando
            ejercicioObtenido?.let {
                android.util.Log.d("EjercicioDetailViewModel", "Ejercicio obtenido: ${it.nombre}")
                android.util.Log.d("EjercicioDetailViewModel", "urlGif: ${it.urlGif}")
                android.util.Log.d("EjercicioDetailViewModel", "urlImagen: ${it.urlImagen}")
                android.util.Log.d("EjercicioDetailViewModel", "urlVideo: ${it.urlVideo}")
            }
            _ejercicio.value = ejercicioObtenido
        }
    }

    // --- ¡CORRECCIÓN! Esta función debe estar aquí ---
    fun updateEjercicio(ejercicioActualizado: Ejercicio) {
        viewModelScope.launch {
            FirebaseRepository.updateEjercicio(ejercicioActualizado.id, ejercicioActualizado)
            // Actualizamos el estado local para que la pantalla de detalle refleje los cambios al volver
            _ejercicio.value = ejercicioActualizado
        }
    }
}

// Factory para poder pasar el 'ejercicioId' al constructor del ViewModel
class EjercicioDetailViewModelFactory(private val ejercicioId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EjercicioDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EjercicioDetailViewModel(ejercicioId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class para EjercicioDetailViewModel")
    }
}
