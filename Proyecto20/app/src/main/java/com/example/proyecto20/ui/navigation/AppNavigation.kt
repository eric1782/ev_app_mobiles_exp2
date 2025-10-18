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
    const val CALENDARIO_ENTRENADOR_SCREEN = "calendario_entrenador"
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
                        // --- CORRECCIÓN APLICADA AQUÍ ---
                        RolUsuario.ALUMNO -> AlumnoMainScreen(
                            // 1. Se pasa el ID del alumno
                            alumnoId = user.id,
                            // 2. Se define la acción para navegar al detalle del ejercicio
                            onNavigateToEjercicioDetail = { ejercicioId ->
                                val route = AppRoutes.EJERCICIO_DETAIL_SCREEN.replace("{ejercicioId}", ejercicioId)
                                navController.navigate(route)
                            }
                        )
                    }
                }
            } else {
                // Si no hay userId, vuelve al login para evitar un estado inconsistente
                navController.popBackStack(AppRoutes.LOGIN_SCREEN, inclusive = false)
            }
        }

        composable(AppRoutes.LISTA_EJERCICIOS_SCREEN) {
            MisEjerciciosScreen(
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
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
            route = AppRoutes.EJERCICIO_DETAIL_SCREEN,
            arguments = listOf(navArgument("ejercicioId") { type = NavType.StringType })
        ) { backStackEntry ->
            val ejercicioId = backStackEntry.arguments?.getString("ejercicioId")
            if (ejercicioId != null) {
                EjercicioDetailScreen(
                    ejercicioId = ejercicioId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        composable(AppRoutes.CALENDARIO_ENTRENADOR_SCREEN) {
            CalendarioEntrenadorScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRutina = { alumnoId ->
                    val route = AppRoutes.ALUMNO_DETAIL_SCREEN.replace("{alumnoId}", alumnoId)
                    navController.navigate(route)
                }
            )
        }


        composable(
            route = AppRoutes.MIS_ALUMNOS_SCREEN,
            arguments = listOf(navArgument("entrenadorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val entrenadorId = backStackEntry.arguments?.getString("entrenadorId")
            if (entrenadorId != null) {
                MisAlumnosScreen(
                    navController = navController,
                    entrenadorId = entrenadorId
                )
            }
        }

        composable(AppRoutes.ADD_ALUMNO_SCREEN) {
            // Placeholder para la pantalla de añadir alumno.
            // Más adelante conectaremos CrearAlumnoScreen aquí.
        }
    }
}
