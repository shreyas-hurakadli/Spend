package com.example.spend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.spend.ui.navigation.NavigationManager
import com.example.spend.ui.permission.RequestPostNotificationPermission
import com.example.spend.ui.theme.SpendTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpendTheme {
                RequestPostNotificationPermission()
                NavigationManager(
                    navHostController = rememberNavController(),
                )
            }
        }
    }
}