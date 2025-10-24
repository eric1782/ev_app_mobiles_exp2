package com.example.proyecto20.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto20.model.RegistroProgreso
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// --- Imports de Vico temporalmente comentados ---
// import com.patrykandpatrick.vico.core.CartesianChartModel
// import com.patrykandpatrick.vico.core.LineCartesianLayerModel
// import com.patrykandpatrick.vico.core.series

class EstadisticasViewModel(private val alumnoId: String) : ViewModel() {

    private val firestore = Firebase.firestore

    private val _historialAgrupado = MutableStateFlow<Map<String, List<RegistroProgreso>>>(emptyMap())
    val historialAgrupado: StateFlow<Map<String, List<RegistroProgreso>>> = _historialAgrupado.asStateFlow()

    // private val _datosGrafico = MutableStateFlow<CartesianChartModel?>(null)
    // val datosGrafico: StateFlow<CartesianChartModel?> = _datosGrafico.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun cargarHistorialCompletoAgrupado() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = firestore.collection("usuarios").document(alumnoId)
                    .collection("historial_progreso")
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .get()
                    .await()
                val historialCompleto = snapshot.toObjects(RegistroProgreso::class.java)
                _historialAgrupado.value = historialCompleto.groupBy { it.ejercicioNombre }
            } catch (e: Exception) {
                // Manejar error
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Función temporalmente comentada hasta resolver imports de Vico
    /*
    fun prepararDatosParaGrafico(ejercicioNombre: String) {
        val historialDelEjercicio = _historialAgrupado.value[ejercicioNombre] ?: emptyList()
        val datosConPeso = historialDelEjercicio.filter { it.peso != null && it.peso > 0 }

        if (datosConPeso.isNotEmpty()) {
            val entries = datosConPeso.map { it.peso!!.toFloat() }
            _datosGrafico.value = CartesianChartModel(
                LineCartesianLayerModel.build {
                    series(entries)
                }
            )
        } else {
            _datosGrafico.value = null
        }
    }
    */
}

class EstadisticasViewModelFactory(private val alumnoId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EstadisticasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EstadisticasViewModel(alumnoId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

