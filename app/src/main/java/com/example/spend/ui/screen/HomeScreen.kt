package com.example.spend.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.spend.R
import com.example.spend.data.room.account.Account
import com.example.spend.getFormattedAmount
import com.example.spend.getLocalCurrencySymbol
import com.example.spend.ui.navigation.RouteNumbers
import com.example.spend.ui.navigation.Routes
import com.example.spend.ui.theme.SpendTheme
import com.example.spend.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            AppTopBar(title = stringResource(id = R.string.home))
        },
        bottomBar = {
            AppBottomBar(
                currentScreenIndex = RouteNumbers.HOME_PAGE.screenNumber,
                navHostController
            )
        },
    ) { innerPadding ->
        val transactions by viewModel.transactions.collectAsState()
        val firstAccount by viewModel.currentAccount.collectAsState()
        var account by remember { mutableStateOf(firstAccount) }

        if (account?.id == 0L) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
                if (firstAccount?.id != 0L)
                    account = firstAccount
            }
        } else {
            Box(
                modifier =
                    if (transactions.isNotEmpty())
                        Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    else Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize()
                ) {
                    BalanceBar(
                        onClick = { account= it },
                        account = account!!,
                        accountList = viewModel.accountList,
                    )
                    Spacer(Modifier.padding(16.dp))
                    Text(
                        text = stringResource(R.string.quick_actions),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(Modifier.padding(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        ActionCard(
                            icon = Icons.Filled.Add,
                            label = stringResource(R.string.add_entry),
                            onClick = { navHostController.navigate(Routes.AddScreen) }
                        )
                        ActionCard(
                            icon = ImageVector.vectorResource(R.drawable.baseline_wallet),
                            label = stringResource(R.string.add_account),
                            onClick = { navHostController.navigate(Routes.AddAccountScreen) }
                        )
                        ActionCard(
                            icon = Icons.Filled.Add,
                            label = stringResource(R.string.add_entry),
                            onClick = {}
                        )
                    }
                    Spacer(Modifier.padding(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.recent_transaction),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        if (transactions.isNotEmpty()) {
                            Text(
                                text = stringResource(R.string.see_all),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier
                                    .clickable { navHostController.navigate(Routes.EntryScreen) }
                            )
                        }
                    }

                    Spacer(Modifier.padding(8.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (transactions.isNotEmpty()) {
                                transactions.forEach { entry ->
                                    TransactionCard(
                                        entry = entry,
                                        icon = ImageVector.vectorResource(R.drawable.baseline_pencil),
                                        iconTint = MaterialTheme.colorScheme.onSecondary,
                                        showDate = true,
                                        backgroundColor = MaterialTheme.colorScheme.secondary,
                                    )
                                }
                            } else {
                                Text(text = stringResource(R.string.no_transactions))
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
private fun ActionCard(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable(enabled = true, onClick = onClick)
            .padding(end = 8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = label,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun BalanceBar(
    onClick: (Account) -> Unit,
    account: Account,
    accountList: StateFlow<List<Account>>,
    modifier: Modifier = Modifier,
) {
    Log.d("BalanceBar", account.toString())
    val list by accountList.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                )
                .fillMaxWidth()
                .padding(24.dp),
        ) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.total_balance),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Column {
                        Row(
                            modifier = Modifier.clickable(
                                enabled = true,
                                onClick = { expanded = true })
                        ) {
                            Text(
                                text = account.name,
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.labelLarge,
                            )
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = stringResource(R.string.see_all),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                list.forEach { account ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                account.name,
                                                textAlign = TextAlign.Center,
                                                color = MaterialTheme.colorScheme.inverseOnSurface
                                            )
                                        },
                                        onClick = { onClick(account) },
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.padding(8.dp))
                Text(
                    text = (if (account.balance < 0) "- " else "") + "${getLocalCurrencySymbol()} ${
                        getFormattedAmount(
                            value = account.balance
                        )
                    }",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 24.sp
                )
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