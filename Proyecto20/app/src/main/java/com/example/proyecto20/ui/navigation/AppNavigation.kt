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

object AppRoutes {
    const val LOGIN_SCREEN = "login"
    const val REGISTRO_ENTRENADOR_SCREEN = "registro_entrenador"
    const val HOME_SCREEN = "home"
    const val MIS_ALUMNOS_SCREEN = "mis_alumnos"
    const val CALENDARIO_ENTRENADOR_SCREEN = "calendario_entrenador"
    const val LISTA_EJERCICIOS_SCREEN = "lista_ejercicios"
    const val ADD_ALUMNO_SCREEN = "add_alumno"
    const val PLANIFICACION_RUTINA_SCREEN = "planificacion_rutina/{alumnoId}"
    const val ADD_EJERCICIO_SCREEN = "add_ejercicio"
    const val EJERCICIO_DETAIL_SCREEN = "ejercicioDetail/{ejercicioId}/{alumnoId}"

    // --- ¡MODIFICACIÓN 1: NUEVA RUTA AÑADIDA! ---
    const val VISUALIZACION_RUTINA_SCREEN = "visualizacion_rutina/{alumnoId}"
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun AppNavigation() {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()

    when (val state = authState) {
        is AuthState.Loading -> LoadingScreen()
        is AuthState.Authenticated -> {
            AppNavHost(authViewModel = authViewModel, user = state.user)
        }
        is AuthState.Unauthenticated, is AuthState.Error -> {
            AuthNavHost(authViewModel = authViewModel, authState = state)
        }
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
                RolUsuario.ALUMNO -> {
                    // El `composable` de AlumnoMainScreen ahora sabe a dónde navegar
                    AlumnoMainScreen(
                        navController = navController,
                        authViewModel = authViewModel,
                        onLogout = onLogout,
                        user = user
                    )
                }
            }
        }

        composable(AppRoutes.MIS_ALUMNOS_SCREEN) {
            val alumnosViewModel: AlumnosViewModel = viewModel(factory = AlumnosViewModelFactory(user.id))
            val alumnos by alumnosViewModel.alumnosFiltrados.collectAsState()
            val busqueda by alumnosViewModel.textoBusqueda.collectAsState()

            MisAlumnosScreen(
                alumnos = alumnos,
                textoBusqueda = busqueda,
                onTextoBusquedaChange = { alumnosViewModel.onTextoBusquedaChange(it) },
                onAddAlumnoClick = { navController.navigate(AppRoutes.ADD_ALUMNO_SCREEN) },
                onAlumnoClick = { alumnoId ->
                    navController.navigate("planificacion_rutina/$alumnoId")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = AppRoutes.PLANIFICACION_RUTINA_SCREEN,
            arguments = listOf(navArgument("alumnoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val alumnoId = backStackEntry.arguments?.getString("alumnoId")
            if (alumnoId != null) {
                val planificacionViewModel: PlanificacionViewModel = viewModel(factory = PlanificacionViewModel.Factory(alumnoId))
                PlanificacionRutinaScreen(
                    viewModel = planificacionViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            } else {
                navController.popBackStack()
            }
        }

        // --- ¡MODIFICACIÓN 2: NUEVO COMPOSABLE PARA LA PANTALLA DEL ALUMNO! ---
        composable(
            route = AppRoutes.VISUALIZACION_RUTINA_SCREEN,
            arguments = listOf(navArgument("alumnoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val alumnoId = backStackEntry.arguments?.getString("alumnoId")
            if (alumnoId != null) {
                // Reutilizamos el mismo ViewModel, ya que solo necesitamos cargar los datos
                val planificacionViewModel: PlanificacionViewModel = viewModel(
                    factory = PlanificacionViewModel.Factory(alumnoId)
                )
                // Llamamos a la nueva pantalla de solo lectura
                VisualizacionRutinaScreen(
                    viewModel = planificacionViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEjercicioDetail = { ejercicioId ->
                        // Aquí navegas a la pantalla de detalle del ejercicio.
                        // La ruta ya existe en AppRoutes, así que la usamos.
                        navController.navigate(
                            AppRoutes.EJERCICIO_DETAIL_SCREEN
                                .replace("{ejercicioId}", ejercicioId)
                                .replace("{alumnoId}", alumnoId)
                        )
                    }
                )
            } else {
                navController.popBackStack() // Si no hay ID, vuelve atrás
            }
        }

        composable(AppRoutes.ADD_ALUMNO_SCREEN) {
            val crearAlumnoViewModel: CrearAlumnoViewModel = viewModel(factory = CrearAlumnoViewModel.Factory(user.id))
            CrearAlumnoScreen(
                viewModel = crearAlumnoViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.CALENDARIO_ENTRENADOR_SCREEN) {
            val calendarioViewModel: CalendarioViewModel = viewModel(factory = CalendarioViewModel.Factory(user.id))
            CalendarioEntrenadorScreen(
                viewModel = calendarioViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRutina = { alumnoId ->
                    navController.navigate("planificacion_rutina/$alumnoId")
                }
            )
        }

        composable(AppRoutes.LISTA_EJERCICIOS_SCREEN) {
            val ejerciciosViewModel: EjerciciosViewModel = viewModel()
            MisEjerciciosScreen(
                ejerciciosViewModel = ejerciciosViewModel,
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.ADD_EJERCICIO_SCREEN) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Pantalla para Añadir un Nuevo Ejercicio")
            }
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
                onLoginSuccess = { /* No se usa */ },
                onDismissError = { authViewModel.dismissError() }
            )
        }
        composable(AppRoutes.REGISTRO_ENTRENADOR_SCREEN) {
            // Aquí va tu pantalla de registro
        }
    }
}
