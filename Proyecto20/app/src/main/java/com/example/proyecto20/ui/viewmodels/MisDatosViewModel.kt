package com.example.proyecto20.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto20.data.FirebaseRepository
import com.example.proyecto20.model.RolUsuario
import com.example.proyecto20.model.TipoAlumno
import com.example.proyecto20.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MisDatosUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val usuario: Usuario? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class MisDatosViewModel(private val userId: String) : ViewModel() {

    private val _uiState = MutableStateFlow(MisDatosUiState())
    val uiState: StateFlow<MisDatosUiState> = _uiState

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            try {
                val usuario = FirebaseRepository.getUsuarioById(userId)
                _uiState.update { it.copy(isLoading = false, usuario = usuario) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "No se pudieron cargar los datos."
                    )
                }
            }
        }
    }

    fun guardarCambios(
        nombre: String,
        apellido: String,
        telefono: String,
        whatsapp: String,
        pesoTexto: String,
        estaturaTexto: String,
        tipoAlumno: TipoAlumno?
    ) {
        val usuarioActual = _uiState.value.usuario ?: return

        val peso = pesoTexto.toDoubleOrNull()
        val estatura = estaturaTexto.toDoubleOrNull()

        val datosActualizados = mutableMapOf<String, Any?>(
            "nombre" to nombre.trim(),
            "apellido" to apellido.trim(),
            "telefono" to telefono.trim().ifBlank { null },
            "whatsapp" to whatsapp.trim().ifBlank { null }
        )

        if (usuarioActual.rol == RolUsuario.ALUMNO) {
            datosActualizados["peso"] = peso
            datosActualizados["estatura"] = estatura
            tipoAlumno?.let { datosActualizados["tipo"] = it }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
            try {
                FirebaseRepository.actualizarDatosUsuario(userId, datosActualizados)
                val usuarioActualizado = FirebaseRepository.getUsuarioById(userId)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        usuario = usuarioActualizado,
                        successMessage = "Datos actualizados correctamente."
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = e.message ?: "No se pudieron guardar los cambios."
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    class Factory(private val userId: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MisDatosViewModel::class.java)) {
                return MisDatosViewModel(userId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

