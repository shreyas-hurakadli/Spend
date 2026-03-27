package com.example.spend.ui.screen.budget

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.category.Category
import com.example.spend.epochSecondsToDate
import com.example.spend.isCurrencyAppropriate
import com.example.spend.toTwoDecimal
import com.example.spend.ui.MAX_BUDGET_NAME_LENGTH
import com.example.spend.ui.MAX_ENTRY_AMOUNT
import com.example.spend.ui.accountIcons
import com.example.spend.ui.icons
import com.example.spend.ui.screen.AccountBottomSheet
import com.example.spend.ui.screen.AppTopBar
import com.example.spend.ui.screen.CategoryBottomSheet
import com.example.spend.ui.screen.DatePicker
import com.example.spend.ui.screen.showToast
import com.example.spend.ui.viewmodel.budget.BudgetViewModel
import com.example.spend.validateCurrency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun EditBudgetScreen(
    navHostController: NavHostController,
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val showToast by viewModel.showToast.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val selectedBudget by viewModel.selectedBudget.collectAsState()
    val currencyCode by viewModel.currencyCode.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var editedBudget by remember { mutableStateOf(value = selectedBudget?.first) }
    var showDatePicker by remember { mutableStateOf(value = false) }
    var showAccounts by remember { mutableStateOf(value = false) }
    var showCategories by remember { mutableStateOf(value = false) }
    var selectedAccount: Account? by remember { mutableStateOf(value = null) }
    var selectedCategory: Category? by remember { mutableStateOf(value = null) }
    var amountInput by remember { mutableStateOf(value = editedBudget?.amount?.toString() ?: "") }

    val context = LocalContext.current

    LaunchedEffect(key1 = showToast) {
        if (showToast && toastMessage.isNotBlank()) {
            showToast(message = toastMessage, context = context)
            viewModel.onToastShow()
        }
    }

    LaunchedEffect(key1 = accounts, key2 = categories) {
        withContext(context = Dispatchers.Default) {
            if (selectedAccount == null) {
                selectedAccount = accounts.find {
                    it.id == selectedBudget?.first?.accountId
                }
            }
            if (selectedCategory == null) {
                selectedCategory = categories.find {
                    it.id == selectedBudget?.first?.categoryId
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(id = R.string.edit_budget),
                canNavigateBack = true,
                onBackClick = { navHostController.popBackStack() },
            )
        },
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .padding(all = 8.dp)
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
        ) {
            if (selectedBudget == null) {
                CircularProgressIndicator()
            }
            editedBudget?.let {
                OutlinedTextField(
                    value = it.name,
                    onValueChange = { input -> editedBudget = it.copy(name = input) },
                    isError = it.name.length > MAX_BUDGET_NAME_LENGTH,
                    label = {
                        Text(
                            text = "Enter budget name",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(size = 24.dp),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = epochSecondsToDate(epochSeconds = it.startTimeStamp + it.period),
                    onValueChange = {},
                    label = {
                        Text(
                            text = stringResource(id = R.string.budget_end_date),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(size = 24.dp),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                tint = MaterialTheme.colorScheme.onBackground,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = currencyCode,
                        onValueChange = {},
                        trailingIcon = {
                            Text(
                                text = currencySymbol,
                                color = Color.Black
                            )
                        },
                        readOnly = true,
                        singleLine = true,
                        shape = RoundedCornerShape(size = 24.dp),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.weight(0.3f)
                    )
                    Spacer(Modifier.weight(0.1f))
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { input ->
                            if (input.isCurrencyAppropriate()) {
                                amountInput = input
                            }
                        },
                        isError = it.amount >= MAX_ENTRY_AMOUNT,
                        label = {
                            Text(
                                text = stringResource(id = R.string.amount),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(size = 24.dp),
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
                            .size(55.dp)
                            .background(
                                color = selectedAccount?.color ?: Color(0xFF77DD77),
                                shape = RoundedCornerShape(size = 16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        selectedAccount?.let { selectedAccount ->
                            selectedAccount.icon?.let { icon ->
                                accountIcons[icon]?.let { resourceId ->
                                    Icon(
                                        imageVector = ImageVector.vectorResource(id = resourceId),
                                        contentDescription = null,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }
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
                        shape = RoundedCornerShape(size = 24.dp),
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
                            .size(55.dp)
                            .background(
                                color = selectedCategory?.color
                                    ?: MaterialTheme.colorScheme.background,
                                shape = RoundedCornerShape(size = 16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        selectedCategory?.let { selectedCategory ->
                            selectedCategory.icon?.let { icon ->
                                icons[icon]?.let { resourceId ->
                                    Icon(
                                        imageVector = ImageVector.vectorResource(id = resourceId),
                                        contentDescription = null,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }

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
                        shape = RoundedCornerShape(size = 24.dp),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                    )
                }
                Spacer(modifier = Modifier.weight(weight = 1f))
                OutlinedButton(
                    onClick = {
                        if (validateCurrency(input = amountInput)) {
                            editedBudget = it.copy(amount = amountInput.toDouble().toTwoDecimal())
                            viewModel.editBudget(editedBudget = editedBudget ?: it)
                        } else {
                            viewModel.showToast(message = "Invalid Amount Input")
                        }
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.save_changes),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                if (showDatePicker) {
                    DatePicker(
                        onDateSelected = { milliSeconds ->
                            milliSeconds?.let { ms ->
                                editedBudget = it.copy(period = (ms / 1000L) - it.startTimeStamp)
                            }
                        },
                        onDismiss = { showDatePicker = false }
                    )
                }

                if (showCategories) {
                    CategoryBottomSheet(
                        categories = categories,
                        onSelect = { category ->
                            selectedCategory = category
                            editedBudget = it.copy(categoryId = category.id)
                        },
                        onDismiss = { showCategories = false }
                    )
                }

                if (showAccounts) {
                    AccountBottomSheet(
                        accounts = accounts,
                        currencySymbol = currencySymbol,
                        onSelect = { account ->
                            selectedAccount = account
                            editedBudget = it.copy(accountId = account.id)
                        },
                        onDismiss = { showAccounts = false }
                    )
                }
            }
        }
    }
}