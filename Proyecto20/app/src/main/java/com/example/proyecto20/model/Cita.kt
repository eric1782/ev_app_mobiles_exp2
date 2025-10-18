package com.example.proyecto20.model

import java.util.Date

data class Cita(
    val id: String,
    val idEntrenador: String,
    val idAlumno: String,
    val fechaHoraInicio: Date,
    val fechaHoraFin: Date, // Para definir la duración
    val notas: String? = null // Notas como "Reprogramada", "Pagada", etc.
)
