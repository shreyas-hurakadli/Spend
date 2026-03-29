package com.example.spend.ui.screen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.example.spend.R
import com.example.spend.ui.navigation.RouteNumbers
import com.example.spend.ui.screen.currency.CurrencyBottomSheet
import com.example.spend.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

private val timeFormats = listOf("12h", "24h")

@Composable
fun SettingsScreen(
    navHostController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val drawerScope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        val activity = context as Activity
        viewModel.onPermissionRequestDismissed(
            permissionPermanentlyDenied = !it &&
                    !ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
        )
    }
    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { directory ->
        directory?.let {
            viewModel.exportCsvFiles(directory = it)
        }
    }

    val showNotificationRequestPermissionDialog by viewModel.showNotificationRequestPermissionDialog.collectAsState()
    val showDeleteDialogBox by viewModel.showDeleteDialogBox.collectAsState()
    val notificationPermissionTurnedOn by viewModel.notificationPermissionTurnedOn.collectAsState()
    val showToast by viewModel.showToast.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val selectedTimeFormat by viewModel.selectedTimeFormat.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()
    val currency by viewModel.currency.collectAsState()
    val showCurrencySheet by viewModel.showCurrencySheet.collectAsState()

    LaunchedEffect(key1 = lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            viewModel.openSettingsEvent.collect {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                context.startActivity(intent)
            }
        }
    }

    LaunchedEffect(key1 = showToast) {
        if (showToast && toastMessage.isNotBlank()) {
            showToast(message = toastMessage, context = context)
            viewModel.onToastShown()
        }
    }

    LaunchedEffect(key1 = showNotificationRequestPermissionDialog) {
        if (showNotificationRequestPermissionDialog) {
            launcher.launch(input = Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    AppNavigationDrawer(
        currentScreenIndex = RouteNumbers.SETTINGS_SCREEN.screenNumber,
        navHostController = navHostController,
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = stringResource(R.string.settings),
                    hasNavigationDrawer = true,
                    onNavigationDrawerClick = {
                        drawerScope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    }
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(paddingValues = innerPadding)
                    .padding(all = 8.dp)
                    .verticalScroll(state = rememberScrollState())
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "NOTIFICATIONS",
                        style = MaterialTheme.typography.labelMedium
                    )
                    SettingTile(
                        name = stringResource(id = R.string.notification_setting),
                        icon = ImageVector.vectorResource(id = R.drawable.notifications),
                        description = stringResource(id = R.string.notification_setting_message),
                        action = {
                            Switch(
                                checked = notificationPermissionTurnedOn,
                                onCheckedChange = {
                                    viewModel.toggleShowNotificationRequestPermissionDialog(
                                        turnOn = it
                                    )
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                                    checkedBorderColor = MaterialTheme.colorScheme.primary,
                                    uncheckedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.primary,
                                    uncheckedBorderColor = MaterialTheme.colorScheme.primary,
                                )
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(height = 16.dp))
                    Text(
                        text = "PREFERENCES",
                        style = MaterialTheme.typography.labelMedium
                    )
                    SettingTile(
                        name = stringResource(id = R.string.time_format),
                        icon = ImageVector.vectorResource(id = R.drawable.time),
                        description = stringResource(id = R.string.time_format_message),
                        action = {
                            Row(
                                modifier = Modifier
                                    .padding(end = 6.dp)
                                    .background(
                                        color = Color.LightGray,
                                        shape = RoundedCornerShape(size = 8.dp)
                                    )
                                    .padding(all = 4.dp)
                            ) {
                                timeFormats.forEach { timeFormat ->
                                    Text(
                                        text = timeFormat,
                                        color = if (selectedTimeFormat == timeFormat) MaterialTheme.colorScheme.onBackground else Color.Gray,
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(
                                                color = if (selectedTimeFormat == timeFormat) MaterialTheme.colorScheme.background else Color.Transparent,
                                                shape = RoundedCornerShape(size = 8.dp)
                                            )
                                            .padding(vertical = 8.dp)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) { viewModel.changeTimeFormat(timeFormat) }
                                    )
                                }
                            }
                        }
                    )
                    SettingTile(
                        name = stringResource(id = R.string.currency),
                        icon = ImageVector.vectorResource(id = R.drawable.money),
                        description = stringResource(id = R.string.currency_setting_message),
                        clickable = true,
                        onClick = { viewModel.toggleShowCurrencySheet() },
                        action = {
                            Text(
                                text = "$currency ($currencySymbol)",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(height = 16.dp))
                    Text(
                        text = "DATA MANAGEMENT",
                        style = MaterialTheme.typography.labelMedium
                    )
                    SettingTile(
                        name = stringResource(R.string.export_csv),
                        icon = ImageVector.vectorResource(id = R.drawable.download),
                        description = "",
                        clickable = true,
                        onClick = { folderPickerLauncher.launch(input = null) },
                        action = {}
                    )
                    SettingTile(
                        name = "Reset All Data",
                        icon = ImageVector.vectorResource(id = R.drawable.delete),
                        description = "",
                        clickable = true,
                        onClick = { viewModel.toggleShowDeleteDialogBox() },
                        action = {}
                    )
                    Spacer(modifier = Modifier.height(height = 16.dp))
                    Text(
                        text = "ABOUT",
                        style = MaterialTheme.typography.labelMedium
                    )
                    SettingTile(
                        name = "App Version",
                        icon = ImageVector.vectorResource(id = R.drawable.information),
                        description = "",
                        action = {
                            Text(text = "v1.0.0", style = MaterialTheme.typography.labelSmall)
                        }
                    )
                    SettingTile(
                        name = "Privacy Policy",
                        icon = ImageVector.vectorResource(id = R.drawable.policy),
                        description = "",
                        action = {}
                    )
                }
                if (showDeleteDialogBox) {
                    DialogBox(
                        onDismissRequest = { viewModel.toggleShowDeleteDialogBox() },
                        onConfirmation = {
                            viewModel.resetData()
                            viewModel.toggleShowDeleteDialogBox()
                        },
                        dialogTitle = stringResource(id = R.string.reset_data),
                        dialogText = stringResource(id = R.string.reset_data_message),
                        confirmText = { Text(text = stringResource(id = R.string.confirm)) },
                        dismissText = { Text(text = stringResource(id = R.string.cancel)) }
                    )
                }

                if (showCurrencySheet) {
                    CurrencyBottomSheet(
                        onSelect = { viewModel.onCurrencySelect(currency = "${it.code} ${it.symbol}") },
                        onDismiss = { viewModel.toggleShowCurrencySheet() }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingTile(
    name: String,
    icon: ImageVector,
    description: String,
    modifier: Modifier = Modifier,
    clickable: Boolean = false,
    onClick: () -> Unit = {},
    action: @Composable () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(all = 8.dp)
            .clip(shape = RoundedCornerShape(size = 16.dp))
            .clickable(enabled = clickable, onClick = onClick)
            .fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = RoundedCornerShape(size = 16.dp)
                )
                .size(size = 48.dp)
        ) {
            Icon(
                imageVector = icon,
                tint = MaterialTheme.colorScheme.onBackground,
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.width(width = 8.dp))
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(fraction = 0.7f)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium.copy(
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.LastLineBottom
                    )
                )
            )
            Text(
                text = description,
                fontSize = 12.sp,
                style = TextStyle(
                    lineHeight = 14.sp,
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.FirstLineTop
                    ),
                ),
                overflow = TextOverflow.Clip,
            )
        }
        Spacer(modifier = Modifier.weight(weight = 1f))
        action()
    }
}