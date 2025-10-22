package com.example.proyecto20.ui.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto20.data.FirebaseRepository
import com.example.proyecto20.model.Usuario
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Estado de la UI de Autenticación
sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val user: Usuario) : AuthState() // <- El estado contiene el Usuario completo
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // --- LA VARIABLE QUE TUS PANTALLAS ESPERAN ---
    // Deriva el objeto Usuario del estado de autenticación.
    val currentUser: StateFlow<Usuario?> = authState.map {
        when (it) {
            is AuthState.Authenticated -> it.user
            else -> null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
    // ---------------------------------------------

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            FirebaseRepository.getAuthState().collect { firebaseUser ->
                if (firebaseUser != null) {
                    // Si hay usuario de Firebase, cargamos su perfil completo de forma segura
                    val userProfile = FirebaseRepository.getUsuarioById(firebaseUser.uid)
                    if (userProfile != null) {
                        _authState.value = AuthState.Authenticated(userProfile)
                    } else {
                        // Si no se encuentra el perfil, es un error grave. Cerramos sesión.
                        FirebaseRepository.logout()
                        _authState.value = AuthState.Error("No se pudo encontrar el perfil del usuario.")
                    }
                } else {
                    _authState.value = AuthState.Unauthenticated
                }
            }
        }
    }

    fun login(email: String, password: String, context: Context) {
        viewModelScope.launch {
            try {
                FirebaseRepository.login(email, password)
                // El 'collect' del init{} se encargará de actualizar el estado
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Email o contraseña incorrectos"
                _authState.value = AuthState.Error(errorMessage)
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun logout() {
        FirebaseRepository.logout()
    }

    fun dismissError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Unauthenticated
        }
    }
}
