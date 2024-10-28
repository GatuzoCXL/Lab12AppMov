package com.example.lab12mapas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.lab12mapas.ui.theme.Lab12MapasTheme
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "TU_API_KEY_AQUI")//hmm
        }
        setContent {
            Lab12MapasTheme {
                MapScreen()
            }
        }
    }
}