package com.example.proyecto20.utils

import android.content.Context
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

/**
 * Helper para programar notificaciones de recordatorio de citas
 */
object CitaNotificationHelper {
    
    /**
     * Programa las notificaciones de recordatorio para una cita presencial
     * 
     * @param context Contexto de la aplicación
     * @param citaId ID único de la cita
     * @param nombreAlumno Nombre del alumno
     * @param dia Dia de la semana (ej: "LUNES", "MARTES")
     * @param hora Hora de la cita (formato: "HH:mm" o "HH:mm - HH:mm")
     * @param fecha Fecha específica de la cita (si es null, se calcula para la próxima ocurrencia del día)
     */
    fun scheduleCitaNotifications(
        context: Context,
        citaId: String,
        nombreAlumno: String,
        dia: String,
        hora: String,
        fecha: Date? = null
    ) {
        val notificationManager = AppNotificationManager(context)
        
        // Extraer hora de inicio (si es formato "HH:mm - HH:mm", tomar la primera)
        val horaInicio = hora.split(" - ").first().trim()
        
        if (horaInicio.isBlank()) {
            Log.w("CitaNotificationHelper", "Hora vacía, no se programa notificación")
            return
        }
        
        // Calcular fecha y hora completa de la cita
        val fechaHoraCita = fecha ?: calcularProximaFecha(dia, horaInicio)
        
        if (fechaHoraCita == null) {
            Log.e("CitaNotificationHelper", "No se pudo calcular la fecha de la cita")
            return
        }
        
        val fechaHoraMillis = fechaHoraCita.time
        
        // Programar notificación de 1 hora antes
        notificationManager.scheduleCitaReminderNotification(
            citaId = citaId,
            nombreAlumno = nombreAlumno,
            horaInicio = horaInicio,
            fechaInicio = fechaHoraMillis,
            minutosAntes = 60
        )
        
        // Programar notificación de 30 minutos antes
        notificationManager.scheduleCitaReminderNotification(
            citaId = citaId,
            nombreAlumno = nombreAlumno,
            horaInicio = horaInicio,
            fechaInicio = fechaHoraMillis,
            minutosAntes = 30
        )
        
        Log.d("CitaNotificationHelper", "Notificaciones programadas para cita $citaId: $nombreAlumno el $dia a las $horaInicio")
    }
    
    /**
     * Cancela las notificaciones programadas de una cita
     */
    fun cancelCitaNotifications(context: Context, citaId: String) {
        val notificationManager = AppNotificationManager(context)
        notificationManager.cancelCitaReminders(citaId)
        Log.d("CitaNotificationHelper", "Notificaciones canceladas para cita $citaId")
    }
    
    /**
     * Calcula la próxima fecha en que ocurre un día de la semana a una hora específica
     */
    private fun calcularProximaFecha(dia: String, hora: String): Date? {
        return try {
            val diasSemana = mapOf(
                "LUNES" to Calendar.MONDAY,
                "MARTES" to Calendar.TUESDAY,
                "MIÉRCOLES" to Calendar.WEDNESDAY,
                "MIERCOLES" to Calendar.WEDNESDAY,
                "JUEVES" to Calendar.THURSDAY,
                "VIERNES" to Calendar.FRIDAY,
                "SÁBADO" to Calendar.SATURDAY,
                "SABADO" to Calendar.SATURDAY,
                "DOMINGO" to Calendar.SUNDAY
            )
            
            val diaSemana = diasSemana[dia.uppercase()] ?: return null
            
            // Parsear hora
            val partesHora = hora.split(":")
            val horaInt = partesHora[0].toIntOrNull() ?: return null
            val minutoInt = partesHora.getOrNull(1)?.toIntOrNull() ?: 0
            
            val calendar = Calendar.getInstance()
            val diaActual = calendar.get(Calendar.DAY_OF_WEEK)
            
            // Calcular días hasta el próximo día de la semana
            var diasHasta = diaSemana - diaActual
            if (diasHasta <= 0) {
                diasHasta += 7 // Si ya pasó este día, ir al próximo
            }
            
            // Si es el mismo día pero la hora ya pasó, ir a la próxima semana
            if (diasHasta == 0) {
                val horaActual = calendar.get(Calendar.HOUR_OF_DAY)
                val minutoActual = calendar.get(Calendar.MINUTE)
                if (horaInt < horaActual || (horaInt == horaActual && minutoInt <= minutoActual)) {
                    diasHasta = 7
                }
            }
            
            calendar.add(Calendar.DAY_OF_MONTH, diasHasta)
            calendar.set(Calendar.HOUR_OF_DAY, horaInt)
            calendar.set(Calendar.MINUTE, minutoInt)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            
            calendar.time
        } catch (e: Exception) {
            Log.e("CitaNotificationHelper", "Error al calcular fecha", e)
            null
        }
    }
}

