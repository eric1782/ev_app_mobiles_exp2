package com.example.proyecto20.data.api

import com.google.gson.annotations.SerializedName

/**
 * Modelos de datos para la API de ejercicios
 */

/**
 * Respuesta de ejercicio desde la API
 */
data class RespuestaEjercicioApi(
    @SerializedName("name")
    val nombre: String,  // Nombre del ejercicio
    
    @SerializedName("type")
    val tipo: String? = null,  // Tipo: strength, cardio, etc.
    
    @SerializedName("muscle")
    val musculo: String? = null,  // Músculo principal
    
    @SerializedName("equipment")
    val equipo: String? = null,  // Equipo necesario
    
    @SerializedName("difficulty")
    val dificultad: String? = null,  // Dificultad
    
    @SerializedName("instructions")
    val instrucciones: String? = null  // Instrucciones
)

/**
 * Información de calorías calculada para un ejercicio
 */
data class InformacionCaloriasEjercicio(
    val nombreEjercicio: String,
    val caloriasPorMinuto: Double,  // Calorías por minuto
    val caloriasTotales: Double,  // Calorías totales para el ejercicio
    val duracionMinutos: Double,  // Duración estimada en minutos
    val met: Double  // Valor MET usado para el cálculo
)

/**
 * Información de calorías para un día de entrenamiento
 */
data class InformacionCaloriasDia(
    val dia: String,
    val caloriasTotales: Double,
    val ejercicios: List<InformacionCaloriasEjercicio>
)

