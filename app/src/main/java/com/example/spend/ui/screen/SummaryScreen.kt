package com.example.spend.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.example.spend.R
import com.example.spend.getFormattedAmount
import com.example.spend.getMonthStart
import com.example.spend.getSunday
import com.example.spend.getTodayStart
import com.example.spend.ui.navigation.RouteNumbers
import com.example.spend.ui.navigation.Routes
import com.example.spend.ui.viewmodel.SummaryViewModel
import kotlinx.coroutines.launch

private val options = listOf("Day", "Week", "Month")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    navHostController: NavHostController,
    viewModel: SummaryViewModel = hiltViewModel()
) {
    var index by remember { mutableIntStateOf(1) }
    val total = 2

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val drawerScope = rememberCoroutineScope()

    val income by viewModel.getIncomeByTime().collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()

    AppNavigationDrawer(
        currentScreenIndex = RouteNumbers.SUMMARY_SCREEN.screenNumber,
        navHostController = navHostController,
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = stringResource(R.string.summary),
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
        ) { innerPadding ->
            val selectedIndex by viewModel.selectedIndex.collectAsState()
            val uiState by viewModel.uiState.collectAsState()

            val expenseSlices by remember(selectedIndex) {
                when (selectedIndex) {
                    0 -> viewModel.getExpenseByCategory(getTodayStart())
                    1 -> viewModel.getExpenseByCategory(getSunday())
                    2 -> viewModel.getExpenseByCategory(getMonthStart())
                    else -> viewModel.getExpenseByCategory(getMonthStart())
                }
            }.collectAsState()

            val incomeSlices by remember(selectedIndex) {
                when (selectedIndex) {
                    0 -> viewModel.getIncomeByCategory(getTodayStart())
                    1 -> viewModel.getIncomeByCategory(getSunday())
                    2 -> viewModel.getIncomeByCategory(getMonthStart())
                    else -> viewModel.getIncomeByCategory(getMonthStart())
                }
            }.collectAsState()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(innerPadding)
            ) {
                Box(
                    modifier = Modifier.verticalScroll(state = rememberScrollState())
                ) {
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SegmentedControl(
                            options = options,
                            selectedIndex = selectedIndex,
                            onSegmentSelected = { viewModel.updateIndex(it) },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Spacer(Modifier.padding(8.dp))
                        InfoBar(
                            balance = uiState.income - uiState.expense,
                            expense = uiState.expense,
                            currencySymbol = currencySymbol
                        )
                        Spacer(Modifier.padding(16.dp))
                        Text(
                            text = stringResource(R.string.analytics),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(Modifier.padding(8.dp))
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                                .background(color = MaterialTheme.colorScheme.background)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceAround,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                when (index) {
                                    1 -> ExpensesByCategoryGraph(slices = expenseSlices)
                                    else -> IncomeByCategoryGraph(slices = incomeSlices)
                                }
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    IconButton(onClick = {
                                        index = if (index == 1) total else index - 1
                                    }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                                            contentDescription = stringResource(R.string.previous)
                                        )
                                    }
                                    ScrollIndicator(index = index, total = total, size = 4.dp)
                                    IconButton(onClick = {
                                        index = if (index == total) 1 else index + 1
                                    }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                                            contentDescription = stringResource(R.string.next)
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.padding(8.dp))
                        Text(
                            text = stringResource(R.string.top_categories),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(Modifier.padding(8.dp))
                        when (index) {
                            1 -> CategoryList(
                                slices = expenseSlices,
                                total = uiState.expense,
                                currencySymbol = currencySymbol,
                                navHostController = navHostController
                            )

                            else -> CategoryList(
                                slices = incomeSlices,
                                total = income,
                                currencySymbol = currencySymbol,
                                navHostController = navHostController
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoBar(
    balance: Double,
    expense: Double,
    currencySymbol: String
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.tertiary, shape = RoundedCornerShape(24.dp))
            .padding(16.dp)
            .animateContentSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.total_balance),
                color = Color(0xFF4CAF50),
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = "$currencySymbol ${getFormattedAmount(balance)}",
                color = Color(0xFF4CAF50),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.total_expense),
                color = Color(0xFFF44336),
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = "$currencySymbol ${getFormattedAmount(expense)}",
                color = Color(0xFFF44336),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ScrollIndicator(index: Int, total: Int, size: Dp, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.onBackground,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(8.dp)
    ) {
        for (i in 1..total) {
            Box(
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(if (i == index) size * 2 else size)
                        .clip(CircleShape)
                        .background(color = MaterialTheme.colorScheme.background)
                )
            }
        }
    }
}

@Composable
private fun CategoryList(
    slices: List<PieChartData.Slice>,
    total: Double,
    currencySymbol: String,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    if (slices.isNotEmpty()) {
        Column(modifier = modifier) {
            slices.forEach { slice ->
                CategoryView(
                    slice = slice,
                    currencySymbol = currencySymbol,
                    total = total,
                    modifier = modifier
                )
            }
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier.padding(all = 48.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.chart),
                    tint = Color.Gray,
                    contentDescription = null,
                    modifier = Modifier.size(size = 100.dp)
                )
            }
            Text(
                text = stringResource(R.string.no_transactions_category_main_message),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.no_transaction_category_extended_message),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Light
            )
            Spacer(modifier = Modifier.height(height = 16.dp))
            FilledTonalButton(
                onClick = { navHostController.navigate(Routes.AddScreen) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(R.string.button_message),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.background
                    )
                }
            }
            Spacer(modifier = Modifier.height(height = 16.dp))
        }
    }
}

