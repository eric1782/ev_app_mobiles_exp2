package com.example.proyecto20.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto20.data.FirebaseRepository
import com.example.proyecto20.model.TipoAlumno
import com.example.proyecto20.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

// Modelo de datos para la UI, puede mostrar un horario o solo el tipo de alumno
data class CitaCalendario(
    val alumnoId: String,
    val nombreAlumno: String,
    val detalle: String, // "10:00 - 11:00" o "Online"
    val tipo: TipoAlumno,
    val numEjercicios: Int = 0 // Número de ejercicios para ese día
)

class CalendarioViewModel(private val entrenadorId: String) : ViewModel() {

    // --- ESTADOS PRINCIPALES ---
    private val _fechaSeleccionada = MutableStateFlow(LocalDate.now())
    val fechaSeleccionada = _fechaSeleccionada.asStateFlow()

    // Guarda TODOS los alumnos del entrenador una sola vez
    private val _todosLosAlumnos = MutableStateFlow<List<Usuario>>(emptyList())

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    // --- FLOW DERIVADO: Las citas para la fecha seleccionada ---
    // Se recalcula automáticamente cuando cambia la fecha o la lista de alumnos
    val citasDelDia = combine(_fechaSeleccionada, _todosLosAlumnos) { fecha, alumnos ->
        filtrarAlumnosPorFecha(fecha, alumnos)
    }

    init {
        // Carga inicial de todos los alumnos
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _todosLosAlumnos.value = FirebaseRepository.getAlumnosByEntrenadorSuspend(entrenadorId)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- ACCIONES DE LA UI ---
    fun cambiarDia(dias: Long) {
        _fechaSeleccionada.value = _fechaSeleccionada.value.plusDays(dias)
    }

    fun seleccionarFecha(fecha: LocalDate) {
        _fechaSeleccionada.value = fecha
    }

    // --- LÓGICA DE FILTRADO ---
    private fun filtrarAlumnosPorFecha(fecha: LocalDate, alumnos: List<Usuario>): List<CitaCalendario> {
        val diaDeLaSemana = fecha.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es", "ES")).uppercase()
        val citas = mutableListOf<CitaCalendario>()

        alumnos.forEach { alumno ->
            // Busca la rutina del día para obtener el número de ejercicios
            val rutinaDelDia = alumno.rutina.find { it.dia.uppercase() == diaDeLaSemana }
            val numEjercicios = rutinaDelDia?.ejercicios?.size ?: 0
            
            when (alumno.tipo) {
                TipoAlumno.PRESENCIAL -> {
                    // Busca si tiene un horario presencial para ese día de la semana
                    val horario = alumno.horariosPresenciales.find { it.dia.uppercase() == diaDeLaSemana }
                    if (horario != null && horario.hora.isNotBlank()) {
                        citas.add(
                            CitaCalendario(
                                alumnoId = alumno.id,
                                nombreAlumno = alumno.nombre,
                                detalle = horario.hora,
                                tipo = TipoAlumno.PRESENCIAL,
                                numEjercicios = numEjercicios
                            )
                        )
                    }
                }
                TipoAlumno.ONLINE -> {
                    // Busca si tiene rutina de entrenamiento para ese día de la semana
                    val tieneRutina = alumno.rutina.any { it.dia.uppercase() == diaDeLaSemana }
                    if (tieneRutina) {
                        citas.add(
                            CitaCalendario(
                                alumnoId = alumno.id,
                                nombreAlumno = alumno.nombre,
                                detalle = "Online",
                                tipo = TipoAlumno.ONLINE,
                                numEjercicios = numEjercicios
                            )
                        )
                    }
                }
            }
        }
        // Ordena las citas: primero los presenciales (que tienen hora) y luego los online
        return citas.sortedWith(compareBy({ it.tipo }, { it.detalle }))
    }

    // Factory para el ViewModel
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
