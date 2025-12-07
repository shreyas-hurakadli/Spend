package com.example.spend.ui.screen

import android.util.Log
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.example.spend.R
import com.example.spend.getFormattedAmount
import com.example.spend.getLocalCurrencySymbol
import com.example.spend.getMonthStart
import com.example.spend.getSunday
import com.example.spend.getTodayStart
import com.example.spend.ui.navigation.RouteNumbers
import com.example.spend.ui.theme.SpendTheme
import com.example.spend.ui.viewmodel.SummaryViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private val options = listOf("Day", "Week", "Month")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    navHostController: NavHostController,
    viewModel: SummaryViewModel = hiltViewModel()
) {
    var index by remember { mutableIntStateOf(1) }
    val thereAreEntries by viewModel.transactionsPresent().collectAsState()
    val total = 2

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val drawerScope = rememberCoroutineScope()

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

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(innerPadding)
            ) {
                if (thereAreEntries) {
                    Box(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
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
                                balance = uiState.balance,
                                expense = uiState.expense
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
                                        1 -> ExpensesByCategoryGraph(
                                            slices = when (selectedIndex) {
                                                0 -> viewModel.getExpenseByCategory(getTodayStart())
                                                1 -> viewModel.getExpenseByCategory(getSunday())
                                                2 -> viewModel.getExpenseByCategory(getMonthStart())
                                                else -> viewModel.getExpenseByCategory(getMonthStart())
                                            }
                                        )

                                        else -> IncomeByCategoryGraph(
                                            slices = when (selectedIndex) {
                                                0 -> viewModel.getIncomeByCategory(getTodayStart())
                                                1 -> viewModel.getIncomeByCategory(getSunday())
                                                2 -> viewModel.getIncomeByCategory(getMonthStart())
                                                else -> viewModel.getIncomeByCategory(getMonthStart())
                                            }
                                        )
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
                        }
                    }
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
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
                                balance = uiState.balance,
                                expense = uiState.expense
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
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = stringResource(R.string.no_transactions),
                                    style = MaterialTheme.typography.headlineSmall
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
private fun InfoBar(
    balance: Double,
    expense: Double
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
                text = "${getLocalCurrencySymbol()} ${getFormattedAmount(balance)}",
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
                text = "${getLocalCurrencySymbol()} ${getFormattedAmount(expense)}",
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
fun LegendGrid(
    slices: List<PieChartData.Slice>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(if (slices.size == 1) 1 else 2),
        userScrollEnabled = false,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .heightIn(min = 0.dp, max = (((slices.size + (slices.size % 2)) / 2) * 50).dp)
            .padding(bottom = 16.dp)
    ) {
        itemsIndexed(slices) { index, slice ->
            Row(
                horizontalArrangement = if (index % 2 == 0) Arrangement.Start else Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(slice.color)
                        .size(30.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = slice.label,
                    style = MaterialTheme.typography.labelSmall,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ExpensesByCategoryGraph(
    slices: StateFlow<List<PieChartData.Slice>>,
    modifier: Modifier = Modifier
) {
    val data by slices.collectAsState()
    Log.d("SummaryScreen", data.toString())

    if (data.isNotEmpty()) {
        Box(contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LegendGrid(data)
                PieChart(
                    pieChartData = PieChartData(
                        slices = data,
                        plotType = PlotType.Pie
                    ),
                    pieChartConfig = PieChartConfig(
                        isAnimationEnable = true,
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
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_expenses))
        }
    }
}

@Composable
private fun IncomeByCategoryGraph(
    slices: StateFlow<List<PieChartData.Slice>>,
    modifier: Modifier = Modifier
) {
    val data by slices.collectAsState()

    if (data.isNotEmpty()) {
        LegendGrid(data)
        PieChart(
            pieChartData = PieChartData(
                slices = data,
                plotType = PlotType.Pie
            ),
            pieChartConfig = PieChartConfig(
                isAnimationEnable = true,
                showSliceLabels = false,
                isClickOnSliceEnabled = false,
                chartPadding = 0,
                backgroundColor = MaterialTheme.colorScheme.background
            ),
            modifier = modifier
                .background(color = MaterialTheme.colorScheme.background)
        )
    } else {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_income))
        }
    }
}

@Preview
@Composable
private fun SummaryScreenPreview() {
    SpendTheme {
        SummaryScreen(navHostController = rememberNavController())
    }
}