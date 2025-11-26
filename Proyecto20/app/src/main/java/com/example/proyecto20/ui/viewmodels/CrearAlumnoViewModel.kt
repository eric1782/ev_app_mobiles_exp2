package com.example.proyecto20.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto20.data.FirebaseRepository
import com.example.proyecto20.model.RolUsuario
import com.example.proyecto20.model.TipoAlumno
import kotlinx.coroutines.launch

class CrearAlumnoViewModel(private val entrenadorId: String) : ViewModel() {

    // Función para crear el alumno. Será llamada desde la pantalla.
    fun crearAlumno(
        email: String,
        nombre: String,
        apellido: String,
        pass: String,
        telefono: String?,
        whatsapp: String?,
        peso: String?,
        estatura: String?,
        tipo: TipoAlumno,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Llamamos a la función del repositorio para crear el usuario
                // con el rol de ALUMNO y el ID del entrenador actual.
                val nuevoUsuario = FirebaseRepository.crearUsuarioEnAuthYFirestore(
                    email = email,
                    nombre = nombre,
                    apellido = apellido,
                    rol = RolUsuario.ALUMNO,
                    entrenadorId = entrenadorId,
                    password = pass,
                    telefono = telefono?.takeIf { it.isNotBlank() },
                    whatsapp = whatsapp?.takeIf { it.isNotBlank() },
                    peso = peso?.takeIf { it.isNotBlank() }?.toDoubleOrNull(),
                    estatura = estatura?.takeIf { it.isNotBlank() }?.toDoubleOrNull(),
                    tipo = tipo
                )

                if (nuevoUsuario != null) {
                    onResult(true, null) // Éxito
                } else {
                    onResult(false, "No se pudo crear el usuario en Firestore.")
                }
            } catch (e: Exception) {
                onResult(false, e.message) // Error (ej: email ya en uso)
            }
        }
    }

    // Factory para pasar el entrenadorId
    class Factory(private val entrenadorId: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CrearAlumnoViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CrearAlumnoViewModel(entrenadorId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
