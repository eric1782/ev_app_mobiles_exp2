package com.example.proyecto20.data

import com.example.proyecto20.model.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date

object MockData {

    // --- EJERCICIOS (Esto ya estaba bien) ---
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

    // --- USUARIOS Y RUTINAS (SECCIÓN CORREGIDA) ---
    private val _todosLosUsuarios = mutableListOf<Usuario>()
    val todosLosUsuarios: List<Usuario> get() = _todosLosUsuarios

    // Lista para guardar las rutinas por separado, ya que no están dentro del Usuario
    private val _todasLasRutinas = mutableListOf<Rutina>()
    val todasLasRutinas: List<Rutina> get() = _todasLasRutinas

    // --- BLOQUE INICIALIZADOR PARA CREAR LOS DATOS DE EJEMPLO ---
    init {
        // 1. Crear los usuarios según la estructura de tu data class
        val entrenador = Usuario(
            id = "user001",
            nombre = "Carlos (Entrenador)",
            email = "carlos@gmail.com",
            password = "123",
            rol = RolUsuario.ENTRENADOR,
            idEntrenadorAsignado = null // El entrenador no tiene un entrenador asignado
        )

        val alumnoDiego = Usuario(
            id = "user002",
            nombre = "Diego",
            email = "diego@gmail.com",
            password = "123",
            rol = RolUsuario.ALUMNO,
            tipoCliente = TipoCliente.PRESENCIAL,
            idEntrenadorAsignado = "user001", // ID del entrenador de arriba
            diasEntrenamiento = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
        )

        val alumnoAna = Usuario(
            id = "user003",
            nombre = "Ana",
            email = "ana@gmail.com",
            password = "123",
            rol = RolUsuario.ALUMNO,
            tipoCliente = TipoCliente.ONLINE,
            idEntrenadorAsignado = "user001",
            diasEntrenamiento = listOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY)
        )

        _todosLosUsuarios.addAll(listOf(entrenador, alumnoDiego, alumnoAna))

        // 2. Crear las rutinas según la estructura de tu data class
        val rutinaDiego = Rutina(
            id = "rut001",
            idAlumno = "user002", // ID de Diego
            idEntrenador = "user001", // ID de Carlos
            nombre = "Fase 1 - Acondicionamiento",
            fechaCreacion = Date(),
            ejerciciosPorDia = mapOf(
                "LUNES" to listOf(
                    EjercicioRutina("ej001", 4, "8-10", 80.0, "Bajar lento y controlado."),
                    EjercicioRutina("ej004", 3, "10-12", 40.0)
                ),
                "MIERCOLES" to listOf(
                    EjercicioRutina("ej002", 4, "8-10", 100.0),
                    EjercicioRutina("ej005", 3, "12-15", 15.0, "Concentrarse en el apretón.")
                ),
                "SÁBADO" to listOf(
                    EjercicioRutina("ej003", 4, "5-8", 120.0),
                    EjercicioRutina("ej006", 3, "10-12", 60.0)
                )
            )
        )

        // Rutina vacía para Ana
        val rutinaAna = Rutina(
            id = "rut002",
            idAlumno = "user003",
            idEntrenador = "user001",
            nombre = "Rutina de Ana",
            fechaCreacion = Date(),
            ejerciciosPorDia = mapOf(
                "LUNES" to listOf(
                    EjercicioRutina("ej001", 4, "8-10", 80.0, "Bajar lento y controlado."),
                    EjercicioRutina("ej004", 3, "10-12", 40.0)
                ),
                "MIERCOLES" to listOf(
                    EjercicioRutina("ej002", 4, "8-10", 100.0),
                    EjercicioRutina("ej005", 3, "12-15", 15.0, "Concentrarse en el apretón.")
                ),
                "SÁBADO" to listOf(
                    EjercicioRutina("ej003", 4, "5-8", 120.0),
                    EjercicioRutina("ej006", 3, "10-12", 60.0)
                )
            )
        )
        _todasLasRutinas.addAll(listOf(rutinaDiego, rutinaAna))
    }

    // --- FUNCIÓN PARA CREAR NUEVOS ALUMNOS (AHORA INCOMPATIBLE) ---
    // ESTA FUNCIÓN YA NO ES COMPATIBLE con tu modelo. La comentamos para que no de error
    // y luego decidimos cómo manejar la creación de alumnos. y mas alumnos 
    /*
    fun crearAlumnoCompleto(
        nombre: String,
        email: String,
        pass: String,
        peso: Double, // Este dato ya no está en Usuario
        estatura: Double, // Este dato ya no está en Usuario
        rutinaMap: Map<String, List<EjercicioRutina>>,
        idEntrenador: String
    ) {
        val nuevoId = "user" + (_todosLosUsuarios.size + 1).toString().padStart(3, '0')

        val nuevoAlumno = Usuario(
            id = nuevoId,
            nombre = nombre,
            email = email,
            password = pass.ifBlank { "123" },
            rol = RolUsuario.ALUMNO,
            tipoCliente = TipoCliente.PRESENCIAL,
            idEntrenadorAsignado = idEntrenador
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
    */


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
