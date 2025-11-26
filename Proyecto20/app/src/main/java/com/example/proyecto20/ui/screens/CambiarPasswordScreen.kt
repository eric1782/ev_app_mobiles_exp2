package com.example.proyecto20.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.proyecto20.ui.viewmodels.AuthViewModel
import com.example.proyecto20.ui.viewmodels.PasswordChangeState // <-- ¡¡IMPORTACIÓN CLAVE AÑADIDA!!

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CambiarPasswordScreen(
    authViewModel: AuthViewModel, // Usamos el AuthViewModel existente
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Observamos el estado del cambio de contraseña desde el ViewModel
    val passwordChangeState by authViewModel.passwordChangeState.collectAsState()

    // Efecto para reaccionar a los cambios de estado (éxito o error)
    LaunchedEffect(passwordChangeState) {
        when (val state = passwordChangeState) {
            is PasswordChangeState.Success -> {
                Toast.makeText(context, "Contraseña cambiada con éxito", Toast.LENGTH_SHORT).show()
                authViewModel.resetPasswordChangeState() // Limpiamos el estado
                onNavigateBack() // Volvemos a la pantalla anterior
            }
            is PasswordChangeState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                authViewModel.resetPasswordChangeState() // Limpiamos el estado para poder reintentar
            }
            is PasswordChangeState.Loading -> { /* Se muestra el indicador de carga */ }
            else -> { /* Estado inicial Idle */ }
        }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = DarkMatterPalette.PrimaryText,
        unfocusedTextColor = DarkMatterPalette.PrimaryText,
        focusedLabelColor = DarkMatterPalette.Highlight,
        unfocusedLabelColor = DarkMatterPalette.SecondaryText,
        focusedBorderColor = DarkMatterPalette.Highlight,
        unfocusedBorderColor = DarkMatterPalette.SecondaryText,
        cursorColor = DarkMatterPalette.Highlight,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent
    )

    DarkMatterBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                DarkMatterTopBar(
                    title = "Cambiar Contraseña",
                    onNavigateBack = onNavigateBack
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text("Contraseña Actual") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nueva Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar Nueva Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )

                Button(
                    onClick = {
                        if (newPassword == confirmPassword) {
                            authViewModel.changePassword(oldPassword, newPassword)
                        } else {
                            Toast.makeText(context, "Las nuevas contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = passwordChangeState !is PasswordChangeState.Loading,
                    colors = DarkMatterButtonColors()
                ) {
                    if (passwordChangeState is PasswordChangeState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                    } else {
                        Text("Confirmar Cambio")
                    }
                }
            }
        }
    }
}
