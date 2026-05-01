package com.shreyashurakadli.budgetwise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.shreyashurakadli.budgetwise.ui.navigation.NavigationManager
import com.shreyashurakadli.budgetwise.ui.theme.SpendTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpendTheme {
                NavigationManager(
                    navHostController = rememberNavController(),
                    intent = intent
                )
            }
        }
    }
}