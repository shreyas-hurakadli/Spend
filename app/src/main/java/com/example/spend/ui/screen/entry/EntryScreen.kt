package com.example.spend.ui.screen.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.spend.R
import com.example.spend.longToDate
import com.example.spend.ui.navigation.RouteNumbers
import com.example.spend.ui.navigation.Routes
import com.example.spend.ui.screen.AppNavigationDrawer
import com.example.spend.ui.screen.AppTopBar
import com.example.spend.ui.screen.NoTransactions
import com.example.spend.ui.screen.TransactionCard
import com.example.spend.ui.viewmodel.entry.EntryViewModel
import kotlinx.coroutines.launch

@Composable
fun EntryScreen(
    navHostController: NavHostController,
    viewModel: EntryViewModel = hiltViewModel(),
) {
    val list by viewModel.transactions.collectAsState()

    val thereAreEntries by viewModel.thereAreEntries.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()

    val showSnackBar by viewModel.showSnackBar.collectAsState()
    val snackBarMessage by viewModel.snackBarMessage.collectAsState()

    val snackBarScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val drawerScope = rememberCoroutineScope()

    LaunchedEffect(showSnackBar) {
        if (showSnackBar && snackBarMessage.isNotEmpty()) {
            snackBarScope.launch {
                snackBarHostState.showSnackbar(snackBarMessage)
                viewModel.toggleSnackBar()
            }
        }
    }

    AppNavigationDrawer(
        currentScreenIndex = RouteNumbers.ENTRY_PAGE.screenNumber,
        navHostController = navHostController,
        drawerState = drawerState,
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = stringResource(R.string.transactions),
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
            floatingActionButton = {
                if (thereAreEntries) {
                    FloatingActionButton(
                        onClick = { navHostController.navigate(Routes.AddScreen) },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = stringResource(id = R.string.add_entry)
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackBarHostState) }
        ) { innerPadding ->
            if (list.isNotEmpty()) {
                var date = ""
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues = innerPadding)
                        .fillMaxSize()
                        .padding(all = 8.dp)
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
                                currencySymbol = currencySymbol,
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
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(paddingValues = innerPadding)
                        .fillMaxSize()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(all = 8.dp)
                    ) {
                        Spacer(Modifier.weight(0.55f))
                        NoTransactions()
                        Spacer(Modifier.weight(1f))
                        OutlinedButton(
                            onClick = { navHostController.navigate(Routes.AddScreen) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Add")
                        }
                    }
                }
            }
        }
    }
}