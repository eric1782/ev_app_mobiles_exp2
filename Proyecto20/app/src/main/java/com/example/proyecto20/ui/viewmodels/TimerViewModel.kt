package com.example.proyecto20.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import android.content.Context
import com.example.proyecto20.model.Timer
import com.example.proyecto20.model.TimerPhase
import com.example.proyecto20.model.TimerState
import com.example.proyecto20.utils.TimerSoundManager
import com.example.proyecto20.utils.AppNotificationManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class TimerViewModel(private val context: Context? = null) : ViewModel() {

    private val _temporizadores = MutableStateFlow<List<Timer>>(emptyList())
    val temporizadores = _temporizadores.asStateFlow()

    private val jobsActivos = mutableMapOf<String, Job>()
    private val soundManager = TimerSoundManager()
    private val notificationManager = context?.let { AppNotificationManager(it) }

    // --- CREAR TEMPORIZADOR ---
    fun crearTemporizador(
        nombre: String,
        tiempoTrabajoMinutos: Int,
        tiempoTrabajoSegundos: Int,
        tiempoDescansoSegundos: Int,
        repeticiones: Int
    ) {
        val tiempoTrabajoTotalSegundos = tiempoTrabajoMinutos * 60 + tiempoTrabajoSegundos
        
        val nuevoTimer = Timer(
            id = UUID.randomUUID().toString(),
            nombre = nombre,
            tiempoTrabajoSegundos = tiempoTrabajoTotalSegundos,
            tiempoDescansoSegundos = tiempoDescansoSegundos,
            repeticiones = repeticiones,
            tiempoRestanteSegundos = tiempoTrabajoTotalSegundos
        )

        _temporizadores.value = _temporizadores.value + nuevoTimer
    }

    // --- INICIAR TEMPORIZADOR ---
    fun iniciarTemporizador(timerId: String) {
        val timer = _temporizadores.value.find { it.id == timerId } ?: return
        
        if (timer.estado == TimerState.IDLE) {
            // Si está en IDLE, empezar con fase PREPARE (5 segundos)
            actualizarTimer(timerId) { timerActualizado ->
                timerActualizado.copy(
                    estado = TimerState.RUNNING,
                    faseActual = TimerPhase.PREPARE,
                    tiempoRestanteSegundos = 5,
                    tiempoInicioPausa = null
                )
            }
            iniciarConteo(timerId)
        } else if (timer.estado == TimerState.PAUSED) {
            // Si está pausado, reanudar desde donde estaba
            actualizarTimer(timerId) { timerActualizado ->
                timerActualizado.copy(
                    estado = TimerState.RUNNING,
                    tiempoInicioPausa = null
                )
            }
            iniciarConteo(timerId)
        }
    }

    // --- PAUSAR TEMPORIZADOR ---
    fun pausarTemporizador(timerId: String) {
        val timer = _temporizadores.value.find { it.id == timerId } ?: return
        
        if (timer.estado == TimerState.RUNNING) {
            jobsActivos[timerId]?.cancel()
            jobsActivos.remove(timerId)
            
            actualizarTimer(timerId) { timerActualizado ->
                timerActualizado.copy(
                    estado = TimerState.PAUSED,
                    tiempoInicioPausa = System.currentTimeMillis()
                )
            }
        }
    }

    // --- REANUDAR TEMPORIZADOR ---
    fun reanudarTemporizador(timerId: String) {
        val timer = _temporizadores.value.find { it.id == timerId } ?: return
        
        if (timer.estado == TimerState.PAUSED) {
            actualizarTimer(timerId) { timerActualizado ->
                timerActualizado.copy(
                    estado = TimerState.RUNNING,
                    tiempoInicioPausa = null
                )
            }
            iniciarConteo(timerId)
        }
    }

    // --- ELIMINAR TEMPORIZADOR ---
    fun eliminarTemporizador(timerId: String) {
        jobsActivos[timerId]?.cancel()
        jobsActivos.remove(timerId)
        _temporizadores.value = _temporizadores.value.filter { it.id != timerId }
    }

    // --- LÓGICA DE CONTEO ---
    private fun iniciarConteo(timerId: String) {
        val job = viewModelScope.launch {
            while (true) {
                delay(1000) // Actualizar cada segundo
                
                val timer = _temporizadores.value.find { it.id == timerId } ?: break
                
                if (timer.estado != TimerState.RUNNING) break
                
                val nuevoTiempoRestante = timer.tiempoRestanteSegundos - 1
                
                // Reproducir sonidos de cuenta regresiva
                when (nuevoTiempoRestante) {
                    3, 2, 1 -> {
                        // Sonido de cuenta regresiva para los últimos 3 segundos
                        soundManager.playCountdownSound()
                    }
                    in 4..5 -> {
                        // Sonido de advertencia para los últimos 5 segundos (excepto 3,2,1)
                        if (nuevoTiempoRestante == 4) {
                            soundManager.playWarningSound()
                        }
                    }
                }
                
                if (nuevoTiempoRestante <= 0) {
                    // El intervalo actual terminó
                    manejarFinIntervalo(timerId)
                } else {
                    actualizarTimer(timerId) { timerActualizado ->
                        timerActualizado.copy(tiempoRestanteSegundos = nuevoTiempoRestante)
                    }
                }
            }
        }
        
        jobsActivos[timerId] = job
    }

    private fun manejarFinIntervalo(timerId: String) {
        val timer = _temporizadores.value.find { it.id == timerId } ?: return
        
        when (timer.faseActual) {
            TimerPhase.PREPARE -> {
                // Terminó la preparación, pasar a trabajo
                soundManager.playStartSound() // Sonido de inicio
                actualizarTimer(timerId) { timerActualizado ->
                    timerActualizado.copy(
                        faseActual = TimerPhase.WORK,
                        tiempoRestanteSegundos = timer.tiempoTrabajoSegundos
                    )
                }
                // El conteo continúa automáticamente
            }
            TimerPhase.WORK -> {
                // Terminó el trabajo, pasar a descanso
                val nuevasRepeticionesCompletadas = timer.repeticionesCompletadas + 1
                
                if (nuevasRepeticionesCompletadas >= timer.repeticiones) {
                    // Se completaron todas las rondas
                    soundManager.playCompleteSound() // Sonido de completado
                    notificationManager?.showTimerCompletedNotification(timer.nombre) // Notificación
                    jobsActivos[timerId]?.cancel()
                    jobsActivos.remove(timerId)
                    
                    actualizarTimer(timerId) { timerActualizado ->
                        timerActualizado.copy(
                            estado = TimerState.COMPLETED,
                            repeticionesCompletadas = timer.repeticiones,
                            tiempoRestanteSegundos = 0
                        )
                    }
                } else {
                    // Aún quedan rondas, pasar a descanso
                    soundManager.playEndIntervalSound() // Sonido de fin de intervalo
                    notificationManager?.showTimerIntervalNotification(timer.nombre, isWork = true) // Notificación
                    actualizarTimer(timerId) { timerActualizado ->
                        timerActualizado.copy(
                            faseActual = TimerPhase.REST,
                            tiempoRestanteSegundos = timer.tiempoDescansoSegundos,
                            repeticionesCompletadas = nuevasRepeticionesCompletadas
                        )
                    }
                    // El conteo continúa automáticamente
                }
            }
            TimerPhase.REST -> {
                // Terminó el descanso, pasar a trabajo
                notificationManager?.showTimerIntervalNotification(timer.nombre, isWork = false) // Notificación
                soundManager.playStartSound() // Sonido de inicio de nueva ronda
                actualizarTimer(timerId) { timerActualizado ->
                    timerActualizado.copy(
                        faseActual = TimerPhase.WORK,
                        tiempoRestanteSegundos = timer.tiempoTrabajoSegundos
                    )
                }
                // El conteo continúa automáticamente
            }
        }
    }

    // --- ACTUALIZAR TIMER EN LA LISTA ---
    private fun actualizarTimer(timerId: String, actualizacion: (Timer) -> Timer) {
        _temporizadores.value = _temporizadores.value.map { timer ->
            if (timer.id == timerId) {
                actualizacion(timer)
            } else {
                timer
            }
        }
    }

    // --- LIMPIAR AL DESTRUIR EL VIEWMODEL ---
    override fun onCleared() {
        super.onCleared()
        jobsActivos.values.forEach { it.cancel() }
        jobsActivos.clear()
        soundManager.release()
    }

    // Factory para el ViewModel
    class Factory(private val context: Context? = null) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TimerViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

