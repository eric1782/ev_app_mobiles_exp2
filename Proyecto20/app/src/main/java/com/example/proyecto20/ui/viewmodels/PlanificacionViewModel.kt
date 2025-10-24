package com.example.proyecto20.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto20.data.FirebaseRepository
import com.example.proyecto20.model.*
import com.google.firebase.Timestamp // ¡Importante!
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class PlanificacionViewModel(private val alumnoId: String) : ViewModel() {

    private val firestore = Firebase.firestore

    private val _alumno = MutableStateFlow<Usuario?>(null)
    val alumno = _alumno.asStateFlow()

    private val _rutina = MutableStateFlow<Map<String, DiaEntrenamiento>>(emptyMap())
    val rutina = _rutina.asStateFlow()

    private val _horariosPresenciales = MutableStateFlow<List<HorarioPresencial>>(emptyList())
    private val _catalogoEjercicios = MutableStateFlow<List<Ejercicio>>(emptyList())
    val catalogoEjercicios = _catalogoEjercicios.asStateFlow()

    private val _diaSeleccionado = MutableStateFlow("LUNES")
    val diaSeleccionado = _diaSeleccionado.asStateFlow()

    val showDialog = mutableStateOf(false)
    val diaParaGuardar = mutableStateOf<String?>(null)

    val horaDelDiaSeleccionado: StateFlow<String?> = combine(
        diaSeleccionado,
        _horariosPresenciales
    ) { dia, horarios ->
        horarios.find { it.dia == dia }?.hora
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)


    init {
        viewModelScope.launch {
            FirebaseRepository.getUsuarioFlow(alumnoId).collect { usuarioActualizado ->
                _alumno.value = usuarioActualizado
                _horariosPresenciales.value = usuarioActualizado?.horariosPresenciales ?: emptyList()
                val rutinaMapa = usuarioActualizado?.rutina?.associateBy { it.dia } ?: emptyMap()
                val diasOrdenados = listOf("LUNES", "MARTES", "MIÉRCOLES", "JUEVES", "VIERNES", "SÁBADO", "DOMINGO")
                _rutina.value = rutinaMapa.entries.sortedBy { diasOrdenados.indexOf(it.key) }.associate { it.toPair() }
            }
        }
        viewModelScope.launch {
            FirebaseRepository.getCatalogoEjerciciosFlow().collect { ejercicios ->
                _catalogoEjercicios.value = ejercicios
            }
        }
    }

    fun seleccionarDia(dia: String) { _diaSeleccionado.value = dia }

    fun onRegistrarHitoDiaClicked(dia: String) {
        diaParaGuardar.value = dia
        showDialog.value = true
    }

    // Esta función ya está correcta y es la que soluciona el error de compilación.
    fun registrarHitoDeProgresoPorDia(comentario: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val diaAGuardar = diaParaGuardar.value ?: run {
                    withContext(Dispatchers.Main) { onResult(false) }; return@launch
                }
                val diaEntrenamiento = _rutina.value[diaAGuardar] ?: run {
                    withContext(Dispatchers.Main) { onResult(false) }; return@launch
                }
                if (diaEntrenamiento.ejercicios.isEmpty()) {
                    withContext(Dispatchers.Main) { onResult(false) }; return@launch
                }

                val batch = firestore.batch()
                val historialRef = firestore.collection("usuarios").document(alumnoId).collection("historial_progreso")

                diaEntrenamiento.ejercicios.forEach { ejercicio ->
                    val nuevoRegistro = RegistroProgreso(
                        ejercicioId = ejercicio.ejercicioId,
                        ejercicioNombre = ejercicio.nombre,
                        series = ejercicio.series,
                        repeticiones = ejercicio.repeticiones,
                        peso = ejercicio.peso,
                        rir = ejercicio.rir,
                        comentario = comentario.takeIf { it.isNotBlank() },
                        timestamp = Timestamp.now() // <--- ¡Esta línea es la clave y ya está bien!
                    )
                    batch.set(historialRef.document(), nuevoRegistro)
                }

                batch.commit().await()
                withContext(Dispatchers.Main) {
                    onResult(true)
                    showDialog.value = false
                    diaParaGuardar.value = null
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }

    // El resto de las funciones que ya tenías
    fun agregarEjercicioARutina(ejercicio: Ejercicio, dia: String) {
        val nuevoEjercicioRutina = EjercicioRutina(ejercicioId = ejercicio.id, nombre = ejercicio.nombre, series = 3, repeticiones = "10-12")
        val rutinaActual = _rutina.value.toMutableMap()
        val diaActual = rutinaActual[dia]
        if (diaActual == null) {
            rutinaActual[dia] = DiaEntrenamiento(dia = dia, ejercicios = listOf(nuevoEjercicioRutina))
        } else {
            val ejerciciosActualizados = diaActual.ejercicios.toMutableList().apply { add(nuevoEjercicioRutina) }
            rutinaActual[dia] = diaActual.copy(ejercicios = ejerciciosActualizados)
        }
        _rutina.value = rutinaActual
    }

    fun eliminarEjercicioDeRutina(ejercicioId: String, dia: String) {
        val rutinaActual = _rutina.value.toMutableMap()
        val diaActual = rutinaActual[dia] ?: return
        val ejerciciosActualizados = diaActual.ejercicios.filterNot { it.ejercicioId == ejercicioId }
        if (ejerciciosActualizados.isEmpty()) {
            rutinaActual.remove(dia)
        } else {
            rutinaActual[dia] = diaActual.copy(ejercicios = ejerciciosActualizados)
        }
        _rutina.value = rutinaActual
    }

    fun actualizarDetallesEjercicio(ejercicioActualizado: EjercicioRutina, dia: String) {
        val rutinaActual = _rutina.value.toMutableMap()
        val diaActual = rutinaActual[dia] ?: return
        val ejerciciosActualizados = diaActual.ejercicios.map { if (it.ejercicioId == ejercicioActualizado.ejercicioId) ejercicioActualizado else it }
        rutinaActual[dia] = diaActual.copy(ejercicios = ejerciciosActualizados)
        _rutina.value = rutinaActual
    }

    fun guardarRutinaCompleta(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val rutinaParaGuardar = _rutina.value.values.toList()
                FirebaseRepository.guardarRutinaDeAlumno(alumnoId, rutinaParaGuardar)
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun asignarHoraPresencial(dia: String, hora: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                FirebaseRepository.asignarHoraPresencial(alumnoId, dia, hora)
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

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
