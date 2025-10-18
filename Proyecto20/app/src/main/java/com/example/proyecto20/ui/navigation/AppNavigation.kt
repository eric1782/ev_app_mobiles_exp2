package com.example.proyecto20.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyecto20.data.MockData
import com.example.proyecto20.model.RolUsuario
import com.example.proyecto20.ui.screens.*

object AppRoutes {
    const val LOGIN_SCREEN = "login"
    const val HOME_SCREEN = "home/{userId}"
    const val CALENDARIO_ENTRENADOR_SCREEN = "calendario_entrenador/{entrenadorId}"
    const val ALUMNO_DETAIL_SCREEN = "alumno_detail/{alumnoId}"
    const val ADD_EJERCICIO_SCREEN = "addEjercicio"
    const val LISTA_EJERCICIOS_SCREEN = "lista_ejercicios"
    const val EJERCICIO_DETAIL_SCREEN = "ejercicioDetail/{ejercicioId}"
    const val MIS_ALUMNOS_SCREEN = "mis_alumnos/{entrenadorId}"
    const val ADD_ALUMNO_SCREEN = "add_alumno"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppRoutes.LOGIN_SCREEN) {

        composable(AppRoutes.LOGIN_SCREEN) {
            var hasLoginError by remember { mutableStateOf(false) }
            LoginScreen(
                onLoginClick = { email, password ->
                    hasLoginError = false
                    val user = MockData.todosLosUsuarios.find {
                        it.email.equals(email, ignoreCase = true) && it.password == password
                    }
                    if (user != null) {
                        val route = AppRoutes.HOME_SCREEN.replace("{userId}", user.id)
                        navController.navigate(route) {
                            popUpTo(AppRoutes.LOGIN_SCREEN) { inclusive = true }
                        }
                    } else {
                        hasLoginError = true
                    }
                },
                hasError = hasLoginError
            )
        }

        composable(
            route = AppRoutes.HOME_SCREEN,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                val user = MockData.todosLosUsuarios.find { it.id == userId }
                if (user != null) {
                    when (user.rol) {
                        RolUsuario.ENTRENADOR -> EntrenadorMainScreen(
                            navController = navController,
                            entrenadorId = user.id,
                            onAlumnoClick = { alumnoId ->
                                val route = AppRoutes.ALUMNO_DETAIL_SCREEN.replace("{alumnoId}", alumnoId)
                                navController.navigate(route)
                            },
                            onAddAlumnoClick = {
                                navController.navigate(AppRoutes.ADD_ALUMNO_SCREEN)
                            }
                        )
                        RolUsuario.ALUMNO -> AlumnoMainScreen(
                            alumnoId = user.id,
                            onNavigateToEjercicioDetail = { ejercicioId ->
                                val route = AppRoutes.EJERCICIO_DETAIL_SCREEN.replace("{ejercicioId}", ejercicioId)
                                navController.navigate(route)
                            }
                        )
                    }
                }
            }
        }

        composable(
            route = AppRoutes.ALUMNO_DETAIL_SCREEN,
            arguments = listOf(navArgument("alumnoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val alumnoId = backStackEntry.arguments?.getString("alumnoId")
            if (alumnoId != null) {
                AlumnoDetailScreen_EntrenadorView(
                    alumnoId = alumnoId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = AppRoutes.CALENDARIO_ENTRENADOR_SCREEN,
            arguments = listOf(navArgument("entrenadorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val entrenadorId = backStackEntry.arguments?.getString("entrenadorId")
            if (entrenadorId != null) {
                CalendarioEntrenadorScreen(
                    entrenadorId = entrenadorId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToRutina = { alumnoId ->
                        val route = AppRoutes.ALUMNO_DETAIL_SCREEN.replace("{alumnoId}", alumnoId)
                        navController.navigate(route)
                    }
                )
            } else {
                navController.popBackStack()
            }
        }

        composable(AppRoutes.LISTA_EJERCICIOS_SCREEN) {
            MisEjerciciosScreen(navController = navController, onNavigateBack = { navController.popBackStack() })
        }
        composable(AppRoutes.ADD_EJERCICIO_SCREEN) {
            AddEjercicioScreen(
                onNavigateBack = { navController.popBackStack() },
                onSave = { nombre, descripcion, musculo, url ->
                    MockData.addEjercicioCompletoAlCatalogo(nombre, descripcion, musculo, url ?: "")
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = AppRoutes.EJERCICIO_DETAIL_SCREEN,
            arguments = listOf(navArgument("ejercicioId") { type = NavType.StringType })
        ) { backStackEntry ->
            val ejercicioId = backStackEntry.arguments?.getString("ejercicioId")
            if (ejercicioId != null) {
                EjercicioDetailScreen(ejercicioId = ejercicioId, onNavigateBack = { navController.popBackStack() })
            }
        }
        composable(
            route = AppRoutes.MIS_ALUMNOS_SCREEN,
            arguments = listOf(navArgument("entrenadorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val entrenadorId = backStackEntry.arguments?.getString("entrenadorId")
            if (entrenadorId != null) {
                MisAlumnosScreen(
                    navController = navController,
                    entrenadorId = entrenadorId,
                    onAddAlumnoClick = {
                        navController.navigate(AppRoutes.ADD_ALUMNO_SCREEN)
                    }
                )
            }
        }

        // --- BLOQUE CORREGIDO Y DEFINITIVO ---
        composable(AppRoutes.ADD_ALUMNO_SCREEN) {
            // Buscamos el ID del entrenador de forma más robusta.
            // Primero, intentamos obtener 'userId' (si venimos de EntrenadorMainScreen).
            // Si es nulo, usamos el operador Elvis (?:) para intentar obtener 'entrenadorId' (si venimos de MisAlumnosScreen).
            val arguments = navController.previousBackStackEntry?.arguments
            val entrenadorId = arguments?.getString("userId") ?: arguments?.getString("entrenadorId")

            if (entrenadorId != null) {

                CrearAlumnoScreen(
                    onNavigateBack = { navController.popBackStack() },
                    // --- CAMBIO: La firma de la función ahora incluye el tipo de cliente ---
                    onSaveAlumno = { nombre, email, pass, peso, estatura, tipo, rutina ->
                        MockData.crearAlumnoCompleto(
                            nombre = nombre,
                            email = email,
                            pass = pass,
                            peso = peso,
                            estatura = estatura,
                            tipoCliente = tipo, // <-- Pasamos el tipo al MockData
                            rutinaMap = rutina,
                            idEntrenador = entrenadorId
                        )
                        navController.popBackStack()
                    }
                )
            } else {
                // Si, a pesar de todo, no encontramos el ID, volvemos para evitar un crash.
                navController.popBackStack()
            }
        }
    }
}
