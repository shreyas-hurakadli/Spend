package com.example.spend.ui.screen.account

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.example.spend.data.room.account.Account
import com.example.spend.ui.accountIcons
import com.example.spend.ui.navigation.RouteNumbers
import com.example.spend.ui.navigation.Routes
import com.example.spend.ui.screen.AppNavigationDrawer
import com.example.spend.ui.screen.AppTopBar
import com.example.spend.ui.viewmodel.account.AccountViewModel
import kotlinx.coroutines.launch

@Composable
fun AccountScreen(
    navHostController: NavHostController,
    viewModel: AccountViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val drawerScope = rememberCoroutineScope()

    val accounts by viewModel.accounts.collectAsState()
    val thereAreAccounts by viewModel.thereAreAccounts.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()

    AppNavigationDrawer(
        currentScreenIndex = RouteNumbers.ACCOUNT_PAGE.screenNumber,
        navHostController = navHostController,
        drawerState = drawerState,
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = stringResource(id = R.string.accounts),
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
                if (thereAreAccounts) {
                    FloatingActionButton(
                        onClick = { navHostController.navigate(Routes.AddAccountScreen) },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = stringResource(id = R.string.add_account)
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(paddingValues = innerPadding)) {
                if (thereAreAccounts) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .fillMaxSize()
                    ) {
                        AccountList(
                            accounts = accounts.filter { it.name != "All" },
                            currencySymbol = currencySymbol,
                            onClick = {
                                viewModel.selectAccount(it)
                                navHostController.navigate(Routes.AccountDetailScreen)
                            }
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .fillMaxSize()
                    ) {
                        Spacer(modifier = Modifier.weight(weight = 0.55f))
                        NoAccounts()
                        Spacer(modifier = Modifier.weight(weight = 1f))
                        OutlinedButton(
                            onClick = { navHostController.navigate(Routes.AddAccountScreen) },
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
private fun AccountList(
    accounts: List<Account>,
    currencySymbol: String,
    modifier: Modifier = Modifier,
    onClick: (Account) -> Unit = {}
) {
    LazyColumn(modifier = modifier) {
        items(items = accounts) { account ->
            AccountView(
                account = account,
                currencySymbol = currencySymbol,
                onClick = onClick
            )
        }
    }
}

@Composable
private fun AccountView(
    account: Account,
    currencySymbol: String,
    modifier: Modifier = Modifier,
    onClick: (Account) -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(enabled = true, onClick = { onClick(account) })
            .padding(bottom = 8.dp)
            .fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(
                    color = account.color,
                    shape = RoundedCornerShape(16.dp)
                )
                .size(size = 55.dp)
        ) {
            account.icon?.let {
                Icon(
                    imageVector = ImageVector.vectorResource(accountIcons[it]!!),
                    contentDescription = null,
                    modifier = modifier.size(size = 30.dp)
                )
            }
        }
        Spacer(modifier.width(16.dp))
        Text(text = account.name, style = MaterialTheme.typography.labelLarge)
        Spacer(modifier.weight(weight = 1f))
        Text(
            text = "$currencySymbol ${account.balance}",
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun NoAccounts(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.padding(all = 32.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.baseline_wallet),
            tint = Color.Gray,
            contentDescription = null,
            modifier = Modifier
                .size(size = 150.dp)
        )
    }
    Spacer(Modifier.height(height = 16.dp))
    Text(
        text = stringResource(R.string.no_account_message),
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = stringResource(R.string.no_account_extended_message),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Light
    )
}