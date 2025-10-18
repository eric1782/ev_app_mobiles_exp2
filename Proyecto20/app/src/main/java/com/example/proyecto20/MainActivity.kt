package com.example.proyecto20

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyecto20.ui.navigation.AppNavigation // Importante: Llama al sistema de navegación
import com.example.proyecto20.ui.theme.Proyecto20Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Proyecto20Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // El punto de entrada de la app ahora es el sistema de navegación
                    AppNavigation()
                }
            }
        }
    }
}

// La vista previa muestra la navegación, que es el punto de entrada
@Preview(showBackground = true)
@Composable
fun AppPreview() {
    Proyecto20Theme {
        AppNavigation()
    }
}
