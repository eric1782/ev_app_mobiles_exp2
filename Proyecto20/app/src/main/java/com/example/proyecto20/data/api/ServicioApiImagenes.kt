package com.example.proyecto20.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Servicio para interactuar con Pexels API
 * Documentación: https://www.pexels.com/api/documentation/
 */
interface ServicioApiImagenes {
    
    /**
     * Busca fotos por término de búsqueda
     * @param query Término de búsqueda (ej: "push up", "squat", "exercise")
     * @param perPage Número de resultados por página (máx 80, default 15)
     * @param page Página de resultados (default 1)
     * @return Respuesta con lista de fotos
     */
    @GET("search")
    suspend fun buscarFotos(
        @Query("query") query: String,
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1
    ): Response<RespuestaBusquedaFotos>
}

