package com.example.spend.ui.screen

import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.spend.R
import com.example.spend.longToDate
import com.example.spend.ui.viewmodel.EntryViewModel

@Composable
fun EntryScreen(
    navHostController: NavHostController,
    viewModel: EntryViewModel = hiltViewModel()
) {
    val list by viewModel.transactions.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.transactions),
                canNavigateBack = true,
                onBackClick = { navHostController.popBackStack() }
            )
        }
    ) { innerPadding ->
        var date = ""
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(items = list) { entry ->
                Column {
                    if (date != longToDate(entry.epochSeconds)) {
                        date = longToDate(entry.epochSeconds)
                        Text(
                            text = longToDate(entry.epochSeconds),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Start,
                        )
                        Spacer(Modifier.padding(top = 8.dp))
                    }
                    TransactionCard(
                        entry = entry,
                        icon = ImageVector.vectorResource(R.drawable.baseline_label),
                        iconTint = MaterialTheme.colorScheme.onSecondary,
                        backgroundColor = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
        }
    }
}