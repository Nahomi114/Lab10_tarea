package com.example.lab10_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.lab10_1.view.ProductoApp
import com.example.lab10_1.ui.theme.Lab10_1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab10_1Theme {
                // A surface container using the 'background' color from the theme
                Surface {
                    ProductoApp()
                }
            }
        }
    }
}
