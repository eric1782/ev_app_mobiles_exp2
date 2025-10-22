package com.example.proyecto20.ui.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto20.data.FirebaseRepository
import com.example.proyecto20.model.RolUsuario
import com.example.proyecto20.model.TipoAlumno
import com.example.proyecto20.model.Usuario // Importante
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estados que la UI puede observar (Sin cambios)
sealed class CrearAlumnoState {
    object Idle : CrearAlumnoState()
    object Loading : CrearAlumnoState()
    data class Success(val passwordTemporal: String) : CrearAlumnoState()
    data class Error(val message: String) : CrearAlumnoState()
}

class CrearAlumnoViewModel(private val entrenadorId: String) : ViewModel() {

    private val _state = MutableStateFlow<CrearAlumnoState>(CrearAlumnoState.Idle)
    val state = _state.asStateFlow()

    // --- Variables para los campos del formulario ---
    val nombre: MutableState<String> = mutableStateOf("")
    val email: MutableState<String> = mutableStateOf("")
    val tipoAlumno: MutableState<TipoAlumno> = mutableStateOf(TipoAlumno.PRESENCIAL)

    // --- ¡CORRECCIÓN DE TIPO! Ahora son Strings para el TextField, pero los convertiremos ---
    val pesoInput: MutableState<String> = mutableStateOf("")
    val estaturaInput: MutableState<String> = mutableStateOf("")

    fun onTipoAlumnoChange(nuevoTipo: TipoAlumno) {
        tipoAlumno.value = nuevoTipo
    }

    fun guardarAlumno() {
        if (nombre.value.isBlank() || email.value.isBlank()) {
            _state.value = CrearAlumnoState.Error("El nombre y el email son obligatorios.")
            return
        }

        viewModelScope.launch {
            _state.value = CrearAlumnoState.Loading
            try {
                // Generamos una contraseña que le pasaremos al repositorio
                val passwordTemporal = "pwd${(100000..999999).random()}"

                // Llamamos a la función del repositorio que hace TODO el trabajo en Firebase
                val nuevoUsuario = FirebaseRepository.crearUsuarioEnAuthYFirestore(
                    email = email.value.trim(),
                    nombre = nombre.value.trim(),
                    rol = RolUsuario.ALUMNO,
                    entrenadorId = entrenadorId,
                    password = passwordTemporal,
                    // --- ¡CORRECCIÓN DE TIPO! Convertimos el texto a Double ---
                    peso = pesoInput.value.toDoubleOrNull(),
                    estatura = estaturaInput.value.toDoubleOrNull(),
                    tipo = tipoAlumno.value
                )

                if (nuevoUsuario != null) {
                    _state.value = CrearAlumnoState.Success(passwordTemporal)
                } else {
                    _state.value = CrearAlumnoState.Error("No se pudo crear el usuario.")
                }

            } catch (e: Exception) {
                // Captura errores comunes como "email ya en uso"
                _state.value = CrearAlumnoState.Error(e.message ?: "Ocurrió un error desconocido.")
            }
        }
    }

    fun clearState() {
        _state.value = CrearAlumnoState.Idle
    }

    // Factory para poder crear el ViewModel con el ID del entrenador
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
