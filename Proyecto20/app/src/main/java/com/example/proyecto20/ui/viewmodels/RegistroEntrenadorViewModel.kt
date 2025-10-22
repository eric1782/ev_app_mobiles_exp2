// Ruta: app/src/main/java/com/example/proyecto20/ui/viewmodels/RegistroEntrenadorViewModel.kt

package com.example.proyecto20.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto20.data.FirebaseRepository
import com.example.proyecto20.model.RolUsuario // <-- ¡AÑADIR ESTA IMPORTACIÓN!
import kotlinx.coroutines.launch

class RegistroEntrenadorViewModel : ViewModel() {
    fun registrarEntrenador(nombre: String, email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            // --- ¡LA CORRECCIÓN ESTÁ AQUÍ! ---
            // Pasamos el enum RolUsuario.ENTRENADOR en lugar de un String.
            FirebaseRepository.register(email, pass, nombre, RolUsuario.ENTRENADOR, onResult)
        }
    }
}
