package com.example.spend.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.spend.R
import com.example.spend.ui.navigation.Routes
import com.example.spend.ui.theme.SpendTheme
import com.example.spend.ui.viewmodel.ExpenseViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.data.ExtraStore.Key
import com.patrykandpatrick.vico.core.common.shape.CorneredShape.Companion.rounded
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ExpensesScreen(
    navHostController: NavHostController,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    var index by remember { mutableIntStateOf(1) }
    val thereAreEntries by viewModel.transactionsPresent().collectAsState()
    val total = 4
    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.expenses)
            ) {
                IconButton(onClick = { navHostController.navigate(Routes.AddScreen) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_entry)
                    )
                }
            }
        },
        bottomBar = {
            AppBottomBar(
                currentScreenIndex = 1,
                onClick = { navHostController.navigate(Routes.HomeScreen) },
            )
        },
        modifier = Modifier.safeContentPadding()
    ) { innerPadding ->
        val selectedIndex = viewModel.selectedIndex.collectAsState()
        val uiState by viewModel.uiState.collectAsState()

        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SegmentedControl(
                    selectedIndex = selectedIndex.value,
                    onSegmentSelected = { viewModel.updateIndex(it) },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(Modifier.padding(8.dp))
                InfoBar(
                    balance = uiState.balance.toString(),
                    expense = uiState.expense.toString()
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
                        .padding(16.dp)
                        .background(color = MaterialTheme.colorScheme.background)
                ) {
                    if (thereAreEntries) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceAround
                        ) {
                            when (index) {
                                1 -> ExpensesGraph(viewModel.getAllExpenseAmount())
                                2 -> IncomeGraph(viewModel.getAllIncomeAmount())
                                3 -> ExpensesByCategoryGraph(viewModel.getExpenseByCategory())
                                4 -> IncomeByCategoryGraph(viewModel.getIncomeByCategory())
                            }
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
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text("No transactions")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScrollIndicator(index: Int, total: Int, size: Dp, modifier: Modifier = Modifier) {
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
fun ExpensesGraph(
    list: StateFlow<List<Double>>,
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val expenses by list.collectAsState()
    LaunchedEffect(expenses) {
        Log.d("ExpenseScreen", "Expenses")
        if (expenses.isNotEmpty()) {
            modelProducer.runTransaction {
                lineSeries { series(expenses) }
            }
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Expenses vs Time", color = MaterialTheme.colorScheme.onBackground)
        CartesianChartHost(
            rememberCartesianChart(
                rememberLineCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(
                    label = rememberTextComponent(
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    label = rememberTextComponent(
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                ),
            ),
            modelProducer,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun IncomeGraph(
    list: StateFlow<List<Double>>,
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val income by list.collectAsState()
    LaunchedEffect(income) {
        Log.d("ExpenseScreen", "Income")
        if (income.isNotEmpty()) {
            modelProducer.runTransaction {
                lineSeries { series(income) }
            }
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Income vs Time", color = MaterialTheme.colorScheme.onBackground)
        CartesianChartHost(
            rememberCartesianChart(
                rememberLineCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(
                    label = rememberTextComponent(
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    label = rememberTextComponent(
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                ),
            ),
            modelProducer,
            modifier = modifier.padding(8.dp)
        )
    }
}

@Composable
fun ExpensesByCategoryGraph(map: StateFlow<Map<String, Double>>, modifier: Modifier = Modifier) {
    val data by map.collectAsState()
    val labelListKey = Key<List<String>>()
    val modelProducer = remember { CartesianChartModelProducer() }
    val valueFormatter = CartesianValueFormatter { context, x, _ ->
        context.model.extraStore[labelListKey][x.toInt()]
    }

    LaunchedEffect(data) {
        Log.d("ExpenseScreen", "ExpensesByCategory")
        if (data.isNotEmpty()) {
            modelProducer.runTransaction {
                columnSeries { series(data.values.toList()) }
                extras { it[labelListKey] = data.keys.toList() }
            }
        }
    }

    val customColumnProvider = object : ColumnCartesianLayer.ColumnProvider {
        override fun getColumn(
            entry: ColumnCartesianLayerModel.Entry,
            seriesIndex: Int,
            extraStore: ExtraStore
        ) = LineComponent(
            fill = fill(Color(0xFF44D43B)),
            shape = rounded(allDp = 12f),
            thicknessDp = 24.0F
        )

        override fun getWidestSeriesColumn(
            seriesIndex: Int,
            extraStore: ExtraStore
        ) = LineComponent(
            fill = fill(Color(0xFF44D43B)),
            shape = rounded(allDp = 12f),
            thicknessDp = 24.0F
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Expenses vs Category", color = MaterialTheme.colorScheme.onBackground)
        CartesianChartHost(
            rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider = customColumnProvider
                ),
                startAxis = VerticalAxis.rememberStart(
                    label = rememberTextComponent(
                        color = MaterialTheme.colorScheme.onTertiary
                    ),
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    label = rememberTextComponent(
                        color = MaterialTheme.colorScheme.onTertiary
                    ),
                    valueFormatter = valueFormatter
                ),
            ),
            modelProducer,
            modifier = modifier.padding(8.dp)
        )
    }
}

@Composable
fun IncomeByCategoryGraph(map: StateFlow<Map<String, Double>>, modifier: Modifier = Modifier) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val data by map.collectAsState()
    val labelListKey = Key<List<String>>()
    val valueFormatter = CartesianValueFormatter { context, x, _ ->
        context.model.extraStore[labelListKey][x.toInt()]
    }
    LaunchedEffect(data) {
        Log.d("ExpenseScreen", "IncomeByCategory")
        if (data.isNotEmpty()) {
            modelProducer.runTransaction {
                columnSeries { series(data.values.toList()) }
                extras { it[labelListKey] = data.keys.toList() }
            }
        }
    }
    val customColumnProvider = object : ColumnCartesianLayer.ColumnProvider {
        override fun getColumn(
            entry: ColumnCartesianLayerModel.Entry,
            seriesIndex: Int,
            extraStore: ExtraStore
        ) = LineComponent(
            fill = fill(Color(0xFF44D43B)),
            shape = rounded(allDp = 12f),
            thicknessDp = 24.0F
        )

        override fun getWidestSeriesColumn(
            seriesIndex: Int,
            extraStore: ExtraStore
        ) = LineComponent(
            fill = fill(Color(0xFF44D43B)),
            shape = rounded(allDp = 12f),
            thicknessDp = 24.0F
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Income vs Category", color = MaterialTheme.colorScheme.onBackground)
        CartesianChartHost(
            rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider = customColumnProvider
                ),
                startAxis = VerticalAxis.rememberStart(
                    label = rememberTextComponent(
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    label = rememberTextComponent(
                        color = MaterialTheme.colorScheme.onTertiary
                    ),
                    valueFormatter = valueFormatter
                ),
            ),
            modelProducer,
            modifier = modifier.padding(8.dp)
        )
    }
}

@Preview
@Composable
private fun ExpensesScreenPreview() {
    SpendTheme {
        ExpensesScreen(navHostController = rememberNavController())
    }
}