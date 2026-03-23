package com.example.spend.ui.screen.budget

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.spend.R
import com.example.spend.ui.screen.AppTopBar
import com.example.spend.ui.viewmodel.budget.BudgetViewModel

@Composable
fun EditBudgetScreen(
    navHostController: NavHostController,
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val showSnackBar by viewModel.showSnackBar.collectAsState()
    val snackBarMessage by viewModel.snackBarMessage.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = showSnackBar) {
        if (showSnackBar && snackBarMessage.isNotEmpty()) {
            snackBarHostState.showSnackbar(message = snackBarMessage)
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(id = R.string.edit_budget),
                canNavigateBack = true,
                onBackClick = { navHostController.popBackStack() },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { innerPadding ->
    }
}