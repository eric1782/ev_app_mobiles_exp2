// Ruta: app/src/main/java/com/example/proyecto20/MainActivity.kt
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
import com.example.proyecto20.ui.navigation.AppNavigation // Importamos nuestra navegación
import com.example.proyecto20.ui.theme.Proyecto20Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Proyecto20Theme {
                // La Surface es el contenedor principal de nuestra app.
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // --- ¡CORRECCIÓN APLICADA! ---
                    // La única responsabilidad de MainActivity es llamar a AppNavigation.
                    // AppNavigation ahora es 100% autónomo y maneja su propia lógica.
                    AppNavigation()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    Proyecto20Theme {
        // En la preview, podemos mostrar directamente la navegación.
        // La preview no tendrá un estado real, así que podría mostrar la pantalla de carga o de login.
        AppNavigation()
    }
}
