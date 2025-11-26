package com.example.proyecto20.data

import com.example.proyecto20.model.*
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

object FirebaseRepository {
    val db = Firebase.firestore
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // --- FUNCIONES DE AUTENTICACIÓN ---
    fun getAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth -> trySend(auth.currentUser).isSuccess }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun login(email: String, password: String): FirebaseUser? {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user
    }

    suspend fun register(email: String, pass: String, nombre: String, rol: RolUsuario, onResult: (Boolean, String?) -> Unit) {
        try {
            val authResult = auth.createUserWithEmailAndPassword(email, pass).await()
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                val newUser = Usuario(id = firebaseUser.uid, nombre = nombre, email = email, rol = rol)
                db.collection("usuarios").document(firebaseUser.uid).set(newUser).await()
                onResult(true, null)
            } else {
                onResult(false, "No se pudo crear el usuario.")
            }
        } catch (e: Exception) {
            onResult(false, e.message)
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun changePassword(oldPass: String, newPass: String) {
        val user = auth.currentUser ?: throw Exception("No hay un usuario logueado.")
        val credential = EmailAuthProvider.getCredential(user.email!!, oldPass)
        user.reauthenticate(credential).await()
        user.updatePassword(newPass).await()
    }

    // --- FUNCIONES DE USUARIO ---
    suspend fun getCurrentUser(): Usuario? {
        val firebaseUser = auth.currentUser ?: return null
        return getUsuarioById(firebaseUser.uid)
    }

    // --- ¡¡INICIO DE LA FUNCIÓN AÑADIDA!! ---
    /**
     * Obtiene un Flow de un usuario para observar sus cambios en tiempo real.
     */
    fun getUsuarioFlow(userId: String): Flow<Usuario?> = callbackFlow {
        val listener = db.collection("usuarios").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val usuario = snapshot?.toObject(Usuario::class.java)?.copy(id = snapshot.id)
                trySend(usuario)
            }
        awaitClose { listener.remove() }
    }
    // --- ¡¡FIN DE LA FUNCIÓN AÑADIDA!! ---

    suspend fun crearUsuarioEnAuthYFirestore(
        email: String,
        nombre: String,
        apellido: String = "",
        rol: RolUsuario,
        entrenadorId: String,
        password: String,
        telefono: String? = null,
        whatsapp: String? = null,
        peso: Double? = null,
        estatura: Double? = null,
        tipo: TipoAlumno
    ): Usuario? {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: return null
        val nuevoUsuario = Usuario(
            id = firebaseUser.uid,
            nombre = nombre,
            apellido = apellido,
            email = email,
            telefono = telefono,
            whatsapp = whatsapp,
            rol = rol,
            idEntrenador = entrenadorId,
            peso = peso,
            estatura = estatura,
            tipo = tipo
        )
        db.collection("usuarios").document(firebaseUser.uid).set(nuevoUsuario).await()
        return nuevoUsuario
    }

    suspend fun getUsuarioById(userId: String): Usuario? {
        return try {
            val document = db.collection("usuarios").document(userId).get().await()
            document.toObject(Usuario::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun actualizarDatosUsuario(userId: String, datos: Map<String, Any?>) {
        db.collection("usuarios").document(userId)
            .set(datos, SetOptions.merge())
            .await()
    }

    // --- FUNCIONES DE ALUMNOS ---
    suspend fun getAlumnosByEntrenadorSuspend(entrenadorId: String): List<Usuario> {
        if (entrenadorId.isBlank()) {
            return emptyList()
        }
        return try {
            val snapshot = db.collection("usuarios")
                .whereEqualTo("rol", RolUsuario.ALUMNO.name)
                .whereEqualTo("idEntrenador", entrenadorId)
                .get()
                .await()
            snapshot.documents.mapNotNull { document ->
                document.toObject(Usuario::class.java)?.copy(id = document.id)
            }
        } catch (e: Exception) {
            println("Error al obtener alumnos: ${e.message}")
            emptyList()
        }
    }

    // --- FUNCIONES DE EJERCICIOS ---
    fun getCatalogoEjerciciosFlow(): Flow<List<Ejercicio>> = callbackFlow {
        val listener = db.collection("ejercicios")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val ejercicios = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Ejercicio::class.java)?.copy(id = document.id)
                } ?: emptyList()
                trySend(ejercicios)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getEjercicioById(ejercicioId: String): Ejercicio? {
        return try {
            val document = db.collection("ejercicios").document(ejercicioId).get().await()
            document.toObject(Ejercicio::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addEjercicio(ejercicio: Ejercicio) {
        db.collection("ejercicios").add(ejercicio).await()
    }

    suspend fun updateEjercicio(ejercicioId: String, ejercicio: Ejercicio) {
        db.collection("ejercicios").document(ejercicioId).set(ejercicio).await()
    }

    // --- FUNCIONES DE RUTINA ---
    fun getRutinaDeAlumnoFlow(alumnoId: String): Flow<List<DiaEntrenamiento>> = callbackFlow {
        val listener = db.collection("usuarios").document(alumnoId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val rutina = snapshot?.toObject(Usuario::class.java)?.rutina ?: emptyList()
                trySend(rutina)
            }
        awaitClose { listener.remove() }
    }

    suspend fun guardarRutinaDeAlumno(alumnoId: String, nuevaRutina: List<DiaEntrenamiento>) {
        try {
            val data = mapOf("rutina" to nuevaRutina)
            db.collection("usuarios").document(alumnoId)
                .set(data, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            println("Error al guardar rutina: ${e.message}")
        }
    }

    // --- ¡¡INICIO DE LA LÓGICA FINAL PARA HORARIOS!! ---

    /**
     * Guarda o actualiza la hora de entrenamiento de un alumno presencial para un día específico.
     * La hora se guarda como un simple texto en el perfil del alumno.
     */
    suspend fun asignarHoraPresencial(
        alumnoId: String,
        diaSemana: String,
        hora: String // Recibimos el texto de la hora directamente
    ) {
        val alumnoRef = db.collection("usuarios").document(alumnoId)

        // Creamos el objeto que se guardará
        val nuevoHorario = HorarioPresencial(dia = diaSemana, hora = hora)

        // Obtenemos los horarios que ya tenía el alumno
        val documento = alumnoRef.get().await()
        val horariosPrevios = documento.toObject(Usuario::class.java)?.horariosPresenciales ?: emptyList()

        val horariosActualizados = horariosPrevios.toMutableList().apply {
            // Quitamos la asignación previa para ese día para poder reemplazarla.
            removeAll { it.dia == diaSemana }
            // Solo añadimos el nuevo horario si no está en blanco
            if (hora.isNotBlank()) {
                add(nuevoHorario)
            }
        }

        // Actualizamos la lista de horarios del alumno en Firestore. ¡Y listo!
        alumnoRef.update("horariosPresenciales", horariosActualizados).await()
    }

    // --- ¡¡FIN DE LA LÓGICA FINAL PARA HORARIOS!! ---
}
