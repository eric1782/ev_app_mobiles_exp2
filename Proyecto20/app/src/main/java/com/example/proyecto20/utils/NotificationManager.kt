package com.example.proyecto20.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.proyecto20.MainActivity
import com.example.proyecto20.R

/**
 * Manager para manejar todas las notificaciones locales de la app
 */
class AppNotificationManager(private val context: Context) {
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    companion object {
        // IDs de canales de notificaciones
        private const val CHANNEL_ID_TIMERS = "channel_timers"
        private const val CHANNEL_ID_CITAS = "channel_citas"
        
        // IDs de notificaciones
        private const val NOTIFICATION_ID_TIMER_COMPLETED = 1000
        private const val NOTIFICATION_ID_TIMER_INTERVAL = 1001
        private const val NOTIFICATION_ID_CITA_1HORA = 2000
        private const val NOTIFICATION_ID_CITA_30MIN = 2001
        
        // Base para IDs de notificaciones de citas (usaremos citaId.hashCode())
        private const val BASE_CITA_ID = 3000
    }
    
    init {
        createNotificationChannels()
    }
    
    /**
     * Crea los canales de notificaciones necesarios (requerido para Android 8.0+)
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Canal para temporizadores
            val timerChannel = NotificationChannel(
                CHANNEL_ID_TIMERS,
                "Temporizadores",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones cuando los temporizadores terminan"
                enableVibration(true)
                enableLights(true)
            }
            
            // Canal para citas
            val citaChannel = NotificationChannel(
                CHANNEL_ID_CITAS,
                "Recordatorios de Citas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Recordatorios antes de tus citas de entrenamiento"
                enableVibration(true)
                enableLights(true)
            }
            
            notificationManager.createNotificationChannel(timerChannel)
            notificationManager.createNotificationChannel(citaChannel)
        }
    }
    
    /**
     * Muestra una notificación cuando un temporizador se completa
     */
    fun showTimerCompletedNotification(timerName: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_TIMERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO: Cambiar por icono personalizado
            .setContentTitle("✅ Temporizador Completado")
            .setContentText("$timerName ha terminado")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("¡Felicidades! Has completado el temporizador: $timerName"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_TIMER_COMPLETED, notification)
    }
    
    /**
     * Muestra una notificación cuando termina un intervalo (trabajo/descanso)
     */
    fun showTimerIntervalNotification(timerName: String, isWork: Boolean) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val title = if (isWork) "⏱️ Tiempo de Trabajo Terminado" else "💤 Tiempo de Descanso Terminado"
        val text = if (isWork) "Es hora de descansar" else "Es hora de trabajar"
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_TIMERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO: Cambiar por icono personalizado
            .setContentTitle(title)
            .setContentText("$timerName: $text")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_TIMER_INTERVAL, notification)
    }
    
    /**
     * Programa una notificación de recordatorio de cita
     * @param citaId ID único de la cita
     * @param nombreAlumno Nombre del alumno
     * @param horaInicio Hora de inicio de la cita (formato: "HH:mm")
     * @param fechaInicio Fecha y hora completa de la cita en milisegundos
     * @param minutosAntes Minutos antes de la cita para mostrar la notificación (30 o 60)
     */
    fun scheduleCitaReminderNotification(
        citaId: String,
        nombreAlumno: String,
        horaInicio: String,
        fechaInicio: Long,
        minutosAntes: Int
    ) {
        val notificationTime = fechaInicio - (minutosAntes * 60 * 1000L)
        val now = System.currentTimeMillis()
        
        // Solo programar si la notificación es en el futuro
        if (notificationTime <= now) {
            android.util.Log.d("NotificationManager", "No se programa notificación: ya pasó el tiempo")
            return
        }
        
        val delay = notificationTime - now
        
        android.util.Log.d("NotificationManager", "Programando notificación para cita $citaId en ${delay / 1000 / 60} minutos")
        
        // Usar AlarmManager para programar la notificación
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        
        val intent = Intent(context, CitaNotificationReceiver::class.java).apply {
            putExtra("cita_id", citaId)
            putExtra("nombre_alumno", nombreAlumno)
            putExtra("hora_inicio", horaInicio)
            putExtra("minutos_antes", minutosAntes)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (BASE_CITA_ID + citaId.hashCode() + minutosAntes).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                android.app.AlarmManager.RTC_WAKEUP,
                notificationTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                android.app.AlarmManager.RTC_WAKEUP,
                notificationTime,
                pendingIntent
            )
        }
    }
    
    /**
     * Cancela las notificaciones programadas de una cita
     */
    fun cancelCitaReminders(citaId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        
        // Cancelar notificación de 1 hora antes
        val intent1h = Intent(context, CitaNotificationReceiver::class.java)
        val pendingIntent1h = PendingIntent.getBroadcast(
            context,
            (BASE_CITA_ID + citaId.hashCode() + 60).toInt(),
            intent1h,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent1h)
        
        // Cancelar notificación de 30 minutos antes
        val intent30min = Intent(context, CitaNotificationReceiver::class.java)
        val pendingIntent30min = PendingIntent.getBroadcast(
            context,
            (BASE_CITA_ID + citaId.hashCode() + 30).toInt(),
            intent30min,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent30min)
    }
    
    /**
     * Muestra una notificación de recordatorio de cita (llamado desde el BroadcastReceiver)
     */
    fun showCitaReminderNotification(nombreAlumno: String, horaInicio: String, minutosAntes: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val tiempoTexto = if (minutosAntes == 60) "1 hora" else "30 minutos"
        val notificationId = if (minutosAntes == 60) NOTIFICATION_ID_CITA_1HORA else NOTIFICATION_ID_CITA_30MIN
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_CITAS)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO: Cambiar por icono personalizado
            .setContentTitle("📅 Recordatorio de Cita")
            .setContentText("Cita con $nombreAlumno en $tiempoTexto")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Tienes una cita con $nombreAlumno a las $horaInicio en $tiempoTexto"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        notificationManager.notify(notificationId, notification)
    }
}

