package com.example.proyecto20.data

import com.example.proyecto20.model.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date
import java.util.Locale

object MockData {

    // --- EJERCICIOS (Sin cambios) ---
    private val _todosLosEjercicios = mutableListOf(
        Ejercicio("ej001", "Press de Banca", "Descripción detallada del press de banca.", "Pecho", "video_press_banca.mp4"),
        Ejercicio("ej002", "Sentadilla", "Descripción detallada de la sentadilla.", "Piernas", "video_sentadilla.mp4"),
        Ejercicio("ej003", "Peso Muerto", "Descripción detallada del peso muerto.", "Espalda", "video_peso_muerto.mp4"),
        Ejercicio("ej004", "Press Militar", "Descripción detallada del press militar.", "Hombros", "video_press_militar.mp4"),
        Ejercicio("ej005", "Curl de Bíceps", "Descripción detallada del curl de bíceps.", "Brazos", "video_curl_biceps.mp4"),
        Ejercicio("ej006", "Jalón al Pecho", "Descripción detallada de jalón al pecho.", "Espalda", "video_jalon_pecho.mp4")
    )
    val todosLosEjercicios: List<Ejercicio> get() = _todosLosEjercicios

    fun addEjercicioCompletoAlCatalogo(nombre: String, descripcion: String, musculo: String, url: String) {
        val nuevoId = "ej${(_todosLosEjercicios.size + 1).toString().padStart(3, '0')}"
        _todosLosEjercicios.add(
            Ejercicio(id = nuevoId, nombre = nombre, descripcion = descripcion, musculoPrincipal = musculo, urlVideo = url)
        )
    }

    // --- USUARIOS Y RUTINAS (Sin cambios) ---
    private val _todosLosUsuarios = mutableListOf<Usuario>()
    val todosLosUsuarios: List<Usuario> get() = _todosLosUsuarios

    private val _todasLasRutinas = mutableListOf<Rutina>()
    val todasLasRutinas: List<Rutina> get() = _todasLasRutinas

    // --- BLOQUE INICIALIZADOR (Sin cambios) ---
    init {
        val entrenador = Usuario(
            id = "user001",
            nombre = "Carlos (Entrenador)",
            email = "carlos@gmail.com",
            password = "123",
            rol = RolUsuario.ENTRENADOR,
            idEntrenadorAsignado = null
        )

        val alumnoDiego = Usuario(
            id = "user002",
            nombre = "Diego",
            email = "diego@gmail.com",
            password = "123",
            rol = RolUsuario.ALUMNO,
            tipoCliente = TipoCliente.PRESENCIAL,
            idEntrenadorAsignado = "user001",
            diasEntrenamiento = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SATURDAY)
        )

