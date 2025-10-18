package com.example.proyecto20.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/**
 * Representa un bloque de entrenamiento fijo y recurrente.
 */
data class HorarioRecurrente(
    val id: String,
    val idAlumno: String,
    val diaDeLaSemana: DayOfWeek,
    val horaInicio: LocalTime,
    val horaFin: LocalTime
)

/**
 * Representa una excepción a un horario recurrente (cancelación o reprogramación).
 */
data class ExcepcionHorario(
    val id: String,
    val idHorarioRecurrente: String,
    val fechaOriginal: LocalDate,
    val nuevaFecha: LocalDate?,
    val nuevaHoraInicio: LocalTime?,
    val nuevaHoraFin: LocalTime?
)
