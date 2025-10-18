package com.example.proyecto20.util

import com.example.proyecto20.data.MockData
import com.example.proyecto20.model.CitaMostrable
import java.time.LocalDate
import java.time.LocalTime

/**
 * Objeto que centraliza la lógica para obtener información
 * de citas y horarios de entrenamiento.
 */
object GestorDeCitas {

    /**
     * Busca en los datos de prueba qué alumnos de un entrenador tienen
     * programado entrenar en una fecha específica.
     */
    fun obtenerCitasParaDia(idEntrenador: String, fecha: LocalDate): List<CitaMostrable> {
        val diaDeLaSemana = fecha.dayOfWeek
        val citasDelDia = mutableListOf<CitaMostrable>()

        // 1. Filtramos para obtener solo los alumnos asignados al entrenador.
        val misAlumnos = MockData.todosLosUsuarios.filter { it.idEntrenadorAsignado == idEntrenador }

        // 2. Iteramos sobre cada alumno del entrenador.
        for (alumno in misAlumnos) {
            // 3. Comprobamos si el alumno tiene definido entrenar ese día de la semana.
            if (alumno.diasEntrenamiento?.contains(diaDeLaSemana) == true) {

                // Como no tenemos una hora guardada, vamos a simularla.
                // Por ejemplo, todos entrenan a las 9:00 AM.
                val horaInicioSimulada = LocalTime.of(9, 0)
                val horaFinSimulada = horaInicioSimulada.plusHours(1) // Duración de 1 hora

                // 4. Creamos el objeto CitaMostrable con los datos.
                citasDelDia.add(
                    CitaMostrable(
                        idAlumno = alumno.id,
                        nombreAlumno = alumno.nombre,
                        horaInicio = horaInicioSimulada,
                        horaFin = horaFinSimulada
                    )
                )
            }
        }

        // 5. Devolvemos la lista de citas, ordenada por hora.
        return citasDelDia.sortedBy { it.horaInicio }
    }
}
