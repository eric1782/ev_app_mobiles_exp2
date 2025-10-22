// Ruta: app/src/main/java/com/example/proyecto20/model/AppModels.kt

package com.example.proyecto20.model

import com.google.firebase.firestore.Exclude

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
    val email: String = "",
    val rol: RolUsuario = RolUsuario.ALUMNO,
    val idEntrenador: String? = null, // <- COMA AÑADIDA
    val peso: Double? = null,         // <- COMA AÑADIDA
    val estatura: Double? = null,     // <- COMA AÑADIDA
    val tipo: TipoAlumno = TipoAlumno.PRESENCIAL, // <- COMA AÑADIDA
    val rutina: List<DiaEntrenamiento> = emptyList(), // <- COMA AÑADIDA
    val horariosPresenciales: List<HorarioPresencial> = emptyList()
)

data class Ejercicio(
    @get:Exclude var id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val musculoPrincipal: String = "",
    val urlVideo: String = ""
)

data class BloqueHorario(
    @get:Exclude var id: String = "",
    val entrenadorId: String = "",
    val idAlumno: String? = null,
    val horaInicio: String = "",
    val horaFin: String = "",
    val disponible: Boolean = true
)

// --- MODELOS DE DATOS PARA ESTRUCTURAS COMPLEJAS (VAN DENTRO DE OTROS MODELOS) ---

data class DiaEntrenamiento(
    val dia: String, // "LUNES", "MARTES", etc.
    val ejercicios: List<EjercicioRutina> = emptyList()
)

data class EjercicioRutina(
    val ejercicioId: String, // Referencia al ejercicio en el catálogo
    val nombre: String,      // Para mostrarlo fácilmente en la UI
    val series: Int = 3,
    val repeticiones: String = "10-12",
    val rir: Int? = 2, // Repeticiones en Reserva
    val peso: Double? = null
)

data class HorarioPresencial(
    val dia: String,
    val bloqueHorarioId: String // ID del bloque reservado en la colección 'horarios'
)


// --- MODELOS DE DATOS ESPECIALES PARA LA UI (NO NECESARIAMENTE EN FIRESTORE) ---

data class Cita(
    val id: String,
    val nombreAlumno: String,
    val horaInicio: String,
    val horaFin: String,
    val idAlumno: String
)
