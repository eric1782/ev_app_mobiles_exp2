package com.example.proyecto20.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto20.data.api.ClienteApiImagenes
import com.example.proyecto20.data.api.Foto
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para buscar imágenes de ejercicios usando Pexels API
 */
class BuscadorImagenesViewModel : ViewModel() {
    
    private val _fotos = MutableStateFlow<List<Foto>>(emptyList())
    val fotos: StateFlow<List<Foto>> = _fotos.asStateFlow()
    
    private val _estaCargando = MutableStateFlow(false)
    val estaCargando: StateFlow<Boolean> = _estaCargando.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    /**
     * Busca imágenes relacionadas con ejercicios
     * @param terminoBusqueda Término de búsqueda (ej: "push up", "squat", "exercise")
     */
    fun buscarImagenes(terminoBusqueda: String) {
        if (terminoBusqueda.isBlank()) {
            _fotos.value = emptyList()
            return
        }
        
        _estaCargando.value = true
        _error.value = null
        
        viewModelScope.launch {
            try {
                // Agregar "exercise" o "workout" al término de búsqueda para mejores resultados
                val query = if (terminoBusqueda.lowercase().contains("exercise") || 
                               terminoBusqueda.lowercase().contains("workout")) {
                    terminoBusqueda
                } else {
                    "$terminoBusqueda exercise"
                }
                
                val respuesta = ClienteApiImagenes.servicioApiImagenes.buscarFotos(
                    query = query,
                    perPage = 20,
                    page = 1
                )
                
                if (respuesta.isSuccessful) {
                    val fotosEncontradas = respuesta.body()?.fotos ?: emptyList()
                    _fotos.value = fotosEncontradas
                    Log.d("BuscadorImagenesViewModel", "Encontradas ${fotosEncontradas.size} imágenes")
                } else {
                    val mensajeError = when (respuesta.code()) {
                        401 -> "API key inválida. Verifica tu API key de Pexels."
                        429 -> "Límite de requests alcanzado. Intenta más tarde."
                        else -> "Error al buscar imágenes: ${respuesta.code()}"
                    }
                    _error.value = mensajeError
                    _fotos.value = emptyList()
                    Log.e("BuscadorImagenesViewModel", "Error: $mensajeError")
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                _fotos.value = emptyList()
                Log.e("BuscadorImagenesViewModel", "Excepción: ${e.message}", e)
            } finally {
                _estaCargando.value = false
            }
        }
    }
    
    /**
     * Limpia los resultados de búsqueda
     */
    fun limpiarBusqueda() {
        _fotos.value = emptyList()
        _error.value = null
    }
}

