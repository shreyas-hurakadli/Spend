package com.example.spend.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.spend.R
import com.example.spend.ui.CurrencyIcon
import com.example.spend.ui.currencyIcons
import com.example.spend.ui.navigation.Routes
import com.example.spend.ui.viewmodel.CurrencyScreenViewModel

@Composable
fun CurrencyScreen(
    navHostController: NavHostController,
    viewModel: CurrencyScreenViewModel = hiltViewModel()
) {
    val selectedCode by viewModel.selectedCode.collectAsState()
    val confirmed by viewModel.confirmed.collectAsState()

    if (confirmed) {
        navHostController.navigate(Routes.HomeScreen)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.select_currency),
                canNavigateBack = true,
                onBackClick = { navHostController.popBackStack() }
            )
        },
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .fillMaxSize()
                .padding(all = 8.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(weight = 1f)
            ) {
                items(items = currencyIcons) { currency ->
                    CurrencyView(
                        currency = currency,
                        selected = selectedCode?.substring(startIndex = 0, endIndex = 3),
                        onSelect = { viewModel.selectCurrency(codeSymbol = it) }
                    )
                }
            }
            OutlinedButton(
                onClick = { viewModel.confirmSelection() },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp)
            ) {
                Text(text = stringResource(id = R.string.confirm_selection))
            }
        }
    }
}

@Composable
private fun CurrencyView(
    currency: CurrencyIcon,
    selected: String?,
    onSelect: (String) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .background(
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(size = 16.dp)
            )
            .clickable(
                enabled = true,
                onClick = { onSelect("${currency.code} ${currency.symbol}") })
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(size = 55.dp)
                    .background(color = Color.White, shape = CircleShape)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(width = 32.dp, height = 24.dp)
                        .border(color = Color.LightGray, width = 1.dp, shape = RectangleShape)
                ) {
                    Image(
                        imageVector = ImageVector.vectorResource(id = currency.flag),
                        contentDescription = currency.name,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Spacer(modifier = Modifier.width(width = 8.dp))
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    text = "${currency.code} ${currency.symbol}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currency.name,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Light
                )
            }
            Spacer(modifier = Modifier.weight(weight = 1f))
            Checkbox(
                checked = (selected == currency.code),
                onCheckedChange = {},
                enabled = false
            )
        }
    }
}