        val alumnoAna = Usuario(
            id = "user003",
            nombre = "Ana",
            email = "ana@gmail.com",
            password = "123",
            rol = RolUsuario.ALUMNO,
            tipoCliente = TipoCliente.ONLINE,
            idEntrenadorAsignado = "user001",
            diasEntrenamiento = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SATURDAY)
        )

        _todosLosUsuarios.addAll(listOf(entrenador, alumnoDiego, alumnoAna))

        val rutinaDiego = Rutina(
            id = "rut001", idAlumno = "user002", idEntrenador = "user001",
            nombre = "Fase 1 - Acondicionamiento", fechaCreacion = Date(),
            ejerciciosPorDia = mapOf(
                "LUNES" to listOf(EjercicioRutina("ej001", 4, "8-10", 80.0), EjercicioRutina("ej004", 3, "10-12", 40.0)),
                "MIERCOLES" to listOf(EjercicioRutina("ej002", 4, "8-10", 100.0), EjercicioRutina("ej005", 3, "12-15", 15.0)),
                "SÁBADO" to listOf(EjercicioRutina("ej003", 4, "5-8", 120.0), EjercicioRutina("ej006", 3, "10-12", 60.0))
            )
        )

        val rutinaAna = Rutina(
            id = "rut002", idAlumno = "user003", idEntrenador = "user001",
            nombre = "Rutina de Ana", fechaCreacion = Date(),
            ejerciciosPorDia = mapOf(
                "LUNES" to listOf(EjercicioRutina("ej001", 4, "8-10", 80.0), EjercicioRutina("ej004", 3, "10-12", 40.0)),
                "MIERCOLES" to listOf(EjercicioRutina("ej002", 4, "8-10", 100.0), EjercicioRutina("ej005", 3, "12-15", 15.0)),
                "SÁBADO" to listOf(EjercicioRutina("ej003", 4, "5-8", 120.0), EjercicioRutina("ej006", 3, "10-12", 60.0))
            )
        )
        _todasLasRutinas.addAll(listOf(rutinaDiego, rutinaAna))
    }

    // --- SECCIÓN CORREGIDA Y ACTIVADA ---

    /**
     * Convierte un String de un día (ej. "LUNES") a su correspondiente DayOfWeek.
     * Es necesario para sincronizar la rutina con el calendario.
     */
    private fun stringToDayOfWeek(dia: String): DayOfWeek? {
        return try {
            // Convierte el string a mayúsculas para que coincida con los nombres del enum DayOfWeek
            DayOfWeek.valueOf(dia.uppercase(Locale.ROOT))
        } catch (e: IllegalArgumentException) {
            null // Devuelve null si el string no es un día de la semana válido
        }
    }

    /**
     * Crea un nuevo alumno y su rutina correspondiente.
     * Esta versión es compatible con `CrearAlumnoScreen`.
     * NOTA: Los campos `peso` y `estatura` se reciben pero no se guardan,
     * ya que no existen en el modelo `Usuario`.
     */
    fun crearAlumnoCompleto(
        nombre: String,
        email: String,
        pass: String,
        peso: Double, // Se recibe pero no se usa en el modelo Usuario
        estatura: Double, // Se recibe pero no se usa en el modelo Usuario
        tipoCliente: TipoCliente, // <-- AÑADIR ESTE PARÁMETRO
        rutinaMap: Map<String, List<EjercicioRutina>>,
        idEntrenador: String
    ) {
        val nuevoId = "user" + (_todosLosUsuarios.size + 1).toString().padStart(3, '0')

        // Convierte las claves del mapa de rutina (Strings como "LUNES") a una lista de DayOfWeek
        val diasDeEntrenamiento = rutinaMap.keys.mapNotNull { stringToDayOfWeek(it) }

        val nuevoAlumno = Usuario(
            id = nuevoId,
            nombre = nombre,
            email = email,
            password = pass.ifBlank { "123" },
            rol = RolUsuario.ALUMNO,
            tipoCliente = tipoCliente, // <-- USAR EL PARÁMETRO RECIBIDO
            idEntrenadorAsignado = idEntrenador,
            diasEntrenamiento = diasDeEntrenamiento
        )

        val nuevaRutina = Rutina(
            id = "rut$nuevoId",
            idAlumno = nuevoId,
            idEntrenador = idEntrenador,
            nombre = "Rutina de $nombre",
            fechaCreacion = Date(),
            ejerciciosPorDia = rutinaMap
        )

        _todosLosUsuarios.add(nuevoAlumno)
        _todasLasRutinas.add(nuevaRutina)
    }


    // --- El resto de los datos de ejemplo (sin cambios) ---
    val historialProgreso = listOf(
        Progreso("user002", "ej001", Date(), 80.0, "8"),
        Progreso("user002", "ej001", Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000), 77.5, "9"),
        Progreso("user002", "ej002", Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000), 100.0, "8"),
        Progreso("user002", "ej002", Date(System.currentTimeMillis() - 9 * 24 * 60 * 60 * 1000), 95.0, "10")
    )

    val horariosRecurrentes = listOf(
        HorarioRecurrente("hr001", "user002", DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0)),
        HorarioRecurrente("hr002", "user003", DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0)),
        HorarioRecurrente("hr003", "user002", DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(10, 0)),
        HorarioRecurrente("hr004", "user003", DayOfWeek.THURSDAY, LocalTime.of(11, 0), LocalTime.of(12, 0)),
        HorarioRecurrente("hr005", "user002", DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0))
    )

    val excepcionesHorario = listOf(
        ExcepcionHorario("ex001", "hr001", LocalDate.now().with(DayOfWeek.MONDAY), null, null, null),
        ExcepcionHorario("ex002", "hr004", LocalDate.now().with(DayOfWeek.THURSDAY), LocalDate.now().with(DayOfWeek.FRIDAY), LocalTime.of(11, 0), LocalTime.of(12, 0))
    )
}
