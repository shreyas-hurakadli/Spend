package com.example.spend.ui.screen.entry

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.spend.R
import com.example.spend.ui.screen.AppTopBar
import com.example.spend.ui.viewmodel.entry.EntryViewModel

@Composable
fun EditTransactionScreen(
    navHostController: NavHostController,
    viewModel: EntryViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(id = R.string.edit_transaction),
                canNavigateBack = true,
                onBackClick = { navHostController.popBackStack() }
            )
        }
    ) { innerPadding ->

    }

}