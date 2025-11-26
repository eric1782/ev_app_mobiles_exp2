package com.example.proyecto20.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto20.data.api.*
import com.example.proyecto20.model.DiaEntrenamiento
import com.example.proyecto20.model.EjercicioRutina
import com.example.proyecto20.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

/**
 * ViewModel para calcular calorías quemadas en ejercicios
 */
class CalculadoraCaloriasViewModel : ViewModel() {
    
    private val _caloriasPorDia = MutableStateFlow<Map<String, InformacionCaloriasDia>>(emptyMap())
    val caloriasPorDia: StateFlow<Map<String, InformacionCaloriasDia>> = _caloriasPorDia.asStateFlow()
    
    private val _estaCargando = MutableStateFlow(false)
    val estaCargando: StateFlow<Boolean> = _estaCargando.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    /**
     * Calcula las calorías para todos los días de entrenamiento de un alumno
     */
    fun calcularCaloriasRutina(rutina: List<DiaEntrenamiento>, usuario: Usuario) {
        viewModelScope.launch {
            _estaCargando.value = true
            _error.value = null
            
            try {
                val pesoUsuario = usuario.peso ?: 70.0 // Peso por defecto si no está disponible
                val caloriasMap = mutableMapOf<String, InformacionCaloriasDia>()
                
                rutina.forEach { diaEntrenamiento ->
                    val caloriasDia = calcularCaloriasDia(diaEntrenamiento, pesoUsuario)
                    caloriasMap[diaEntrenamiento.dia] = caloriasDia
                }
                
                _caloriasPorDia.value = caloriasMap
            } catch (e: Exception) {
                Log.e("CalculadoraCaloriasViewModel", "Error al calcular calorías", e)
                _error.value = "Error al calcular calorías: ${e.message}"
            } finally {
                _estaCargando.value = false
            }
        }
    }
    
    /**
     * Calcula las calorías para un día específico de entrenamiento
     */
    private suspend fun calcularCaloriasDia(
        diaEntrenamiento: DiaEntrenamiento,
        pesoUsuario: Double
    ): InformacionCaloriasDia {
        val ejerciciosCalorias = mutableListOf<InformacionCaloriasEjercicio>()
        var caloriasTotalesDia = 0.0
        
        diaEntrenamiento.ejercicios.forEach { ejercicioRutina ->
            val caloriasEjercicio = calcularCaloriasEjercicio(ejercicioRutina, pesoUsuario)
            ejerciciosCalorias.add(caloriasEjercicio)
            caloriasTotalesDia += caloriasEjercicio.caloriasTotales
        }
        
        return InformacionCaloriasDia(
            dia = diaEntrenamiento.dia,
            caloriasTotales = caloriasTotalesDia,
            ejercicios = ejerciciosCalorias
        )
    }
    
    /**
     * Calcula las calorías para un ejercicio específico
     */
    private suspend fun calcularCaloriasEjercicio(
        ejercicioRutina: EjercicioRutina,
        pesoUsuario: Double
    ): InformacionCaloriasEjercicio {
        // Buscar el ejercicio en la API para obtener información
        val nombreEjercicio = ejercicioRutina.nombre
        var met = obtenerMetEjercicio(nombreEjercicio)
        
        // Ajustar MET según el peso levantado (ejercicios con más peso queman más calorías)
        ejercicioRutina.peso?.let { pesoLevantado ->
            if (pesoLevantado > 0) {
                // Aumentar MET proporcionalmente al peso levantado
                // Ejemplo: si levanta 50kg vs 20kg, aumenta el MET en un 20-30%
                val factorPeso = 1.0 + (pesoLevantado / pesoUsuario) * 0.3
                met *= factorPeso.coerceIn(1.0, 1.5) // Limitar el aumento al 50%
            }
        }
        
        // Calcular duración estimada
        val duracionMinutos = calcularDuracionEjercicio(ejercicioRutina)
        
        // Calcular calorías usando la fórmula: Calorías = MET × peso(kg) × tiempo(horas)
        val caloriasPorMinuto = (met * pesoUsuario) / 60.0
        val caloriasTotales = caloriasPorMinuto * duracionMinutos
        
        return InformacionCaloriasEjercicio(
            nombreEjercicio = nombreEjercicio,
            caloriasPorMinuto = caloriasPorMinuto,
            caloriasTotales = caloriasTotales,
            duracionMinutos = duracionMinutos,
            met = met
        )
    }
    
