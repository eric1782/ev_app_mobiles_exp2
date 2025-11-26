package com.example.proyecto20.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.proyecto20.model.TipoAlumno
import com.example.proyecto20.ui.viewmodels.CrearAlumnoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearAlumnoScreen(
    viewModel: CrearAlumnoViewModel,
    onNavigateBack: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var estatura by remember { mutableStateOf("") }
    var tipoAlumno by remember { mutableStateOf(TipoAlumno.PRESENCIAL) }
    var tipoExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
                    title = "Añadir Nuevo Alumno",
                    onNavigateBack = onNavigateBack
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
                OutlinedTextField(
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = { Text("Apellido") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = textFieldColors
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña (mín. 6 caracteres)") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = textFieldColors
                )
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = textFieldColors
                )
                OutlinedTextField(
                    value = whatsapp,
                    onValueChange = { whatsapp = it },
                    label = { Text("WhatsApp (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = textFieldColors
                )
                OutlinedTextField(
                    value = peso,
                    onValueChange = { peso = it },
                    label = { Text("Peso (kg) - opcional") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = textFieldColors
                )
                OutlinedTextField(
                    value = estatura,
                    onValueChange = { estatura = it },
                    label = { Text("Estatura (cm) - opcional") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = textFieldColors
                )
                
                ExposedDropdownMenuBox(
                    expanded = tipoExpanded,
                    onExpandedChange = { tipoExpanded = !tipoExpanded }
                ) {
                    OutlinedTextField(
                        value = when (tipoAlumno) {
                            TipoAlumno.PRESENCIAL -> "Presencial"
                            TipoAlumno.ONLINE -> "Online"
                        },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de alumno") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoExpanded)
                        },
                        colors = textFieldColors,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = tipoExpanded,
                        onDismissRequest = { tipoExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Presencial") },
                            onClick = {
                                tipoAlumno = TipoAlumno.PRESENCIAL
                                tipoExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Online") },
                            onClick = {
                                tipoAlumno = TipoAlumno.ONLINE
                                tipoExpanded = false
                            }
                        )
                    }
                }

                Button(
                    onClick = {
                        if (nombre.isNotBlank() && email.isNotBlank() && password.length >= 6) {
                            viewModel.crearAlumno(
                                email = email,
                                nombre = nombre,
                                apellido = apellido,
                                pass = password,
                                telefono = telefono.takeIf { it.isNotBlank() },
                                whatsapp = whatsapp.takeIf { it.isNotBlank() },
                                peso = peso.takeIf { it.isNotBlank() },
                                estatura = estatura.takeIf { it.isNotBlank() },
                                tipo = tipoAlumno
                            ) { success, errorMsg ->
                                if (success) {
                                    Toast.makeText(context, "Alumno creado con éxito", Toast.LENGTH_SHORT).show()
                                    onNavigateBack()
                                } else {
                                    Toast.makeText(context, "Error: ${errorMsg ?: "Desconocido"}", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Por favor, rellena los campos obligatorios (Nombre, Email y Contraseña).", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = DarkMatterButtonColors()
                ) {
                    Text("CREAR ALUMNO")
                }
            }
        }
    }
}
