package com.example.proyecto20.ui.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto20.data.FirebaseRepository
import com.example.proyecto20.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estados para el flujo de autenticación general
sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: Usuario) : AuthState()
    data class Error(val message: String) : AuthState()
}

// Estados específicos para el cambio de contraseña
sealed class PasswordChangeState {
    object Idle : PasswordChangeState()
    object Loading : PasswordChangeState()
    object Success : PasswordChangeState()
    data class Error(val message: String) : PasswordChangeState()
}

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState = _authState.asStateFlow()

    // StateFlow para el cambio de contraseña
    private val _passwordChangeState = MutableStateFlow<PasswordChangeState>(PasswordChangeState.Idle)
    val passwordChangeState = _passwordChangeState.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // getCurrentUser ahora devuelve nuestro objeto Usuario completo
                val user = FirebaseRepository.getCurrentUser()
                _authState.value = if (user != null) AuthState.Authenticated(user) else AuthState.Unauthenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error al verificar usuario")
            }
        }
    }

    fun login(email: String, pass: String, context: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // 1. Iniciar sesión en Firebase Auth
                val firebaseUser = FirebaseRepository.login(email, pass)

                if (firebaseUser != null) {
                    // --- ¡¡CORRECCIÓN APLICADA AQUÍ!! ---
                    // 2. Si el login es exitoso, usamos el UID para obtener nuestro objeto Usuario completo de Firestore.
                    val usuarioCompleto = FirebaseRepository.getUsuarioById(firebaseUser.uid)

                    if (usuarioCompleto != null) {
                        // 3. Ahora sí pasamos el objeto Usuario al estado Authenticated.
                        _authState.value = AuthState.Authenticated(usuarioCompleto)
                    } else {
                        // Esto pasaría si el usuario existe en Auth pero no en la base de datos de Firestore.
                        throw Exception("No se encontraron los datos del usuario.")
                    }
                } else {
                    throw Exception("Usuario o contraseña incorrectos")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error al iniciar sesión")
                // El Toast fue removido para que sea la UI (la Screen) la que muestre el error.
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            FirebaseRepository.logout()
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun dismissError() {
        // Al descartar un error de login, volvemos a Unauthenticated
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Unauthenticated
        }
    }

    // --- NUEVAS FUNCIONES PARA CAMBIAR CONTRASEÑA ---

    fun changePassword(oldPass: String, newPass: String) {
        viewModelScope.launch {
            _passwordChangeState.value = PasswordChangeState.Loading
            try {
                FirebaseRepository.changePassword(oldPass, newPass)
                _passwordChangeState.value = PasswordChangeState.Success
            } catch (e: Exception) {
                _passwordChangeState.value = PasswordChangeState.Error(e.message ?: "Error al cambiar la contraseña")
            }
        }
    }

    fun resetPasswordChangeState() {
        _passwordChangeState.value = PasswordChangeState.Idle
    }
}
