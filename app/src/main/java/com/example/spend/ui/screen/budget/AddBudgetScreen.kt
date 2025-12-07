package com.example.spend.ui.screen.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.spend.R
import com.example.spend.getLocalCurrencySymbol
import com.example.spend.getTodayStart
import com.example.spend.longToDate
import com.example.spend.ui.accountIcons
import com.example.spend.ui.icons
import com.example.spend.ui.screen.AccountBottomSheet
import com.example.spend.ui.screen.AppTopBar
import com.example.spend.ui.screen.CategoryBottomSheet
import com.example.spend.ui.screen.DatePicker
import com.example.spend.ui.viewmodel.budget.AddBudgetViewModel
import com.example.spend.ui.viewmodel.budget.Period
import com.example.spend.validateCurrency
import kotlinx.coroutines.launch
import kotlin.collections.get

@Composable
fun AddBudgetScreen(
    navHostController: NavHostController,
    viewModel: AddBudgetViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val fromDateInteractionSource = remember { MutableInteractionSource() }
    val toDateInteractionSource = remember { MutableInteractionSource() }
    val snackBarScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.uiState.collectAsState()
    val period by viewModel.period.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedAccount by viewModel.selectedAccount.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedPeriod by viewModel.period.collectAsState()
    val fromDate by viewModel.fromDate.collectAsState()
    val toDate by viewModel.toDate.collectAsState()
    val showSnackBar by viewModel.showSnackBar.collectAsState()
    val snackBarMessage by viewModel.snackBarMessage.collectAsState()

    var amount by rememberSaveable { mutableStateOf("") }
    var showCategories by rememberSaveable { mutableStateOf(false) }
    var showAccounts by rememberSaveable { mutableStateOf(false) }
    var showPeriods by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val isFromDatePressed by fromDateInteractionSource.collectIsPressedAsState()
    val isToDatePressed by toDateInteractionSource.collectIsPressedAsState()
    var isFromDate by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect (showSnackBar) {
        if (showSnackBar && snackBarMessage.isNotEmpty()) {
            snackBarScope.launch {
                snackBarHostState.showSnackbar(message = snackBarMessage)
                viewModel.toggleShowSnackBar()
            }
        }
    }

    LaunchedEffect(isFromDatePressed) {
        if (isFromDatePressed) {
            isFromDate = true
            showDatePicker = true
        }
    }

    LaunchedEffect(isToDatePressed) {
        if (isToDatePressed) {
            isFromDate = false
            showDatePicker = true
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.add_budget),
                canNavigateBack = true,
                onBackClick = { navHostController.popBackStack() },
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(innerPadding)
                .clickable(
                    enabled = true,
                    indication = null,
                    interactionSource = interactionSource,
                    onClick = { focusManager.clearFocus() }
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { viewModel.setName(it) },
                    label = {
                        Text(
                            text = "Enter budget name",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = if (period == Period.NONE) "" else period.toString(),
                    onValueChange = {},
                    label = {
                        Text(
                            text = "Select period",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            showPeriods = true
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                tint = MaterialTheme.colorScheme.onBackground,
                                contentDescription = null
                            )
                        }
                    }
                )
                if (selectedPeriod == Period.ONE_TIME) {
                    Spacer(Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = if (fromDate == null) ""
                            else longToDate(fromDate ?: getTodayStart()),
                            onValueChange = {},
                            label = {
                                Text(
                                    text = "From Date",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            },
                            singleLine = true,
                            readOnly = true,
                            shape = RoundedCornerShape(24.dp),
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 16.sp
                            ),
                            interactionSource = fromDateInteractionSource,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.weight(0.1f))
                        OutlinedTextField(
                            value = if (toDate == null) ""
                            else longToDate(toDate ?: getTodayStart()),
                            onValueChange = {},
                            interactionSource = toDateInteractionSource,
                            label = {
                                Text(
                                    text = "To Date",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            },
                            singleLine = true,
                            readOnly = true,
                            shape = RoundedCornerShape(24.dp),
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 16.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = "INR",
                        onValueChange = {},
                        trailingIcon = {
                            Text(
                                text = getLocalCurrencySymbol() ?: "$",
                                color = Color.Black
                            )
                        },
                        readOnly = true,
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.weight(0.3f)
                    )
                    Spacer(Modifier.weight(0.1f))
                    OutlinedTextField(
                        value = amount,
                        onValueChange = {
                            if (it == "" || it.last() == '.' || validateCurrency(it)) {
                                amount = it
                                viewModel.setAmount(it)
                            }
                        },
                        label = {
                            Text(
                                text = "Amount",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.weight(0.6f)
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Select Account",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(55.dp)
                            .background(color = selectedAccount?.color ?: Color(0xFF77DD77)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedAccount != null && selectedAccount?.icon != null) {
                            Icon(
                                imageVector = ImageVector.vectorResource(accountIcons[selectedAccount!!.icon]!!),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(4.dp))
                    OutlinedTextField(
                        value = selectedAccount?.name ?: "",
                        onValueChange = {},
                        trailingIcon = {
                            IconButton(onClick = { showAccounts = true }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    contentDescription = null
                                )
                            }
                        },
                        readOnly = true,
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Select Category",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(55.dp)
                            .background(
                                color = selectedCategory?.color
                                    ?: MaterialTheme.colorScheme.background
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedCategory != null && selectedCategory?.icon != null && icons[selectedCategory!!.icon] != null) {
                            Icon(
                                imageVector = ImageVector.vectorResource(icons[selectedCategory!!.icon]!!),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(4.dp))
                    OutlinedTextField(
                        value = selectedCategory?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            IconButton(
                                onClick = { showCategories = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    contentDescription = null
                                )
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.weight(1f))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.clear()
                            amount = ""
                            showAccounts = false
                            showCategories = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {
                            viewModel.save()
                            amount = ""
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Add")
                    }
                }
                if (showAccounts) {
                    AccountBottomSheet(
                        accounts = accounts,
                        onSelect = {
                            viewModel.setAccount(it)
                            viewModel.setSelectedAccount(it)
                        },
                        onDismiss = { showAccounts = false }
                    )
                }
                if (showCategories) {
                    CategoryBottomSheet(
                        categories = categories,
                        onSelect = {
                            viewModel.setCategory(it)
                            viewModel.setSelectedCategory(it)
                        },
                        onDismiss = { showCategories = false }
                    )
                }
                if (showPeriods) {
                    PeriodBottomSheet(
                        onDismiss = { showPeriods = false },
                        onSelect = {
                            when (it) {
                                "Week" -> viewModel.setPeriod(Period.WEEK, Period.WEEK.time)
                                "Month" -> viewModel.setPeriod(Period.MONTH, Period.MONTH.time)
                                "Year" -> viewModel.setPeriod(Period.YEAR, Period.YEAR.time)
                                "One Time" -> viewModel.setPeriod(
                                    Period.ONE_TIME,
                                    Period.ONE_TIME.time
                                )
                            }
                        }
                    )
                }
                if (showDatePicker) {
                    DatePicker(
                        onDateSelected = {
                            if (isFromDate) viewModel.setFromDate(it ?: getTodayStart())
                            else viewModel.setToDate(it ?: getTodayStart())
                        },
                        onDismiss = { showDatePicker = false }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodBottomSheet(
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val periods = Period.entries.map { it.toString() }.filter { it != "None" }.distinct()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) {
        LazyColumn {
            items(items = periods) { period ->
                TextButton(
                    onClick = {
                        onSelect(period)
                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onDismiss()
                            }
                        }
                    }
                ) {
                    Text(text = period, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}