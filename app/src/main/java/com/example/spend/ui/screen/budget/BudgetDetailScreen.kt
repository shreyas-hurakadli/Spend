package com.example.spend.ui.screen.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.spend.R
import com.example.spend.data.room.budget.Budget
import com.example.spend.getLocalCurrencySymbol
import com.example.spend.ui.screen.AppTopBar
import com.example.spend.ui.screen.TransactionCard
import com.example.spend.ui.viewmodel.budget.BudgetViewModel

@Composable
fun BudgetDetailScreen(
    navHostController: NavHostController,
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val selectedBudget by viewModel.selectedBudget.collectAsState()
    val transactions by viewModel.selectedBudgetTransactions.collectAsState(initial = emptyList())

    val budget = selectedBudget?.first ?: Budget()
    val expense = selectedBudget?.second ?: 0.00
    val remaining = budget.amount - expense
    val progress = (expense / budget.amount).coerceIn(0.0, 1.0).toFloat()

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.budget_detail),
                canNavigateBack = true,
                onBackClick = { navHostController.popBackStack() },
            )
        }
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxSize()
            ) {
                Text(
                    text = budget.name,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.fillMaxSize(fraction = 0.08f))
                Text(
                    text = "REMAINING",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = getLocalCurrencySymbol() + expense.toInt().toString(),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "%.2f".format(remaining - remaining.toInt())
                            .substring(startIndex = 1),
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.fillMaxHeight(fraction = 0.05f))
                if (!progress.isNaN()) {
                    LinearProgressIndicator(
                        progress = { progress },
                        color = if (progress >= 1f) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.background,
                        modifier = Modifier
                            .fillMaxWidth(fraction = 0.8f)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onBackground,
                                shape = RoundedCornerShape(size = 16.dp)
                            )
                            .height(height = 24.dp)
                    )
                }
                Spacer(modifier = Modifier.fillMaxHeight(fraction = 0.02f))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Spent",
                            color = Color.Gray,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = getLocalCurrencySymbol() + "%.2f".format(expense),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Limit",
                            color = Color.Gray,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = getLocalCurrencySymbol() + "%.2f".format(budget.amount),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.fillMaxHeight(fraction = 0.05f))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.transactions),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    if (transactions?.isNotEmpty() ?: false) {
                        Text(
                            text = stringResource(R.string.see_all),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .clickable {}
                        )
                    }
                }
                Spacer(modifier = Modifier.fillMaxHeight(fraction = 0.01f))
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(size = 8.dp)
                        )
                        .fillMaxSize(),
                    contentAlignment = if (transactions?.isNotEmpty()
                            ?: false
                    ) Alignment.TopCenter else Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (transactions?.isNotEmpty() ?: false) {
                            val entryCategoryTransactions = transactions?.take(n = 4)
                            entryCategoryTransactions?.forEach { entryCategory ->
                                TransactionCard(
                                    entryCategory = entryCategory,
                                    iconTint = Color.Black,
                                    showDate = true,
                                )
                            }
                        } else {
                            Text(text = stringResource(R.string.no_transactions))
                        }
                    }
                }
            }
        }
    }
}