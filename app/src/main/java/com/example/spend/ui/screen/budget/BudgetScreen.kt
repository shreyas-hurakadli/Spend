package com.example.spend.ui.screen.budget

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.spend.R
import com.example.spend.data.room.budget.Budget
import com.example.spend.getLocalCurrencySymbol
import com.example.spend.ui.navigation.RouteNumbers
import com.example.spend.ui.navigation.Routes
import com.example.spend.ui.screen.AppNavigationDrawer
import com.example.spend.ui.screen.AppTopBar
import com.example.spend.ui.viewmodel.budget.BudgetViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.absoluteValue

@Composable
fun BudgetScreen(
    navHostController: NavHostController,
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val drawerScope = rememberCoroutineScope()

    val thereAreBudgets by viewModel.thereAreBudgets.collectAsState()
    val budgets by viewModel.budgets.collectAsState()

    AppNavigationDrawer(
        currentScreenIndex = RouteNumbers.BUDGET_PAGE.screenNumber,
        navHostController = navHostController,
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = stringResource(R.string.budget),
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
                if (thereAreBudgets) {
                    FloatingActionButton(
                        onClick = { navHostController.navigate(Routes.AddBudgetScreen) },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = stringResource(id = R.string.add_budget)
                        )
                    }
                }
            },
        ) { innerPadding ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(paddingValues = innerPadding)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = if (thereAreBudgets) Arrangement.Top
                    else Arrangement.Center,
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .fillMaxSize()
                ) {
                    if (thereAreBudgets) {
                        LazyColumn {
                            items(items = budgets) {
                                BudgetView(
                                    budget = it.first,
                                    expense = it.second,
                                    onClick = {
                                        viewModel.selectBudget(budgetPair = it)
                                        navHostController.navigate(route = Routes.BudgetDetailScreen)
                                    },
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .fillMaxWidth()
                                )
                                Spacer(Modifier.height(16.dp))
                            }
                        }
                    } else {
                        Spacer(Modifier.weight(0.55f))
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.account_wallet),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null,
                                modifier = Modifier.size(200.dp)
                            )
                        }
                        Spacer(Modifier.weight(0.1f))
                        Text(
                            text = stringResource(R.string.budget_message),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.weight(0.05f))
                        Text(
                            text = stringResource(R.string.budget_extended_message),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Light,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.weight(1f))
                        OutlinedButton(
                            onClick = { navHostController.navigate(Routes.AddBudgetScreen) },
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

@Composable
private fun BudgetView(
    budget: Budget,
    expense: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = budget.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${getLocalCurrencySymbol()} ${budget.amount}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(Modifier.height(16.dp))

            val progress = (expense / budget.amount).coerceIn(0.0, 1.0).toFloat()

            LinearProgressIndicator(
                progress = { progress },
                color = if (progress >= 1f) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = RoundedCornerShape(size = 16.dp)
                    )
                    .height(height = 12.dp)
            )

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Spent: ${getLocalCurrencySymbol()} $expense",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = (if (budget.amount >= expense) "Remaining" else "Overspent") + ": ${getLocalCurrencySymbol()} ${(budget.amount - expense).absoluteValue}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (progress > 1f || budget.amount < expense) Color(color = 0xFFF44336)
                    else MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}