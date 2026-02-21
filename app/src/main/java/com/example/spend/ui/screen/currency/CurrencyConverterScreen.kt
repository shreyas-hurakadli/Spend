package com.example.spend.ui.screen.currency

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.spend.R
import com.example.spend.ui.navigation.RouteNumbers
import com.example.spend.ui.screen.AppNavigationDrawer
import com.example.spend.ui.screen.AppTopBar
import com.example.spend.ui.viewmodel.currency.CurrencyConverterViewModel
import kotlinx.coroutines.launch

@Composable
fun CurrencyConverterScreen(
    navHostController: NavHostController,
    viewModel: CurrencyConverterViewModel = hiltViewModel()
) {
    val currencies by viewModel.currencies.collectAsState()

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    AppNavigationDrawer(
        currentScreenIndex = RouteNumbers.CURRENCY_SCREEN.screenNumber,
        navHostController = navHostController,
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = stringResource(R.string.currencies),
                    hasNavigationDrawer = true,
                    onNavigationDrawerClick = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            if (currencies.isEmpty()) {
                NoCurrencyScreen(
                    modifier = Modifier
                        .padding(paddingValues = innerPadding)
                        .fillMaxSize()
                ) { }
            } else {

            }
        }
    }
}

@Composable
private fun NoCurrencyScreen(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

}