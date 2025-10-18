package com.example.proyecto20.model

import java.time.DayOfWeek

enum class RolUsuario {
    ALUMNO,
    ENTRENADOR
}

enum class TipoCliente {
    ONLINE,
    PRESENCIAL
}

data class Usuario(
    val id: String,
    val nombre: String,
    val email: String,
    val password: String,
    val rol: RolUsuario,
    val tipoCliente: TipoCliente? = null,
    val idEntrenadorAsignado: String?,
    val diasEntrenamiento: List<DayOfWeek>? = null
)
