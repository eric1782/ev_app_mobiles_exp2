package com.example.proyecto20.data.api

import com.google.gson.annotations.SerializedName

/**
 * Modelos de datos para la API de imágenes (Pexels)
 */

/**
 * Respuesta de búsqueda de fotos
 */
data class RespuestaBusquedaFotos(
    @SerializedName("total_results")
    val totalResultados: Int,
    
    @SerializedName("page")
    val pagina: Int,
    
    @SerializedName("per_page")
    val porPagina: Int,
    
    @SerializedName("photos")
    val fotos: List<Foto>
)

/**
 * Información de una foto
 */
data class Foto(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("width")
    val ancho: Int,
    
    @SerializedName("height")
    val alto: Int,
    
    @SerializedName("url")
    val url: String,
    
    @SerializedName("photographer")
    val fotografo: String,
    
    @SerializedName("photographer_url")
    val urlFotografo: String,
    
    @SerializedName("photographer_id")
    val idFotografo: Long,
    
    @SerializedName("src")
    val src: TamanosFoto
)

/**
 * Diferentes tamaños de la foto
 */
data class TamanosFoto(
    @SerializedName("original")
    val original: String,
    
    @SerializedName("large2x")
    val large2x: String,
    
    @SerializedName("large")
    val large: String,
    
    @SerializedName("medium")
    val medium: String,
    
    @SerializedName("small")
    val small: String,
    
    @SerializedName("portrait")
    val portrait: String,
    
    @SerializedName("landscape")
    val landscape: String,
    
    @SerializedName("tiny")
    val tiny: String
)

