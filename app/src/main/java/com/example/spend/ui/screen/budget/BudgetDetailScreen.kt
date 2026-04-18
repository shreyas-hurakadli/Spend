package com.example.spend.ui.screen.budget

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.spend.R
import com.example.spend.getFormattedAmount
import com.example.spend.ui.navigation.Routes
import com.example.spend.ui.screen.AppTopBar
import com.example.spend.ui.screen.NoTransactions
import com.example.spend.ui.screen.TransactionCard
import com.example.spend.ui.viewmodel.budget.BudgetDetailViewModel
import kotlin.math.max

@Composable
fun BudgetDetailScreen(
    navHostController: NavHostController,
    viewModel: BudgetDetailViewModel = hiltViewModel(),
) {
    val budget by viewModel.budget.collectAsState()
    val transactions by viewModel.transactions.collectAsState(initial = emptyList())
    val currencySymbol by viewModel.currencySymbol.collectAsState()
    val expenses by viewModel.expenses.collectAsState(initial = 0.00)

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
        Column(
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .padding(all = 8.dp)
                .fillMaxSize()
        ) {
            if (budget == null) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            }
            budget?.let { budget ->
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.weight(weight = 1f)
                ) {
                    item {
                        val progress by remember(
                            key1 = expenses,
                            key2 = budget.amount
                        ) {
                            mutableFloatStateOf(
                                value = (expenses / budget.amount).coerceIn(0.0, 1.0).toFloat()
                            )
                        }
                        Text(
                            text = budget.name,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(height = 16.dp))
                        Text(
                            text = stringResource(id = R.string.expense),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        BasicText(
                            text = "$currencySymbol ${getFormattedAmount(value = expenses)}",
                            maxLines = 1,
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            autoSize = TextAutoSize.StepBased(
                                minFontSize = 24.sp,
                                maxFontSize = 48.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(height = 8.dp))
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
                        Spacer(modifier = Modifier.height(height = 4.dp))
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
                                    text = stringResource(id = R.string.remaining),
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        lineHeight = MaterialTheme.typography.labelSmall.fontSize
                                    ),
                                )

                                BasicText(
                                    text = "$currencySymbol ${
                                        getFormattedAmount(
                                            value = max(a = budget.amount - expenses, b = 0.00)
                                        )
                                    }",
                                    maxLines = 1,
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 1.em
                                    ),
                                    autoSize = TextAutoSize.StepBased(
                                        minFontSize = 8.sp,
                                        maxFontSize = 24.sp
                                    ),
                                )
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = stringResource(id = R.string.limit),
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        lineHeight = MaterialTheme.typography.labelSmall.fontSize
                                    ),
                                )
                                BasicText(
                                    text = "$currencySymbol ${getFormattedAmount(value = budget.amount)}",
                                    maxLines = 1,
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 1.em
                                    ),
                                    autoSize = TextAutoSize.StepBased(
                                        minFontSize = 8.sp,
                                        maxFontSize = 24.sp
                                    ),
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(height = 8.dp))
                        Text(
                            text = stringResource(id = R.string.transactions),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.height(height = 8.dp))
                        if (transactions?.isEmpty() ?: true) {
                            NoTransactions()
                        }
                    }
                    transactions?.let {
                        items(items = it) { entryCategory ->
                            TransactionCard(
                                entryCategory = entryCategory,
                                currencySymbol = currencySymbol,
                                iconTint = Color.Black,
                                showDate = true,
                                clickable = true,
                                onClick = { navHostController.navigate(route = Routes.EntryDetailScreen) }
                            )
                        }
                    }
                }
            }
            OutlinedButton(
                onClick = { navHostController.navigate(Routes.EditBudgetScreen) },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.edit_budget),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
    /*
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
     */
}