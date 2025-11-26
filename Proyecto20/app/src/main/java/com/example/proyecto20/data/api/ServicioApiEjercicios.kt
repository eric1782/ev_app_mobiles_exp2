package com.example.proyecto20.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Servicio para interactuar con wger API
 * Base URL: https://wger.de/api/v2/
 * 
 * API gratuita, código abierto, soporte multiidioma (incluyendo español)
 * Documentación: https://wger.de/en/software/api
 * 
 * Nota: wger API devuelve resultados paginados, por lo que necesitamos manejar la paginación
 */
interface ServicioApiEjercicios {
    
    /**
     * Busca ejercicios por nombre (soporta español)
     * @param nombre Nombre del ejercicio (puede ser parcial, en español o inglés)
     * @param idioma Idioma (2 = español, 1 = inglés)
     * @param limite Límite de resultados
     * @return Respuesta paginada con lista de ejercicios
     */
    @GET("exercise/search/")
    suspend fun buscarEjercicios(
        @Query("term") nombre: String,
        @Query("language") idioma: Int = 2,  // 2 = español
        @Query("limit") limite: Int = 20
    ): Response<RespuestaBusquedaEjercicios>
}

/**
 * Respuesta de búsqueda de ejercicios (wger usa paginación)
 */
data class RespuestaBusquedaEjercicios(
    val suggestions: List<SugerenciaEjercicio>? = null
)

/**
 * Sugerencia de ejercicio en la búsqueda
 */
data class SugerenciaEjercicio(
    val value: String,  // Nombre del ejercicio
    val data: DatosSugerenciaEjercicio? = null
)

/**
 * Datos adicionales de la sugerencia
 */
data class DatosSugerenciaEjercicio(
    val id: Int? = null,
    val name: String? = null
)

