// Ruta: app/src/main/java/com/example/proyecto20/model/AppModels.kt

package com.example.proyecto20.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp

// --- ENUMS PARA CONTROLAR ESTADOS Y TIPOS ---

enum class RolUsuario {
    ENTRENADOR,
    ALUMNO
}

enum class TipoAlumno {
    ONLINE,
    PRESENCIAL
}

// --- MODELOS DE DATOS PRINCIPALES (SE SINCRONIZAN CON FIRESTORE) ---

data class Usuario(
    @get:Exclude var id: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val telefono: String? = null,
    val whatsapp: String? = null,
    val rol: RolUsuario = RolUsuario.ALUMNO,
    val idEntrenador: String? = null,
    val peso: Double? = null,
    val estatura: Double? = null,
    val tipo: TipoAlumno = TipoAlumno.PRESENCIAL,
    val rutina: List<DiaEntrenamiento> = emptyList(),
    val horariosPresenciales: List<HorarioPresencial> = emptyList()
)

data class Ejercicio(
    @get:Exclude var id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val musculoPrincipal: String = "",
    val urlVideo: String = "",
    val urlGif: String = "",              // URL del GIF (puede ser de API o subido)
    val urlImagen: String = "",           // URL de imagen (puede ser de API o subida)
    val fuenteVideo: String = "",         // "api", "upload", "manual" - para saber el origen
    val esDeAPI: Boolean = false          // Si fue importado completamente de la API
)


// --- MODELOS DE DATOS PARA ESTRUCTURAS COMPLEJAS (VAN DENTRO DE OTROS MODELOS) ---

data class DiaEntrenamiento(
    val dia: String = "", // "LUNES", "MARTES", etc.
    val ejercicios: List<EjercicioRutina> = emptyList()
)

data class EjercicioRutina(
    val ejercicioId: String = "", // Referencia al ejercicio en el catálogo
    val nombre: String = "",      // Para mostrarlo fácilmente en la UI
    val series: Int = 0,
    val repeticiones: String = "",
    val rir: Int? = null,
    val peso: Double? = null
)

data class HorarioPresencial(
    val dia: String = "",
    val hora: String = ""
)


// --- MODELOS DE DATOS ESPECIALES PARA LA UI (NO NECESARIAMENTE EN FIRESTORE) ---

data class Cita(
    val id: String = "",
    val nombreAlumno: String = "",
    val horaInicio: String = "",
    val horaFin: String = "",
    val idAlumno: String = ""
)

// --- ¡¡INICIO DE LA CORRECCIÓN CRÍTICA!! ---
data class RegistroProgreso(
    // Se cambia 'fecha' por 'timestamp' para que coincida con el resto de la app
    @ServerTimestamp
    val timestamp: com.google.firebase.Timestamp? = null,
    val ejercicioId: String = "",
    val ejercicioNombre: String = "",
    val series: Int = 0,
    val repeticiones: String = "",
    val peso: Double? = null,
    val rir: Int? = null,
    val comentario: String? = null
)

// --- MODELOS PARA TEMPORIZADORES (SESSION-ONLY, NO SE GUARDAN EN FIRESTORE) ---

enum class TimerState {
    IDLE,           // Creado pero no iniciado
    RUNNING,        // En ejecución (trabajo o descanso)
    PAUSED,         // Pausado
    COMPLETED       // Completado todas las rondas
}

enum class TimerPhase {
    PREPARE,        // Fase de preparación (5 segundos antes de empezar)
    WORK,           // Fase de trabajo
    REST            // Fase de descanso
}

data class Timer(
    val id: String,                    // ID único del temporizador
    val nombre: String,                // Nombre del temporizador (ej: "Tabata - Sara")
    val tiempoTrabajoSegundos: Int,    // Duración del trabajo en segundos
    val tiempoDescansoSegundos: Int,   // Duración del descanso en segundos
    val repeticiones: Int,             // Número de rondas
    val repeticionesCompletadas: Int = 0,  // Rondas completadas
    val estado: TimerState = TimerState.IDLE,
    val faseActual: TimerPhase = TimerPhase.WORK,
    val tiempoRestanteSegundos: Int = 0,  // Tiempo restante del intervalo actual
    val tiempoInicioPausa: Long? = null   // Timestamp cuando se pausó (para calcular el tiempo transcurrido)
)

