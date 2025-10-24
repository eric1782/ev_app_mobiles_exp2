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
    val email: String = "",
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
    val urlVideo: String = ""
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

