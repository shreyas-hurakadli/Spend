package com.example.spend.ui.screen.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.spend.getFormattedAmount
import com.example.spend.ui.screen.AppTopBar
import com.example.spend.ui.screen.DialogBox
import com.example.spend.ui.screen.NoTransactions
import com.example.spend.ui.screen.TransactionCard
import com.example.spend.ui.viewmodel.budget.BudgetViewModel

@Composable
fun BudgetDetailScreen(
    navHostController: NavHostController,
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val selectedBudget by viewModel.selectedBudget.collectAsState()
    val transactions by viewModel.selectedBudgetTransactions.collectAsState(initial = emptyList())
    val currencySymbol by viewModel.currencySymbol.collectAsState()

    val budget = selectedBudget?.first ?: Budget()
    val expense = selectedBudget?.second ?: 0.00
    val progress = (expense / budget.amount).coerceIn(0.0, 1.0).toFloat()

    var showDialogBox by remember { mutableStateOf(value = false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.budget_detail),
                canNavigateBack = true,
                onBackClick = { navHostController.popBackStack() },
                actions = {
                    IconButton(onClick = { showDialogBox = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = stringResource(R.string.delete)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = if (transactions?.isEmpty() ?: false) Modifier
                .padding(paddingValues = innerPadding)
                .verticalScroll(state = rememberScrollState())
                .fillMaxSize()
            else Modifier
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
                    text = stringResource(id = R.string.expense),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                BasicText(
                    text = "$currencySymbol ${getFormattedAmount(value = expense)}",
                    maxLines = 1,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = 24.sp,
                        maxFontSize = 48.sp
                    )
                )
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
                            text = stringResource(id = R.string.spent),
                            color = Color.Gray,
                            style = MaterialTheme.typography.labelSmall
                        )
                        BasicText(
                            text = "$currencySymbol ${getFormattedAmount(value = expense)}",
                            maxLines = 1,
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            autoSize = TextAutoSize.StepBased(
                                minFontSize = 8.sp,
                                maxFontSize = 24.sp
                            )
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.limit),
                            color = Color.Gray,
                            style = MaterialTheme.typography.labelSmall
                        )
                        BasicText(
                            text = "$currencySymbol ${getFormattedAmount(value = budget.amount)}",
                            maxLines = 1,
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            autoSize = TextAutoSize.StepBased(
                                minFontSize = 8.sp,
                                maxFontSize = 24.sp
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(height = 16.dp))
                Text(
                    text = stringResource(R.string.transactions),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(alignment = Alignment.Start)
                )
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
                    val entryCategoryTransactions = transactions
                    if (entryCategoryTransactions?.isNotEmpty() ?: false) {
                        LazyColumn {
                            items(items = entryCategoryTransactions) { entryCategory ->
                                TransactionCard(
                                    entryCategory = entryCategory,
                                    currencySymbol = currencySymbol,
                                    iconTint = Color.Black,
                                    showDate = true,
                                )
                            }
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            NoTransactions()
                        }
                    }
                }
                if (showDialogBox) {
                    DialogBox(
                        onDismissRequest = { showDialogBox = false },
                        onConfirmation = {
                            navHostController.popBackStack()
                            viewModel.deleteBudget(selectedBudget?.first ?: Budget())
                        },
                        dialogTitle = stringResource(id = R.string.delete_budget),
                        dialogText = stringResource(id = R.string.budget_delete_message),
                        confirmText = {
                            Text(
                                text = stringResource(id = R.string.delete),
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        dismissText = { Text(text = stringResource(id = R.string.cancel)) },
                    )
                }
            }
        }
    }
}