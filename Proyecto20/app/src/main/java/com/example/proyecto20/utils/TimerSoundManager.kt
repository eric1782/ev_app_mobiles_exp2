package com.example.proyecto20.utils

import android.media.AudioManager
import android.media.ToneGenerator

class TimerSoundManager {
    
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
    
    /**
     * Sonido de inicio de trabajo - más energético y deportivo
     */
    fun playStartSound() {
        // Tono más fuerte y energético para inicio de trabajo
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 300)
    }
    
    /**
     * Sonido de fin de intervalo - inicio de descanso
     */
    fun playEndIntervalSound() {
        // Tono distintivo para fin de intervalo
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2, 400)
    }
    
    /**
     * Sonido de completado - celebración
     */
    fun playCompleteSound() {
        // Tono de éxito más largo y celebratorio
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 600)
        // Reproducir un segundo tono para celebrar
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 300)
        }, 650)
    }
    
    /**
     * Sonido de cuenta regresiva (3, 2, 1) - más corto y preciso
     */
    fun playCountdownSound() {
        // Tono corto y claro para cuenta regresiva
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
    }
    
    /**
     * Sonido de advertencia (últimos 5 segundos) - más intenso
     */
    fun playWarningSound() {
        // Tono más intenso para advertencia
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2, 200)
    }
    
    fun release() {
        toneGenerator.release()
    }
}

