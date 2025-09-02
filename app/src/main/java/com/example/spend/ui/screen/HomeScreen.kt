package com.example.spend.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.spend.R
import com.example.spend.getFormattedAmount
import com.example.spend.getLocalCurrencySymbol
import com.example.spend.ui.navigation.Routes
import com.example.spend.ui.theme.SpendTheme
import com.example.spend.ui.viewmodel.HomeViewModel
import kotlin.math.abs

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            AppTopBar(title = stringResource(id = R.string.home))
        },
        bottomBar = {
            AppBottomBar(
                currentScreenIndex = 0,
                navHostController
            )
        },
        modifier = Modifier.safeContentPadding()
    ) { innerPadding ->
        val balance by viewModel.balance.collectAsState()
        val transactions by viewModel.transactions.collectAsState()

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                BalanceBar(balance = balance)
                Spacer(Modifier.padding(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.recent_transaction),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = stringResource(R.string.see_all),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .clickable { navHostController.navigate(Routes.EntryScreen) }
                    )
                }

                Spacer(Modifier.padding(8.dp))
                Box(
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    if (transactions.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.no_transactions),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn {
                            items(transactions) { entry ->
                                TransactionCard(
                                    entry = entry,
                                    icon = ImageVector.vectorResource(R.drawable.baseline_pencil),
                                    iconTint = MaterialTheme.colorScheme.onSecondary,
                                    showDate = true,
                                    backgroundColor = MaterialTheme.colorScheme.secondary,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BalanceBar(
    balance: Double,
    modifier: Modifier = Modifier
) {
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                )
                .fillMaxWidth()
                .padding(24.dp),
        ) {
            Column {
                Text(
                    text = stringResource(R.string.total_balance),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelLarge,
                )
                Spacer(Modifier.padding(8.dp))
                Text(
                    text = (if (balance < 0) "- " else "") + "${getLocalCurrencySymbol()} ${
                        getFormattedAmount(
                            value = balance
                        )
                    }",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 24.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreviewLight() {
    SpendTheme {
        HomeScreen(navHostController = rememberNavController())
    }
}