@Composable
private fun CategoryView(
    slice: PieChartData.Slice,
    currencySymbol: String,
    total: Double,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = slice.color,
                    shape = RoundedCornerShape(16.dp)
                )
                .size(size = 55.dp)
        )
        Spacer(modifier.width(16.dp))
        Text(text = slice.label, style = MaterialTheme.typography.labelLarge)
        Spacer(modifier.weight(weight = 1f))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${getFormattedAmount((slice.value / total) * 100)} %",
                color = slice.color,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Light
            )
            Text(
                text = "$currencySymbol ${getFormattedAmount(slice.value.toDouble())}",
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
private fun ExpensesByCategoryGraph(
    slices: List<PieChartData.Slice>,
    modifier: Modifier = Modifier
) {
    if (slices.isNotEmpty()) {
        Box(contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                PieChart(
                    pieChartData = PieChartData(
                        slices = slices,
                        plotType = PlotType.Pie
                    ),
                    pieChartConfig = PieChartConfig(
                        showSliceLabels = false,
                        isClickOnSliceEnabled = false,
                        chartPadding = 0,
                        backgroundColor = MaterialTheme.colorScheme.background
                    ),
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.background)
                )
            }
        }
    } else {
        NoTransactionsMessage(modifier = modifier)
    }
}

@Composable
fun NoTransactionsMessage(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.padding(all = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.piggy_bank),
                    tint = Color.Gray,
                    modifier = Modifier.size(size = 100.dp),
                    contentDescription = null
                )
            }
            Text(
                text = stringResource(R.string.no_transactions_main_message),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.no_transactions_extended_message),
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
private fun IncomeByCategoryGraph(
    slices: List<PieChartData.Slice>,
    modifier: Modifier = Modifier
) {
    if (slices.isNotEmpty()) {
        PieChart(
            pieChartData = PieChartData(
                slices = slices,
                plotType = PlotType.Pie
            ),
            pieChartConfig = PieChartConfig(
                showSliceLabels = false,
                isClickOnSliceEnabled = false,
                chartPadding = 0,
                backgroundColor = MaterialTheme.colorScheme.background
            ),
            modifier = modifier
                .background(color = MaterialTheme.colorScheme.background)
        )
    } else {
        NoTransactionsMessage(modifier = modifier)
    }
}