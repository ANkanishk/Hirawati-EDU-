package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.SchoolAppMainContainer
import com.example.ui.SchoolViewModel

class MainActivity : ComponentActivity() {
    // Standard viewmodel injection of central state holder
    private val viewModel: SchoolViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SchoolAppMainContainer(viewModel)
        }
    }
}
