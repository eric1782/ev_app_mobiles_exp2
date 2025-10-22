// Archivo: Proyecto20/build.gradle.kts (RAÍZ)
// --- CÓDIGO FINAL, CORRECTO Y BASADO EN TU LIBS.VERSIONS.TOML ---

plugins {
    // Usa los alias que SÍ existen en tu libs.versions.toml
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false // <- Usa el alias para Compose

    // Declaramos el plugin de Google Services aquí directamente, ya que no estaba en tu toml
    id("com.google.gms.google-services") version "4.4.2" apply false
}
