package com.example.spend.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.spend.LongToDate
import com.example.spend.R
import com.example.spend.data.room.Entry
import com.example.spend.getTodayStart
import com.example.spend.ui.navigation.Routes
import com.example.spend.ui.theme.SpendTheme
import com.example.spend.ui.viewmodel.AppViewModelFactory
import com.example.spend.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    viewModel: HomeViewModel = viewModel(
        factory = AppViewModelFactory.Factory
    )
) {
    var openDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(title = stringResource(id = R.string.home))
        },
        bottomBar = {
            AppBottomBar(
                currentScreenIndex = 0,
                onClick = { navHostController.navigate(Routes.ExpensesScreen) }
            )
        },
        modifier = Modifier.safeContentPadding()
    ) { innerPadding ->
        val balance = viewModel.balance.collectAsState()
        val transactions = viewModel.transactions.collectAsState()

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xFF8C6EFF),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .fillMaxWidth()
                        .padding(24.dp),
                ) {
                    IconButton(
                        onClick = { openDialog = true },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = stringResource(R.string.update_balance),
                        )
                    }
                    Column {
                        Text(
                            text = stringResource(R.string.total_balance),
                            style = MaterialTheme.typography.labelLarge,
                        )
                        Spacer(Modifier.padding(8.dp))
                        Text(
                            text = "₹ ${balance.value}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 24.sp
                        )
                    }
                }
                Spacer(Modifier.padding(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.transaction_history),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = stringResource(R.string.see_all),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8C6EFF),
                        modifier = Modifier
                            .clickable { navHostController.navigate(Routes.EntryScreen) }
                    )
                }

                if (openDialog) {
                    DialogBox(
                        onDismissRequest = {
                            viewModel.updateBalance(it)
                            openDialog = false
                        },
                    )
                }

                Spacer(Modifier.padding(8.dp))
                Box(
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    if (transactions.value.isEmpty()) {
                        Text(
                            text = "No transactions yet!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn {
                            items(transactions.value) { entry ->
                                TransactionCard(
                                    entry = entry,
                                    showDate = true
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
fun DialogBox(
    onDismissRequest: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var balance by rememberSaveable { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismissRequest(balance) }) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.enter_new_balance),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                OutlinedTextField(
                    value = balance,
                    onValueChange = {
                        if (it == "")
                            balance = ""
                        else if (it.all { c -> c.isDigit() }) {
                            balance = it
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    textStyle = TextStyle(
                        fontSize = 16.sp
                    )
                )
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    FloatingActionButton(
                        onClick = { onDismissRequest(balance) },
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = stringResource(R.string.done),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreviewLight() {
    SpendTheme {
        HomeScreen(navHostController = rememberNavController())
    }
}