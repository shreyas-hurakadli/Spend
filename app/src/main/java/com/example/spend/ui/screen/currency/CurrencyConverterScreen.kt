package com.example.spend.ui.screen.currency

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.spend.R
import com.example.spend.getFormattedAmount
import com.example.spend.ui.CurrencyIcon
import com.example.spend.ui.currencyIcons
import com.example.spend.ui.navigation.RouteNumbers
import com.example.spend.ui.screen.AppNavigationDrawer
import com.example.spend.ui.screen.AppTopBar
import com.example.spend.ui.viewmodel.currency.CurrencyConverterViewModel
import kotlinx.coroutines.launch

private val buttons = listOf(
    listOf("1", "2", "3"),
    listOf("4", "5", "6"),
    listOf("7", "8", "9"),
    listOf(".", "0"),
)

@Composable
fun CurrencyConverterScreen(
    navHostController: NavHostController,
    viewModel: CurrencyConverterViewModel = hiltViewModel()
) {
    val currencies by viewModel.currencies.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val baseCurrency by viewModel.baseCurrency.collectAsState()
    val quoteCurrency by viewModel.quoteCurrency.collectAsState()
    val baseCurrencyValue by viewModel.baseCurrencyValue.collectAsState()
    val quoteCurrencyValue by viewModel.quoteCurrencyValue.collectAsState()
    val conversionValue by viewModel.conversionValue.collectAsState()
    val showBaseCurrencyBottomSheet by viewModel.showBaseCurrencyBottomSheet.collectAsState()
    val showQuoteCurrencyBottomSheet by viewModel.showQuoteCurrencyBottomSheet.collectAsState()

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    AppNavigationDrawer(
        currentScreenIndex = RouteNumbers.CURRENCY_SCREEN.screenNumber,
        navHostController = navHostController,
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = stringResource(id = R.string.currency_converter),
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
            if (isLoading) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(paddingValues = innerPadding)
                        .fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            } else {
                if (currencies.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(paddingValues = innerPadding)
                            .fillMaxSize()
                    ) {
                        Spacer(modifier = Modifier.fillMaxHeight(fraction = 0.2f))
                        NoCurrencyScreen()
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier
                            .padding(paddingValues = innerPadding)
                            .fillMaxSize()
                            .padding(all = 8.dp)
                    ) {
                        BoxWithConstraints(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(weight = 0.45f)
                        ) {
                            val maxHeight = maxHeight
                            Column {
                                ConverterCard(
                                    title = stringResource(id = R.string.send),
                                    amount = baseCurrencyValue,
                                    currency = baseCurrency,
                                    onCurrencyIconClick = { viewModel.toggleShowBaseCurrencyBottomSheet() },
                                    maxHeight = (maxHeight / 2) - 8.dp
                                )
                                Spacer(modifier = Modifier.height(height = 8.dp))
                                ConverterCard(
                                    title = stringResource(id = R.string.receive),
                                    amount = if (quoteCurrencyValue - quoteCurrencyValue.toInt() > 0.00) getFormattedAmount(
                                        value = quoteCurrencyValue
                                    ) else quoteCurrencyValue.toInt().toString(),
                                    currency = quoteCurrency,
                                    onCurrencyIconClick = { viewModel.toggleShowQuoteCurrencyBottomSheet() },
                                    maxHeight = (maxHeight / 2) - 8.dp
                                )
                            }
                            ExchangeButton(
                                onClick = { viewModel.swapCurrencies() },
                                modifier = Modifier.size(
                                    size = if (screenWidth <= 360.dp) 24.dp else 32.dp
                                )
                            )
                        }
                        Column(
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.1f)
                        ) {
                            Text(
                                text = stringResource(id = R.string.exchange_rate),
                                fontSize = if (screenWidth <= 360.dp) 12.sp else 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "1 ${baseCurrency.code} = ${conversionValue.toBigDecimal()} ${quoteCurrency.code}",
                                fontSize = if (screenWidth <= 360.dp) 16.sp else 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(weight = 0.45f)
                                .padding(horizontal = 16.dp)
                        ) {
                            buttons.forEach { buttonRow ->
                                CalculatorButtonRow(
                                    buttons = buttonRow,
                                    onButtonClick = { viewModel.updateInputValue(value = it) },
                                    screenWidth = screenWidth,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        if (showBaseCurrencyBottomSheet || showQuoteCurrencyBottomSheet) {
                            CurrencyBottomSheet(
                                onSelect = {
                                    if (showBaseCurrencyBottomSheet) {
                                        viewModel.updateBaseCurrency(currencyIcon = it)
                                    } else {
                                        viewModel.updateQuoteCurrency(currencyIcon = it)
                                    }
                                },
                                onDismiss = {
                                    if (showBaseCurrencyBottomSheet) {
                                        viewModel.toggleShowBaseCurrencyBottomSheet()
                                    } else {
                                        viewModel.toggleShowQuoteCurrencyBottomSheet()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConverterCard(
    title: String,
    amount: String,
    currency: CurrencyIcon,
    onCurrencyIconClick: () -> Unit,
    maxHeight: Dp
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(height = maxHeight)
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(size = 16.dp)
            )
            .padding(all = 8.dp)
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onCurrencyIconClick,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.background,
                                    shape = CircleShape
                                )
                                .size(size = 24.dp)
                        ) {
                            Image(
                                imageVector = ImageVector.vectorResource(id = currency.flag),
                                contentDescription = currency.name
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray
                )
            }
            BasicText(
                text = amount,
                style = MaterialTheme.typography.displayMedium,
                maxLines = 1,
                autoSize = TextAutoSize.StepBased(
                    minFontSize = 24.sp,
                    maxFontSize = 48.sp
                ),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
private fun NoCurrencyScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(all = 32.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.no_data),
            tint = Color.Gray,
            contentDescription = null,
            modifier = Modifier.size(size = 150.dp)
        )
    }
    Spacer(Modifier.height(height = 16.dp))
    Text(
        text = stringResource(id = R.string.no_exchange_data_main_message),
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = stringResource(id = R.string.no_exchange_data_extended_message),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Light
    )
}

@Composable
private fun ExchangeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.exchange),
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = null,
            modifier = modifier
        )
    }
}

@Composable
fun CalculatorButtonRow(
    buttons: List<String>,
    onButtonClick: (String) -> Unit,
    screenWidth: Dp,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        buttons.forEach { button ->
            OutlinedButton(
                onClick = { onButtonClick(button) },
                shape = RoundedCornerShape(size = 16.dp),
                border = null,
                colors = ButtonColors(
                    containerColor = Color(0xFFEAEDF2),
                    contentColor = Color.Black,
                    disabledContainerColor = Color(0xFFEAEDF2),
                    disabledContentColor = Color.Black,
                ),
                contentPadding = PaddingValues(all = 0.dp),
                modifier = Modifier.weight(weight = 1f)
            ) {
                Text(
                    text = button,
                    fontSize = if (screenWidth <= 360.dp) 16.sp else 24.sp,
                    modifier = Modifier.padding(all = 16.dp)
                )
            }
        }
        if (buttons.size == 2) {
            OutlinedButton(
                onClick = { onButtonClick("B") },
                shape = RoundedCornerShape(size = 16.dp),
                border = null,
                colors = ButtonColors(
                    containerColor = Color(0xFFEAEDF2),
                    contentColor = Color.Black,
                    disabledContainerColor = Color(0xFFEAEDF2),
                    disabledContentColor = Color.Black,
                ),
                contentPadding = PaddingValues(all = 0.dp),
                modifier = Modifier.weight(weight = 1f)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.outline_backspace),
                    tint = Color.Black,
                    contentDescription = null,
                    modifier = Modifier.padding(all = 16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyBottomSheet(
    onSelect: (CurrencyIcon) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) {
        LazyColumn {
            items(
                items = currencyIcons,
                key = { it.code }
            ) { currencyIcon ->
                CurrencyView(currency = currencyIcon) {
                    onSelect(currencyIcon)
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            onDismiss()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrencyView(
    currency: CurrencyIcon,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(text = currency.name, style = MaterialTheme.typography.titleMedium)
        },
        supportingContent = { Text(text = currency.code) },
        colors = ListItemColors(
            containerColor = Color.Transparent,
            headlineColor = MaterialTheme.colorScheme.onBackground,
            leadingIconColor = Color.Transparent,
            overlineColor = Color.Transparent,
            supportingTextColor = MaterialTheme.colorScheme.onBackground,
            trailingIconColor = Color.Transparent,
            disabledHeadlineColor = Color.Transparent,
            disabledLeadingIconColor = Color.Transparent,
            disabledTrailingIconColor = Color.Transparent
        ),
        leadingContent = {
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
        },
        modifier = Modifier
            .clickable(enabled = true, onClick = onClick)
            .fillMaxWidth(),
    )
}