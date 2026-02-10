package com.example.spend.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navHostController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val drawerScope = rememberCoroutineScope()

    AppNavigationDrawer(
        currentScreenIndex = RouteNumbers.HOME_PAGE.screenNumber,
        navHostController = navHostController,
        drawerState = drawerState,
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = stringResource(R.string.home),
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
            val transactions by viewModel.transactions.collectAsState()
            val firstAccount by viewModel.currentAccount.collectAsState()
            val accountList by viewModel.accountList.collectAsState()

            var showAccountsBottomSheet by remember { mutableStateOf(false) }
            var account by remember { mutableStateOf(firstAccount) }

            if (account.id == 0L) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                    if (firstAccount.id != 0L)
                        account = firstAccount
                }
            } else {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    val maxWidth = maxWidth
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxSize()
                    ) {
                        BalanceBar(
                            account = account,
                            onDropDownClick = { showAccountsBottomSheet = true },
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
                                maxWidth = maxWidth,
                                onClick = { navHostController.navigate(Routes.AddScreen) }
                            )
                            ActionCard(
                                icon = ImageVector.vectorResource(R.drawable.baseline_wallet),
                                label = stringResource(R.string.add_account),
                                maxWidth = maxWidth,
                                onClick = { navHostController.navigate(Routes.AddAccountScreen) }
                            )
                            ActionCard(
                                icon = ImageVector.vectorResource(R.drawable.baseline_category),
                                label = stringResource(R.string.add_category),
                                maxWidth = maxWidth,
                                onClick = { navHostController.navigate(Routes.CreateCategoryScreen) }
                            )
                            ActionCard(
                                icon = ImageVector.vectorResource(R.drawable.coin),
                                label = stringResource(R.string.add_budget),
                                maxWidth = maxWidth,
                                onClick = { navHostController.navigate(Routes.AddBudgetScreen) }
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
                                    color = MaterialTheme.colorScheme.background,
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
                                    transactions.forEach { entryCategory ->
                                        TransactionCard(
                                            entryCategory = entryCategory,
                                            iconTint = Color.Black,
                                            showDate = true,
                                        )
                                    }
                                } else {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(all = 32.dp)
                                    ) {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(R.drawable.no_transactions),
                                            tint = Color.Gray,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(size = 150.dp)
                                        )
                                    }
                                    Spacer(Modifier.height(height = 16.dp))
                                    Text(
                                        text = stringResource(R.string.no_transactions),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = stringResource(R.string.no_transactions_home_extended_message),
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Light
                                    )
                                    Spacer(Modifier.height(height = 16.dp))
                                }
                            }
                        }
                        if (showAccountsBottomSheet) {
                            AccountBottomSheet(
                                accounts = accountList,
                                onSelect = { account = it },
                                onDismiss = { showAccountsBottomSheet = false },
                            )
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
    maxWidth: Dp,
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
                    shape = RoundedCornerShape(24.dp)
                )
                .dropShadow(
                    shape = RoundedCornerShape(24.dp),
                    shadow = Shadow(
                        radius = 4.dp,
                        spread = 2.dp,
                        color = Color(0x40000000),
                        offset = DpOffset(x = 4.dp, 4.dp)
                    )
                )
                .padding(vertical = 24.dp)
                .width((maxWidth - 16.dp) / 3)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
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
    account: Account,
    onDropDownClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(24.dp)
            )
            .dropShadow(
                shape = RoundedCornerShape(24.dp),
                shadow = Shadow(
                    radius = 10.dp,
                    spread = 6.dp,
                    color = Color(0x40000000),
                    offset = DpOffset(x = 4.dp, 4.dp)
                )
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
                            onClick = onDropDownClick
                        )
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

@Preview(showBackground = true)
@Composable
private fun MainScreenPreviewLight() {
    SpendTheme {
        HomeScreen(navHostController = rememberNavController())
    }
}