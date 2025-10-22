package com.example.proyecto20.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto20.data.FirebaseRepository
import com.example.proyecto20.model.BloqueHorario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

// ViewModel que recibe el ID del entrenador
class CalendarioViewModel(private val entrenadorId: String) : ViewModel() {

    // StateFlow para la fecha seleccionada. Comienza con la fecha de hoy.
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    // StateFlow para la lista de citas (Bloques Horarios)
    private val _citas = MutableStateFlow<List<BloqueHorario>>(emptyList())
    val citas = _citas.asStateFlow()

    init {
        // Al iniciar, carga todas las citas del entrenador.
        cargarCitasDelEntrenador()
    }

    private fun cargarCitasDelEntrenador() {
        viewModelScope.launch {
            // Usamos el repositorio para obtener los horarios.
            FirebaseRepository.getHorariosEntrenador(entrenadorId).collect { listaHorarios ->
                _citas.value = listaHorarios
            }
        }
    }

    // Función pública para que la UI pueda cambiar la fecha seleccionada.
    fun onDateSelected(newDate: LocalDate) {
        _selectedDate.value = newDate
    }

    // Factory para poder pasar el 'entrenadorId' al crear el ViewModel.
    // Esto es crucial y ya lo teníamos, pero es bueno confirmarlo.
    class Factory(private val entrenadorId: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CalendarioViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CalendarioViewModel(entrenadorId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
