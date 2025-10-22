package com.example.proyecto20.util

import androidx.compose.ui.graphics.Color

// Enum para representar las categorías de IMC de forma segura.
enum class CategoriaIMC {
    BAJO_PESO_SEVERO,
    BAJO_PESO,
    NORMAL,
    SOBREPESO,
    OBESIDAD_I,
    OBESIDAD_II,
    OBESIDAD_III // Obesidad Mórbida
}

// Data class para contener el resultado completo del análisis de IMC.
data class ResultadoIMC(
    val valor: Double,
    val categoria: CategoriaIMC,
    val nombreCategoria: String,
    val color: Color
)

object UtilidadesIMC {
    /**
     * Calcula el IMC y devuelve un objeto ResultadoIMC completo con categoría, nombre y color.
     */
    fun calcularYClasificar(pesoKg: Double?, alturaCm: Int?): ResultadoIMC? {
        if (pesoKg == null || pesoKg <= 0 || alturaCm == null || alturaCm <= 0) {
            return null
        }
        val alturaEnMetros = alturaCm / 100.0
        val imc = pesoKg / (alturaEnMetros * alturaEnMetros)

        val (categoria, nombreCategoria, color) = when {
            imc < 16.0 -> Triple(CategoriaIMC.BAJO_PESO_SEVERO, "Bajo Peso Severo", Color.Red)
            imc < 18.5 -> Triple(CategoriaIMC.BAJO_PESO, "Bajo Peso", Color(0xFFFFA726)) // Naranja
            imc < 25.0 -> Triple(CategoriaIMC.NORMAL, "Peso Normal", Color(0xFF66BB6A)) // Verde
            imc < 30.0 -> Triple(CategoriaIMC.SOBREPESO, "Sobrepeso", Color(0xFFFFA726)) // Naranja
            imc < 35.0 -> Triple(CategoriaIMC.OBESIDAD_I, "Obesidad Grado I", Color.Red)
            imc < 40.0 -> Triple(CategoriaIMC.OBESIDAD_II, "Obesidad Grado II", Color.Red)
            else -> Triple(CategoriaIMC.OBESIDAD_III, "Obesidad Mórbida", Color.Red)
        }

        return ResultadoIMC(imc, categoria, nombreCategoria, color)
    }
}
