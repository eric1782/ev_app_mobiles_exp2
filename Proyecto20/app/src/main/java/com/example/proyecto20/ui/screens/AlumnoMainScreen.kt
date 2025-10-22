package com.example.proyecto20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyecto20.model.Usuario // Importante tener el modelo Usuario
import com.example.proyecto20.ui.navigation.AppRoutes
import com.example.proyecto20.ui.viewmodels.AuthViewModel

@Composable
fun AlumnoMainScreen(
    navController: NavController,
    authViewModel: AuthViewModel, // Lo mantenemos por si se necesita para algo más
    onLogout: () -> Unit,
    user: Usuario // Recibimos el usuario completo directamente
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bienvenido, Alumno",
            style = MaterialTheme.typography.headlineSmall
        )

        // Ya no necesitamos 'user?.let' porque 'user' nunca será nulo aquí
        Text(
            text = user.nombre,
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            // Usamos el id del usuario que recibimos como parámetro
            if (user.id.isNotBlank()) {
                // --- ¡¡MODIFICACIÓN CLAVE!! ---
                // Se cambia la ruta a la nueva pantalla de visualización para el alumno.
                navController.navigate(
                    AppRoutes.VISUALIZACION_RUTINA_SCREEN.replace("{alumnoId}", user.id)
                )
            }
        }) {
            Text("Ver mi Rutina")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onLogout) {
            Text("Cerrar Sesión")
        }
    }
}
