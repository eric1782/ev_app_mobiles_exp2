package com.example.proyecto20.model

import java.util.Date

data class Progreso(
    val idUsuario: String,
    val idEjercicio: String,
    val fecha: Date,
    val pesoLogradoKg: Double,
    val repeticionesLogradas: String
)
