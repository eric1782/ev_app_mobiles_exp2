

plugins {
    // Todos los plugins se llaman ahora usando alias del catálogo
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.gms.services) apply false // <--- CAMBIO CLAVE
}
