package com.example.proyecto20.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto20.data.FirebaseRepository
import com.example.proyecto20.model.DiaEntrenamiento
import com.example.proyecto20.model.Ejercicio
import com.example.proyecto20.model.EjercicioRutina
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PlanificacionViewModel(private val alumnoId: String) : ViewModel() {

    // --- ESTADOS PÚBLICOS PARA LA UI ---
    private val _rutina = MutableStateFlow<List<DiaEntrenamiento>>(emptyList())
    val rutina = _rutina.asStateFlow()

    private val _catalogoEjercicios = MutableStateFlow<List<Ejercicio>>(emptyList())
    val catalogoEjercicios = _catalogoEjercicios.asStateFlow()

    private val _diaSeleccionado = MutableStateFlow<String?>(null)
    val diaSeleccionado = _diaSeleccionado.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    // Este Flow derivado es el que usa la pantalla para mostrar los ejercicios del día.
    val ejerciciosDelDiaSeleccionado = combine(rutina, diaSeleccionado) { rutinaActual, dia ->
        rutinaActual.find { it.dia == dia }?.ejercicios ?: emptyList()
    }

    init {
        viewModelScope.launch {
            FirebaseRepository.getRutinaDeAlumnoFlow(alumnoId).collect { _rutina.value = it }
        }
        viewModelScope.launch {
            FirebaseRepository.getCatalogoEjerciciosFlow().collect { _catalogoEjercicios.value = it }
        }
    }

    // --- ACCIONES DESDE LA UI ---

    fun seleccionarDia(dia: String) {
        _diaSeleccionado.value = dia
    }

    fun anadirEjercicioARutina(ejercicio: Ejercicio) {
        val dia = _diaSeleccionado.value ?: return
        val nuevoEjercicioRutina = EjercicioRutina(ejercicioId = ejercicio.id, nombre = ejercicio.nombre)

        val rutinaActualizada = _rutina.value.toMutableList()
        val diaAActualizar = rutinaActualizada.find { it.dia == dia }

        if (diaAActualizar != null) {
            val ejerciciosActualizados = diaAActualizar.ejercicios.toMutableList().apply { add(nuevoEjercicioRutina) }
            val indiceDia = rutinaActualizada.indexOf(diaAActualizar)
            rutinaActualizada[indiceDia] = diaAActualizar.copy(ejercicios = ejerciciosActualizados)
        } else {
            rutinaActualizada.add(DiaEntrenamiento(dia = dia, ejercicios = listOf(nuevoEjercicioRutina)))
        }
        _rutina.value = rutinaActualizada
    }

    fun eliminarEjercicioDeRutina(ejercicioId: String) {
        val dia = _diaSeleccionado.value ?: return
        _rutina.value = _rutina.value.map { diaEntrenamiento ->
            if (diaEntrenamiento.dia == dia) {
                diaEntrenamiento.copy(ejercicios = diaEntrenamiento.ejercicios.filterNot { it.ejercicioId == ejercicioId })
            } else {
                diaEntrenamiento
            }
        }
    }

    fun guardarRutina() {
        viewModelScope.launch {
            _isSaving.value = true
            FirebaseRepository.guardarRutinaDeAlumno(alumnoId, _rutina.value)
            _isSaving.value = false
        }
    }

    // --- FACTORY ---
    class Factory(private val alumnoId: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlanificacionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PlanificacionViewModel(alumnoId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
