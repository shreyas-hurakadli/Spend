package com.example.spend.ui.screen.entry

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.spend.R
import com.example.spend.longToDate
import com.example.spend.ui.navigation.Routes
import com.example.spend.ui.screen.AppTopBar
import com.example.spend.ui.screen.TransactionCard
import com.example.spend.ui.viewmodel.entry.EntryViewModel
import kotlinx.coroutines.launch

@Composable
fun EntryScreen(
    navHostController: NavHostController,
    viewModel: EntryViewModel = hiltViewModel(),
) {
    val list by viewModel.transactions.collectAsState()

    val showSnackBar by viewModel.showSnackBar.collectAsState()
    val snackBarMessage by viewModel.snackBarMessage.collectAsState()

    val snackBarScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(showSnackBar) {
        if (showSnackBar && snackBarMessage.isNotEmpty()) {
            snackBarScope.launch {
                snackBarHostState.showSnackbar(snackBarMessage)
                viewModel.toggleSnackBar()
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.transactions),
                canNavigateBack = true,
                onBackClick = { navHostController.popBackStack() },
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->
        var date = ""
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(8.dp)
        ) {
            items(items = list) { entryCategory ->
                Column {
                    if (date != longToDate(entryCategory.entry.epochSeconds)) {
                        date = longToDate(entryCategory.entry.epochSeconds)
                        Text(
                            text = longToDate(entryCategory.entry.epochSeconds),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Start,
                        )
                        Spacer(Modifier.padding(top = 8.dp))
                    }
                    TransactionCard(
                        entryCategory = entryCategory,
                        iconTint = Color.Black,
                        clickable = true,
                        onClick = {
                            viewModel.selectEntry(entryCategory)
                            navHostController.navigate(Routes.EntryDetailScreen)
                        }
                    )
                }
            }
        }
    }
}