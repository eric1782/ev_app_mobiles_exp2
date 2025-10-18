package com.example.proyecto20.model

import java.time.LocalTime

/**
 * Data class simple para representar la información de una cita
 * que se va a mostrar en el calendario.
 */
data class CitaMostrable(
    val idAlumno: String,
    val nombreAlumno: String,
    val horaInicio: LocalTime,
    val horaFin: LocalTime
)
