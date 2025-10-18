package com.example.proyecto20.model

import java.util.Date

/**
 * Define un ejercicio específico dentro de una rutina.
 */
data class EjercicioRutina(
    val idEjercicio: String,
    val series: Int,
    val repeticiones: String,
    val pesoRecomendadoKg: Double,
    val notas: String? = null
)

/**
 * Representa la rutina completa asignada a un alumno.
 */
data class Rutina(
    val id: String,
    val idAlumno: String,
    val idEntrenador: String,
    val nombre: String,
    val fechaCreacion: Date,
    val ejerciciosPorDia: Map<String, List<EjercicioRutina>> // Clave: "LUNES", "MARTES", etc.
)
