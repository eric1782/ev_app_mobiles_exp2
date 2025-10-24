package com.example.proyecto20.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyecto20.model.RolUsuario
import com.example.proyecto20.model.Usuario
import com.example.proyecto20.ui.screens.*
import com.example.proyecto20.ui.viewmodels.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object AppRoutes {
    const val LOGIN_SCREEN = "login"
    const val REGISTRO_ENTRENADOR_SCREEN = "registro_entrenador"
    const val HOME_SCREEN = "home"

    // --- Rutas del Entrenador ---
    const val MIS_ALUMNOS_SCREEN = "mis_alumnos"
    const val ADD_ALUMNO_SCREEN = "add_alumno"
    const val LISTA_EJERCICIOS_SCREEN = "lista_ejercicios"
    const val ADD_EJERCICIO_SCREEN = "add_ejercicio"
    const val EDIT_EJERCICIO_SCREEN = "edit_ejercicio/{ejercicioId}"
    const val CALENDARIO_SCREEN = "calendario_screen"
    const val PLANIFICACION_RUTINA_SCREEN = "planificacion_rutina/{alumnoId}"

    // --- Rutas del Alumno ---
    const val VISUALIZACION_RUTINA_SCREEN = "visualizacion_rutina/{alumnoId}"

    // --- ¡¡INICIO DE LA CORRECCIÓN IMPORTANTE!! ---
    // Se ha corregido la definición de la ruta para que los parámetros sean consistentes
    const val EJERCICIO_DETAIL_SOLO_LECTURA_SCREEN = "ejercicioDetailSoloLectura/{alumnoId}/{ejercicioId}" +
            "?series={series}&repeticiones={repeticiones}&peso={peso}&rir={rir}"
    // --- ¡¡FIN DE LA CORRECCIÓN IMPORTANTE!! ---

    // --- Rutas Comunes ---
    const val EJERCICIO_DETAIL_SCREEN = "ejercicioDetail/{ejercicioId}"
    const val CAMBIAR_PASSWORD_SCREEN = "cambiar_password"

    // --- Nuevas rutas para estadísticas ---
    const val ESTADISTICAS_ALUMNO_SCREEN = "estadisticas_alumno/{alumnoId}"
    const val ESTADISTICAS_DETALLE_SCREEN = "estadisticas_detalle/{alumnoId}/{ejercicioNombre}"
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun AppNavigation() {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()

    when (val state = authState) {
        is AuthState.Loading -> LoadingScreen()
        is AuthState.Authenticated -> AppNavHost(authViewModel = authViewModel, user = state.user)
        is AuthState.Unauthenticated, is AuthState.Error -> AuthNavHost(authViewModel = authViewModel, authState = state)
    }
}

@Composable
fun AppNavHost(authViewModel: AuthViewModel, user: Usuario) {
    val navController = rememberNavController()
    val onLogout = { authViewModel.logout() }

    NavHost(navController = navController, startDestination = AppRoutes.HOME_SCREEN) {
        composable(AppRoutes.HOME_SCREEN) {
            when (user.rol) {
                RolUsuario.ENTRENADOR -> EntrenadorMainScreen(navController, authViewModel, onLogout)
                RolUsuario.ALUMNO -> AlumnoMainScreen(navController, authViewModel, onLogout, user)
            }
        }

        // --- RUTAS DEL ENTRENADOR ---
        composable(AppRoutes.MIS_ALUMNOS_SCREEN) {
            val vm: AlumnosViewModel = viewModel(factory = AlumnosViewModelFactory(user.id))
            MisAlumnosScreen(
                viewModel = vm,
                onNavigateToCrearAlumno = { navController.navigate(AppRoutes.ADD_ALUMNO_SCREEN) },
                onNavigateToPlanificacion = { alumnoId ->
                    navController.navigate(AppRoutes.PLANIFICACION_RUTINA_SCREEN.replace("{alumnoId}", alumnoId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(AppRoutes.ADD_ALUMNO_SCREEN) {
            val vm: CrearAlumnoViewModel = viewModel(factory = CrearAlumnoViewModel.Factory(user.id))
            CrearAlumnoScreen(viewModel = vm, onNavigateBack = { navController.popBackStack() })
        }
        composable(
            route = AppRoutes.PLANIFICACION_RUTINA_SCREEN,
            arguments = listOf(navArgument("alumnoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val alumnoId = backStackEntry.arguments?.getString("alumnoId")
            if (alumnoId != null) {
                val vm: PlanificacionViewModel = viewModel(factory = PlanificacionViewModel.Factory(alumnoId))
                PlanificacionRutinaScreen(
                    viewModel = vm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
        composable(AppRoutes.LISTA_EJERCICIOS_SCREEN) {
            val vm: EjerciciosViewModel = viewModel()
            MisEjerciciosScreen(
                ejerciciosViewModel = vm,
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = AppRoutes.EJERCICIO_DETAIL_SCREEN,
            arguments = listOf(navArgument("ejercicioId") { type = NavType.StringType })
        ) { backStackEntry ->
            val ejercicioId = backStackEntry.arguments?.getString("ejercicioId")
            if (ejercicioId != null) {
                EjercicioDetailScreen(
                    ejercicioId = ejercicioId,
                    navController = navController,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        // --- RUTAS DEL ALUMNO ---
        composable(
            route = AppRoutes.EJERCICIO_DETAIL_SOLO_LECTURA_SCREEN,
            arguments = listOf(
                navArgument("alumnoId") { type = NavType.StringType },
                navArgument("ejercicioId") { type = NavType.StringType },
                navArgument("series") { type = NavType.StringType; nullable = true },
                navArgument("repeticiones") { type = NavType.StringType; nullable = true },
                navArgument("peso") { type = NavType.StringType; nullable = true },
                navArgument("rir") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val alumnoId = backStackEntry.arguments?.getString("alumnoId")
            val ejercicioId = backStackEntry.arguments?.getString("ejercicioId")
            val series = backStackEntry.arguments?.getString("series")
            val repeticiones = backStackEntry.arguments?.getString("repeticiones")
            val peso = backStackEntry.arguments?.getString("peso")
            val rir = backStackEntry.arguments?.getString("rir")
            if (alumnoId != null && ejercicioId != null) {
                EjercicioDetailScreenSoloLectura(
                    alumnoId = alumnoId,
                    ejercicioId = ejercicioId,
                    series = series,
                    repeticiones = repeticiones,
                    peso = peso,
                    rir = rir,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        // --- ¡¡INICIO DEL ARREGLO DEL CRASH!! ---
        composable(
            route = AppRoutes.VISUALIZACION_RUTINA_SCREEN,
            arguments = listOf(navArgument("alumnoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val alumnoId = backStackEntry.arguments?.getString("alumnoId")
            if (alumnoId != null) {
                val vm: PlanificacionViewModel = viewModel(factory = PlanificacionViewModel.Factory(alumnoId))
                VisualizacionRutinaScreen(
                    viewModel = vm,
                    onNavigateBack = { navController.popBackStack() },
                    // Se corrige la lambda para construir la ruta correctamente, igual que en AlumnoMainScreen
                    onNavigateToEjercicioDetail = { ejercicio ->
                        // Codificamos las repeticiones por si contienen caracteres especiales como '/'
                        val encodedRepeticiones = URLEncoder.encode(ejercicio.repeticiones, StandardCharsets.UTF_8.toString())
                        val route = AppRoutes.EJERCICIO_DETAIL_SOLO_LECTURA_SCREEN
                            .replace("{alumnoId}", alumnoId)
                            .replace("{ejercicioId}", ejercicio.ejercicioId)
                            .replace("{series}", ejercicio.series.toString())
                            .replace("{repeticiones}", encodedRepeticiones)
                            .replace("{peso}", ejercicio.peso?.toString() ?: "")
                            .replace("{rir}", ejercicio.rir?.toString() ?: "")
                        navController.navigate(route)
                    }
                )
            }
        }
        // --- ¡¡FIN DEL ARREGLO DEL CRASH!! ---

        // --- NUEVAS RUTAS DE ESTADÍSTICAS ---
        composable(
            route = AppRoutes.ESTADISTICAS_ALUMNO_SCREEN,
            arguments = listOf(navArgument("alumnoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val alumnoId = backStackEntry.arguments?.getString("alumnoId")
            if (alumnoId != null) {
                EstadisticasAlumnoScreen(
                    alumnoId = alumnoId,
                    onNavigateToDetail = { ejercicioNombre ->
                        val encodedNombre = URLEncoder.encode(ejercicioNombre, StandardCharsets.UTF_8.toString())
                        navController.navigate(
                            AppRoutes.ESTADISTICAS_DETALLE_SCREEN
                                .replace("{alumnoId}", alumnoId)
                                .replace("{ejercicioNombre}", encodedNombre)
                        )
                    },
                    onNavigateBack = { navController.popBackStack() } // Se cambia a popBackStack para poder volver
                )
            }
        }
        composable(
            route = AppRoutes.ESTADISTICAS_DETALLE_SCREEN,
            arguments = listOf(
                navArgument("alumnoId") { type = NavType.StringType },
                navArgument("ejercicioNombre") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val alumnoId = backStackEntry.arguments?.getString("alumnoId")
            val ejercicioNombre = backStackEntry.arguments?.getString("ejercicioNombre")
            if (alumnoId != null && ejercicioNombre != null) {
                EstadisticasDetalleScreen(
                    alumnoId = alumnoId,
                    ejercicioNombre = ejercicioNombre,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        // --- OTRAS RUTAS ---
        composable(AppRoutes.CALENDARIO_SCREEN) {
            val vm: CalendarioViewModel = viewModel(factory = CalendarioViewModel.Factory(user.id))
            CalendarioScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlanificacion = { alumnoId ->
                    navController.navigate(AppRoutes.PLANIFICACION_RUTINA_SCREEN.replace("{alumnoId}", alumnoId))
                }
            )
        }
        composable(AppRoutes.ADD_EJERCICIO_SCREEN) {
            val viewModel: EjerciciosViewModel = viewModel()
            AddEjercicioScreen(
                onNavigateBack = { navController.popBackStack() },
                onSave = { nombre, descripcion, musculo, urlVideo ->
                    viewModel.addEjercicio(nombre, descripcion, musculo, urlVideo)
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = AppRoutes.EDIT_EJERCICIO_SCREEN,
            arguments = listOf(navArgument("ejercicioId") { type = NavType.StringType })
        ) { backStackEntry ->
            val ejercicioId = backStackEntry.arguments?.getString("ejercicioId")
            if (ejercicioId != null) {
                val viewModel: EjercicioDetailViewModel = viewModel(factory = EjercicioDetailViewModelFactory(ejercicioId))
                val ejercicio by viewModel.ejercicio.collectAsState()

                if (ejercicio == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                } else {
                    AddEjercicioScreen(
                        ejercicioInicial = ejercicio,
                        onNavigateBack = { navController.popBackStack() },
                        onSave = { nombre, descripcion, musculo, urlVideo ->
                            val ejercicioActualizado = ejercicio!!.copy(
                                nombre = nombre,
                                descripcion = descripcion,
                                musculoPrincipal = musculo,
                                urlVideo = urlVideo ?: ""
                            )
                            viewModel.updateEjercicio(ejercicioActualizado)
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
        composable(AppRoutes.CAMBIAR_PASSWORD_SCREEN) {
            CambiarPasswordScreen(
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun AuthNavHost(authViewModel: AuthViewModel, authState: AuthState) {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = AppRoutes.LOGIN_SCREEN) {
        composable(AppRoutes.LOGIN_SCREEN) {
            LoginScreen(
                navController = navController,
                authState = authState,
                onLoginClick = { email, password -> authViewModel.login(email, password, context) },
                onLoginSuccess = {},
                onDismissError = { authViewModel.dismissError() }
            )
        }
        composable(AppRoutes.REGISTRO_ENTRENADOR_SCREEN) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Pantalla de Registro de Entrenador")
            }
        }
    }
}
