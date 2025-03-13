package com.example.spend.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.spend.LongToDate
import com.example.spend.R
import com.example.spend.ui.viewmodel.AppViewModelFactory
import com.example.spend.ui.viewmodel.EntryViewModel

@Composable
fun EntryScreen(
    navHostController: NavHostController,
    viewModel: EntryViewModel = viewModel(
        factory = AppViewModelFactory.Factory
    )
) {
    val list = viewModel.transactions.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.transactions),
                canNavigateBack = true,
                onBackClick = { navHostController.popBackStack() }
            )
        }
    ) { innerPadding ->
        var date: String = ""

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(items = list.value) { entry ->
                Column {
                    if (date != LongToDate(entry.date)) {
                        date = LongToDate(entry.date)
                        Text(
                            text = LongToDate(entry.date),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Start,
                        )
                        Spacer(Modifier.padding(top = 8.dp))
                    }
                    TransactionCard(
                        entry = entry,
                        showDate = false
                    )
                }
            }
        }
    }
}