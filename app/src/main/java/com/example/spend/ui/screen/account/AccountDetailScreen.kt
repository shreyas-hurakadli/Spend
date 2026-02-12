package com.example.spend.ui.screen.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.spend.R
import com.example.spend.getFormattedAmount
import com.example.spend.getLocalCurrencySymbol
import com.example.spend.ui.accountIcons
import com.example.spend.ui.navigation.Routes
import com.example.spend.ui.screen.AppTopBar
import com.example.spend.ui.screen.NoTransactions
import com.example.spend.ui.screen.TransactionCard
import com.example.spend.ui.viewmodel.account.AccountViewModel
import com.example.spend.ui.viewmodel.entry.EntryViewModel

@Composable
fun AccountDetailScreen(
    navHostController: NavHostController,
    viewModel: AccountViewModel = hiltViewModel(),
    entryViewModel: EntryViewModel = hiltViewModel()
) {
    val selectedAccount by viewModel.selectedAccount.collectAsState()
    val selectedAccountTransactions by viewModel.selectedAccountTransactions.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(id = R.string.account_detail),
                canNavigateBack = true,
                onBackClick = { navHostController.popBackStack() },
            )
        }
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(paddingValues = innerPadding)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxSize()
            ) {
                selectedAccount?.let {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .background(
                                color = it.color,
                                shape = RoundedCornerShape(size = 16.dp)
                            )
                            .size(size = 60.dp)
                    ) {
                        it.icon?.let { icon ->
                            Icon(
                                imageVector = ImageVector.vectorResource(id = accountIcons[icon]!!),
                                tint = MaterialTheme.colorScheme.onBackground,
                                contentDescription = null,
                                modifier = Modifier.size(size = 30.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(height = 8.dp))
                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(height = 16.dp))
                    Text(
                        text = "${getLocalCurrencySymbol()} ${getFormattedAmount(value = it.balance)}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(height = 16.dp))
                    Text(
                        text = stringResource(id = R.string.transactions),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(height = 8.dp))
                    if (selectedAccountTransactions.isNotEmpty()) {
                        LazyColumn {
                            items(items = selectedAccountTransactions) { entryCategory ->
                                TransactionCard(
                                    entryCategory = entryCategory,
                                    iconTint = entryCategory.color,
                                    showDate = true,
                                    clickable = true,
                                    onClick = {
                                        entryViewModel.selectEntry(entryCategory)
                                        navHostController.navigate(Routes.EntryDetailScreen)
                                    }
                                )
                            }
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            NoTransactions()
                        }
                    }
                }
                if (selectedAccount == null) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}