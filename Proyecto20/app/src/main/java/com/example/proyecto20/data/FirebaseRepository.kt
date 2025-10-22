package com.example.proyecto20.data

import com.example.proyecto20.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

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

    // Esta función `register` es para el registro inicial del entrenador.
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

    suspend fun crearUsuarioEnAuthYFirestore(
        email: String,
        nombre: String,
        rol: RolUsuario,
        entrenadorId: String,
        password: String,
        // --- ¡CAMPOS AÑADIDOS CON TIPO CORRECTO! ---
        peso: Double?,
        estatura: Double?,
        tipo: TipoAlumno
    ): Usuario? {
        // 1. Crear el usuario en Firebase Authentication
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: return null

        // 2. Crear el objeto Usuario con TODOS los datos
        val nuevoUsuario = Usuario(
            id = firebaseUser.uid,
            nombre = nombre,
            email = email,
            rol = rol,
            idEntrenador = entrenadorId,
            // --- ¡CAMPOS AÑADIDOS! ---
            peso = peso,
            estatura = estatura,
            tipo = tipo
        )

        // 3. Guardar el objeto Usuario en Firestore
        db.collection("usuarios").document(firebaseUser.uid).set(nuevoUsuario).await()

        // 4. Devolver el usuario recién creado
        return nuevoUsuario
    }

    // --- FUNCIONES DE USUARIO ---
    suspend fun getUsuarioById(userId: String): Usuario? {
        return try {
            val document = db.collection("usuarios").document(userId).get().await()
            if (!document.exists()) return null

            Usuario(
                id = document.id,
                nombre = document.getString("nombre") ?: "",
                email = document.getString("email") ?: "",
                idEntrenador = document.getString("idEntrenador"),
                rol = try { RolUsuario.valueOf(document.getString("rol") ?: "ALUMNO") } catch (e: Exception) { RolUsuario.ALUMNO },
                rutina = emptyList() // Se carga por separado
            )
        } catch (e: Exception) {
            null
        }
    }

    // --- FUNCIONES DE ALUMNOS ---
    fun getAlumnosByEntrenadorFlow(entrenadorId: String): Flow<List<Usuario>> {
        if (entrenadorId.isBlank()) {
            return flowOf(emptyList())
        }
        return db.collection("usuarios")
            .whereEqualTo("idEntrenador", entrenadorId)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { document ->
                    getUsuarioById(document.id) // Reutilizamos la función segura
                }
            }
    }

    // --- FUNCIONES DE EJERCICIOS ---
    fun getCatalogoEjerciciosFlow(): Flow<List<Ejercicio>> {
        return db.collection("ejercicios")
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { document ->
                    Ejercicio(
                        id = document.id,
                        nombre = document.getString("nombre") ?: "",
                        descripcion = document.getString("descripcion") ?: "",
                        musculoPrincipal = document.getString("musculoPrincipal") ?: "",
                        urlVideo = document.getString("urlVideo") ?: ""
                    )
                }
            }
    }

    suspend fun addEjercicio(nombre: String, descripcion: String, musculo: String, urlVideo: String) {
        val ejercicio = Ejercicio(nombre = nombre, descripcion = descripcion, musculoPrincipal = musculo, urlVideo = urlVideo)
        db.collection("ejercicios").add(ejercicio).await()
    }

    // --- FUNCIONES DE RUTINA ---
    fun getRutinaDeAlumnoFlow(alumnoId: String): Flow<List<DiaEntrenamiento>> {
        return db.collection("usuarios").document(alumnoId)
            .snapshots()
            .map { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val rutinaData = documentSnapshot.get("rutina") as? List<Map<String, Any>> ?: return@map emptyList()

                    rutinaData.mapNotNull { diaMap ->
                        val diaNombre = diaMap["dia"] as? String ?: return@mapNotNull null
                        val ejerciciosData = diaMap["ejercicios"] as? List<Map<String, Any>> ?: emptyList()

                        val ejercicios = ejerciciosData.mapNotNull { ejercicioMap ->
                            EjercicioRutina(
                                ejercicioId = ejercicioMap["ejercicioId"] as? String ?: "",
                                nombre = ejercicioMap["nombre"] as? String ?: "",
                                series = (ejercicioMap["series"] as? Long)?.toInt() ?: 3,
                                repeticiones = ejercicioMap["repeticiones"] as? String ?: "10-12",
                                rir = (ejercicioMap["rir"] as? Long)?.toInt(),
                                peso = ejercicioMap["peso"] as? Double
                            )
                        }
                        DiaEntrenamiento(dia = diaNombre, ejercicios = ejercicios)
                    }
                } else {
                    emptyList()
                }
            }
    }

    suspend fun guardarRutinaDeAlumno(alumnoId: String, nuevaRutina: List<DiaEntrenamiento>) {
        try {
            val data = mapOf("rutina" to nuevaRutina)
            db.collection("usuarios").document(alumnoId)
                .set(data, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            // Manejar error
        }
    }

    // --- FUNCIONES DE CALENDARIO ---
    fun getHorariosEntrenador(entrenadorId: String): Flow<List<BloqueHorario>> {
        return db.collection("horarios")
            .whereEqualTo("entrenadorId", entrenadorId)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { document ->
                    BloqueHorario(
                        id = document.id,
                        entrenadorId = document.getString("entrenadorId") ?: "",
                        idAlumno = document.getString("idAlumno"),
                        horaInicio = document.getString("horaInicio") ?: "",
                        horaFin = document.getString("horaFin") ?: "",
                        disponible = document.getBoolean("disponible") ?: true
                    )
                }
            }
    }
}
