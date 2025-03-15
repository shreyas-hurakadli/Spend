package com.example.spend.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.spend.R
import com.example.spend.data.room.Entry
import com.example.spend.ui.navigation.Routes
import com.example.spend.ui.theme.SpendTheme
import com.example.spend.ui.viewmodel.AppViewModelFactory
import com.example.spend.ui.viewmodel.ExpenseViewModel

@Composable
fun ExpensesScreen(
    navHostController: NavHostController,
    viewModel: ExpenseViewModel = viewModel(factory = AppViewModelFactory.Factory)
) {
    val list = viewModel.list.collectAsState()

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
                CardRow(
                    list = list.value,
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
fun CardRow(
    list: List<Entry>,
    modifier: Modifier = Modifier
) {
    LazyRow(modifier = modifier) {
        itemsIndexed(list) { index, entry ->
            CardInfo(
                index = index,
                entry = entry
            )
        }
    }
}

@Composable
fun CardInfo(
    index: Int,
    entry: Entry,
) {
    Card(
        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, end = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardColors(
            containerColor = if (index and 1 == 0) Color(0xFFF7C948) else Color(0xFF8C6EFF),
            contentColor = if (index and 1 == 0) Color.Black else Color.White,
            disabledContainerColor = Color.White,
            disabledContentColor = Color.White
        )
    ) {
        Text(text = entry.tag)
    }
}

@Preview
@Composable
private fun ExpensesScreenPreview() {
    SpendTheme {
        ExpensesScreen(navHostController = rememberNavController())
    }
}