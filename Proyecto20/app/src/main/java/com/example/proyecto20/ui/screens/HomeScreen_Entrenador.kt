package com.example.proyecto20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyecto20.data.MockData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen_Entrenador(
    navController: NavController,
    userId: String
) {
    val entrenador = MockData.todosLosUsuarios.find { it.id == userId }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Panel de Entrenador") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bienvenido, ${entrenador?.nombre ?: "Entrenador"}",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Button(
                onClick = { navController.navigate("misAlumnos/$userId") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Gestionar Mis Alumnos")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("misEjercicios") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Gestionar Catálogo de Ejercicios")
            }
        }
    }
}
