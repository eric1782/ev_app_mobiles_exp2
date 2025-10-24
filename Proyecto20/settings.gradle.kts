// Archivo: settings.gradle.kts

// ESTE BLOQUE ES LA SOLUCIÓN. Le dice a Gradle dónde buscar los plugins.
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal() // El portal de plugins de Gradle. ¡Este es el que resuelve el error!
    }
}

// Este bloque le dice a Gradle dónde buscar las librerías (dependencias).
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Proyecto20"
include(":app")
