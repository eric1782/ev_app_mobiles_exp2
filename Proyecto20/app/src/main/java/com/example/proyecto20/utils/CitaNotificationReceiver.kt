package com.example.proyecto20.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * BroadcastReceiver que se activa cuando es el momento de mostrar
 * la notificación de recordatorio de cita
 */
class CitaNotificationReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("CitaNotificationReceiver", "Recibido broadcast para notificación de cita")
        
        val citaId = intent.getStringExtra("cita_id") ?: return
        val nombreAlumno = intent.getStringExtra("nombre_alumno") ?: return
        val horaInicio = intent.getStringExtra("hora_inicio") ?: return
        val minutosAntes = intent.getIntExtra("minutos_antes", 30)
        
        Log.d("CitaNotificationReceiver", "Mostrando notificación: $nombreAlumno a las $horaInicio ($minutosAntes min antes)")
        
        val notificationManager = AppNotificationManager(context)
        notificationManager.showCitaReminderNotification(nombreAlumno, horaInicio, minutosAntes)
    }
}

