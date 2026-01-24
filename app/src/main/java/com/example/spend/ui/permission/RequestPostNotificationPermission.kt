package com.example.spend.ui.permission

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spend.ui.viewmodel.PermissionViewModel

@Composable
fun RequestPostNotificationPermission(
    firstRequest: Boolean = true,
    viewModel: PermissionViewModel = hiltViewModel()
) {
    val postNotificationPermissionState by viewModel.postNotificationsPermissionState.collectAsState()

    if (!postNotificationPermissionState || !firstRequest) {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { }

        LaunchedEffect(key1 = Unit) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            viewModel.registerPostNotificationsPermission()
        }
    }
}