    /**
     * Obtiene el valor MET (Metabolic Equivalent) para un ejercicio
     * Hace matching flexible del nombre del ejercicio
     */
    private suspend fun obtenerMetEjercicio(nombreEjercicio: String): Double {
        // Primero intentar buscar en la API
        try {
            val respuesta = ClienteApiEjercicios.servicioApiEjercicios.buscarEjercicios(
                nombre = extraerPalabrasClave(nombreEjercicio),
                idioma = 2, // Español
                limite = 5
            )
            
            if (respuesta.isSuccessful && respuesta.body()?.suggestions?.isNotEmpty() == true) {
                // Si encontramos el ejercicio, usar un MET promedio para ejercicios de fuerza
                return obtenerMetPorTipoEjercicio(nombreEjercicio)
            }
        } catch (e: Exception) {
            Log.w("CalculadoraCaloriasViewModel", "Error al buscar ejercicio en API: ${e.message}")
        }
        
        // Si no se encuentra en la API, usar matching local por palabras clave
        return obtenerMetPorPalabrasClave(nombreEjercicio)
    }
    
    /**
     * Extrae palabras clave del nombre del ejercicio para la búsqueda
     */
    private fun extraerPalabrasClave(nombreEjercicio: String): String {
        // Convertir a minúsculas y extraer palabras principales
        val palabras = nombreEjercicio.lowercase()
            .split(" ", ",", "-", "con", "de", "en")
            .filter { it.length > 2 }
        
        // Retornar las primeras 2-3 palabras más relevantes
        return palabras.take(2).joinToString(" ")
    }
    
    /**
     * Obtiene MET por tipo de ejercicio basado en palabras clave
     */
    private fun obtenerMetPorPalabrasClave(nombreEjercicio: String): Double {
        val nombreLower = nombreEjercicio.lowercase()
        
        // Ejercicios de fuerza/pesas (MET más alto)
        val ejerciciosFuerza = listOf("sentadilla", "squat", "peso muerto", "deadlift", "press", 
            "press banca", "bench press", "remo", "row", "curl", "biceps", "triceps", 
            "pierna", "leg", "hombro", "shoulder", "pecho", "chest", "espalda", "back")
        
        // Ejercicios cardiovasculares (MET medio-alto)
        val ejerciciosCardio = listOf("correr", "running", "caminar", "walking", "bicicleta", 
            "bike", "cardio", "aerobico", "aeróbico")
        
        // Ejercicios de calistenia (MET medio)
        val ejerciciosCalistenia = listOf("flexion", "push-up", "pull-up", "dominada", 
            "abdominal", "crunch", "plancha", "plank")
        
        return when {
            ejerciciosFuerza.any { nombreLower.contains(it) } -> 6.0 // MET para ejercicios de fuerza
            ejerciciosCardio.any { nombreLower.contains(it) } -> 8.0 // MET para cardio
            ejerciciosCalistenia.any { nombreLower.contains(it) } -> 5.0 // MET para calistenia
            else -> 5.5 // MET por defecto (ejercicio moderado)
        }
    }
    
    /**
     * Obtiene MET por tipo de ejercicio (método alternativo)
     */
    private fun obtenerMetPorTipoEjercicio(nombreEjercicio: String): Double {
        // Similar a obtenerMetPorPalabrasClave pero con valores más específicos
        return obtenerMetPorPalabrasClave(nombreEjercicio)
    }
    
    /**
     * Calcula la duración estimada de un ejercicio en minutos
     * Basado en: series × repeticiones × tiempo por repetición + tiempo de descanso
     */
    private fun calcularDuracionEjercicio(ejercicioRutina: EjercicioRutina): Double {
        val series = ejercicioRutina.series
        val repeticionesTexto = ejercicioRutina.repeticiones
        
        // Extraer número de repeticiones (puede ser "10-12" o "10")
        val repeticiones = extraerNumeroRepeticiones(repeticionesTexto)
        
        // Tiempo promedio por repetición (segundos)
        val tiempoPorRepeticion = 3.0 // 3 segundos por repetición promedio
        
        // Tiempo de descanso entre series (segundos)
        val tiempoDescanso = 60.0 // 60 segundos (1 minuto) entre series
        
        // Calcular tiempo total en minutos
        val tiempoEjecucion = (series * repeticiones * tiempoPorRepeticion) / 60.0
        val tiempoDescansoTotal = ((series - 1) * tiempoDescanso) / 60.0
        
        return tiempoEjecucion + tiempoDescansoTotal
    }
    
    /**
     * Extrae el número de repeticiones de un string (ej: "10-12" -> 11, "10" -> 10)
     */
    private fun extraerNumeroRepeticiones(repeticionesTexto: String): Int {
        return try {
            if (repeticionesTexto.contains("-")) {
                // Rango: "10-12" -> promedio 11
                val partes = repeticionesTexto.split("-")
                val min = partes[0].trim().toIntOrNull() ?: 10
                val max = partes.getOrNull(1)?.trim()?.toIntOrNull() ?: min
                (min + max) / 2
            } else {
                // Número único: "10" -> 10
                repeticionesTexto.trim().toIntOrNull() ?: 10
            }
        } catch (e: Exception) {
            10 // Valor por defecto
        }
    }
    
    /**
     * Limpia los resultados
     */
    fun limpiarResultados() {
        _caloriasPorDia.value = emptyMap()
        _error.value = null
    }
}

