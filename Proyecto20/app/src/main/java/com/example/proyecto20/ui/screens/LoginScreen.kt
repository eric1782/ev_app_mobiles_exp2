// Ruta: app/src/main/java/com/example/proyecto20/ui/screens/LoginScreen.kt

package com.example.proyecto20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyecto20.ui.navigation.AppRoutes
import com.example.proyecto20.ui.viewmodels.AuthState
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    navController: NavController,
    authState: AuthState,
    onLoginClick: (String, String) -> Unit,
    onLoginSuccess: () -> Unit,
    onDismissError: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoading = authState is AuthState.Loading

    // --- ¡LA CLAVE ESTÁ AQUÍ! ---
    // Este `LaunchedEffect` se ejecuta cada vez que el `authState` cambia.
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            onLoginSuccess()
        }
    }

    // Composable para mostrar el diálogo de error
    if (authState is AuthState.Error) {
        AlertDialog(
            onDismissRequest = onDismissError,
            title = { Text("Error de Autenticación") },
            text = { Text(authState.message) },
            confirmButton = {
                Button(onClick = onDismissError) {
                    Text("Entendido")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Iniciar Sesión", style = MaterialTheme.typography.headlineLarge)

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { onLoginClick(email, password) },
                enabled = !isLoading, // Desactivar el botón mientras carga
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Ingresar")
                }
            }

            TextButton(onClick = { navController.navigate(AppRoutes.REGISTRO_ENTRENADOR_SCREEN) }) {
                Text("¿No tienes cuenta? Regístrate como entrenador")
            }
        }
    }
}
