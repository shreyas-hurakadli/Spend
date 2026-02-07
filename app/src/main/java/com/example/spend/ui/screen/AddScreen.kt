package com.example.spend.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.spend.R
import com.example.spend.getTodayStart
import com.example.spend.longToDate
import com.example.spend.longToDayTime
import com.example.spend.ui.theme.SpendTheme
import com.example.spend.ui.viewmodel.AddViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

private val options = listOf("Income", "Expense", "Transfer")

private val buttons = listOf(
    listOf("+", "7", "8", "9"),
    listOf("-", "4", "5", "6"),
    listOf("x", "1", "2", "3"),
    listOf("÷", "0", ".", "=")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    navHostController: NavHostController,
    viewModel: AddViewModel = hiltViewModel()
) {
    var showAccountsBottomSheet by remember { mutableStateOf(false) }
    var showCategoryBottomSheet by remember { mutableStateOf(false) }
    var accountIndex by remember { mutableIntStateOf(0) }

    val showSnackBar by viewModel.showSnackBar.collectAsState()
    val snackBarMessage by viewModel.snackBarMessage.collectAsState()
    val selectedIndex = viewModel.selectedIndex
    val amount = viewModel.amount
    val time = viewModel.time
    val date = viewModel.date
    val description = viewModel.description
    val operator = viewModel.operator
    val incomeCategories by viewModel.incomeCategories.collectAsState()
    val expenseCategories by viewModel.expenseCategories.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val selectedCategory = viewModel.category.name
    val selectedToAccount = viewModel.toAccount.name
    val selectedFromAccount = viewModel.fromAccount.name

    val keyboardController = LocalSoftwareKeyboardController.current
    val snackBarScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(showSnackBar) {
        if (showSnackBar && snackBarMessage.isNotEmpty()) {
            snackBarScope.launch {
                snackBarHostState.showSnackbar(message = snackBarMessage)
                viewModel.toggleShowSnackBar()
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.add_entry),
                canNavigateBack = true,
                onBackClick = { navHostController.popBackStack() },
                actions = {
                    IconButton(onClick = { viewModel.save() }) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = stringResource(R.string.save),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->
        BoxWithConstraints(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 8.dp)
                .fillMaxSize()
        ) {
            val maxWidth = maxWidth
            val maxHeight = maxHeight
            Column {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(0.35f)
                ) {
                    SegmentedControl(
                        options = options,
                        selectedIndex = selectedIndex,
                        onSegmentSelected = {
                            viewModel.changeSelectedIndex(it)
                            viewModel.resetIds()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    EntrySpecificationUI(
                        selectedIndex = selectedIndex,
                        firstOnClick = {
                            accountIndex = 0
                            showAccountsBottomSheet = true
                        },
                        secondOnClick = {
                            if (selectedIndex == 2) {
                                accountIndex = 1
                                showAccountsBottomSheet = true
                            } else {
                                showCategoryBottomSheet = true
                            }
                        },
                        fromAccount = selectedFromAccount,
                        toAccount = selectedToAccount,
                        category = selectedCategory
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { viewModel.changeDescription(it) },
                        singleLine = false,
                        maxLines = 4,
                        placeholder = {
                            Text(
                                "Add Notes (Optional)",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { keyboardController?.hide() }
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }
                Spacer(Modifier.height(4.dp))
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(0.65f)
                ) {
                    CalculatorUI(
                        amount = amount,
                        onValueChange = { viewModel.changeAmount(it) },
                        onBackspaceClick = {
                            if (it.isEmpty() || it == "-" || it == "Infinit") viewModel.changeAmount(
                                "0"
                            )
                            else viewModel.changeAmount(it)
                        },
                        maxWidth = maxWidth,
                        maxHeight = maxHeight,
                        operation = operator,
                        updateOperator = {
                            if (it == "") viewModel.resetOperator()
                            else viewModel.changeOperator(it)
                        },
                        calculateAmount = { viewModel.calculateAnswer() },
                        addToAmount = { viewModel.changeAmount(amount + it) }
                    )
                    Spacer(Modifier.height(8.dp))
                    DateTimePicker(
                        date = date,
                        time = time,
                        onDateChange = {
                            val selectedDate =
                                Instant.ofEpochMilli(it ?: System.currentTimeMillis())
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                    .atStartOfDay(ZoneId.systemDefault())
                                    .toEpochSecond()

                            viewModel.changeDate(
                                value = selectedDate
                            )
                        },
                        onTimeChange = {
                            viewModel.changeTime(
                                value = ((it
                                    ?: System.currentTimeMillis()) / 1000L) - getTodayStart()
                            )
                        }
                    )
                }
                if (showAccountsBottomSheet) {
                    AccountBottomSheet(
                        accounts = accounts.filter { it.name != "All" },
                        onSelect = {
                            if (accountIndex == 0)
                                viewModel.changeFromAccount(value = it)
                            else
                                viewModel.changeToAccount(value = it)
                        },
                        onDismiss = { showAccountsBottomSheet = false },
                    )
                }
                if (showCategoryBottomSheet) {
                    CategoryBottomSheet(
                        categories =
                            if (selectedIndex == 0) incomeCategories.filter { it.name != "All" }
                            else expenseCategories.filter { it.name != "All" },
                        onSelect = { viewModel.changeCategoryId(value = it) },
                        onDismiss = { showCategoryBottomSheet = false },
                    )
                }
            }
        }
    }
}

@Composable
private fun CalculatorUI(
    amount: String,
    onValueChange: (String) -> Unit,
    onBackspaceClick: (String) -> Unit,
    updateOperator: (String) -> Unit,
    calculateAmount: () -> Unit,
    addToAmount: (String) -> Unit,
    operation: String,
    maxWidth: Dp,
    maxHeight: Dp
) {
    var textFieldHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = amount,
            onValueChange = onValueChange,
            shape = RoundedCornerShape(16.dp),
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.End,
                fontSize = 30.sp
            ),
            leadingIcon = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                ) {
                    Text(
                        text = operation,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton(onClick = { updateOperator("") }) {
                        Text(
                            text = "CE",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = { onBackspaceClick(amount.dropLast(n = 1)) }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.outline_backspace),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            },
            maxLines = 1,
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldHeight = with(receiver = density) {
                        coordinates.size.height.toDp()
                    }
                }
        )
        val buttonWidth = (maxWidth - 16.dp) / 4
        val availableHeightForButtons = maxHeight * 0.65f - textFieldHeight - 56.dp
        val totalSpacerHeight = 16.dp
        val buttonHeight = (availableHeightForButtons - totalSpacerHeight) / 4
        val buttonAspectRatio = (buttonWidth / buttonHeight).coerceIn(0.8f, 1.5f)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
                buttons.forEach { rowButtons ->
                    if (rowButtons == buttons.first()) {
                        Spacer(Modifier.height(4.dp))
                    }
                    CalculatorButtonRow(
                        buttons = rowButtons,
                        maxWidth = maxWidth,
                        buttonAspectRatio = buttonAspectRatio,
                        updateOperator = updateOperator,
                        calculateAmount = calculateAmount,
                        addToAmount = addToAmount
                    )
                    if (rowButtons != buttons.last()) {
                        Spacer(Modifier.height(4.dp))
                    }
                }
        }
    }
}

@Composable
private fun CalculatorButtonRow(
    buttons: List<String>,
    maxWidth: Dp,
    updateOperator: (String) -> Unit,
    calculateAmount: () -> Unit,
    addToAmount: (String) -> Unit,
    buttonAspectRatio: Float
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        buttons.forEach { button ->
            CalculatorButton(
                button = button,
                modifier = Modifier
                    .width((maxWidth - 16.dp) / 4)
                    .aspectRatio(buttonAspectRatio),
                onClick = {
                    when (button) {
                        "-", "+", "x", "÷" -> updateOperator(button)
                        "=" -> calculateAmount()
                        else -> addToAmount(button)
                    }
                }
            )
        }
    }
}

@Composable
private fun CalculatorButton(button: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val backgroundColor = when (button) {
        "-", "+", "x", "=", "÷" -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.background
    }
    val textColor = when (button) {
        "-", "+", "x", "=", "÷" -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onBackground
    }
    val fontWeight = when (button) {
        "-", "+", "x", "=", "÷" -> FontWeight.ExtraBold
        else -> FontWeight.Normal
    }
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        modifier = modifier
    ) {
        Text(
            text = button,
            color = textColor,
            fontWeight = fontWeight,
            fontSize = 24.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateTimePicker(
    time: Long,
    date: Long,
    onDateChange: (Long?) -> Unit,
    onTimeChange: (Long?) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Column {
        HorizontalDivider()
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        enabled = true,
                        onClick = { showDatePicker = true }
                    )
            ) {
                Text(
                    text = longToDate(longDate = date),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            VerticalDivider()
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        enabled = true,
                        onClick = { showTimePicker = true }
                    )
            ) {
                Text(text = longToDayTime(time), style = MaterialTheme.typography.titleMedium)
            }
        }
    }

    if (showTimePicker || showDatePicker) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (showTimePicker) {
                val (initialHour, initialMinute) = rememberSaveable {
                    val dayRelativeSeconds = (System.currentTimeMillis() / 1000) - getTodayStart()
                    (dayRelativeSeconds / 3600) to (dayRelativeSeconds / 216000)
                }
                val timePickerState = rememberTimePickerState(
                    initialHour = initialHour.toInt(),
                    initialMinute = initialMinute.toInt(),
                    is24Hour = true
                )

                Dialog(onDismissRequest = { showTimePicker = false }) {
                    TimePicker(
                        timePickerState = timePickerState,
                        onConfirm = {
                            onTimeChange(it)
                            showTimePicker = false
                        },
                        onDismiss = { showTimePicker = false }
                    )
                }
            }
            if (showDatePicker) {
                DatePicker(
                    onDateSelected = {
                        onDateChange(it)
                        showTimePicker = false
                    },
                    onDismiss = { showDatePicker = false }
                )
            }
        }
    }
}

@Composable
private fun EntrySpecificationUI(
    selectedIndex: Int,
    firstOnClick: () -> Unit,
    secondOnClick: (Int) -> Unit,
    fromAccount: String,
    toAccount: String,
    category: String,
    modifier: Modifier = Modifier,
) {

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = if (selectedIndex == 2) "From" else "Account",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall
            )
            SpecificationSelectionButton(
                text = if (fromAccount != "") fromAccount else "Account",
                icon = ImageVector.vectorResource(R.drawable.baseline_wallet),
                onClick = firstOnClick
            )
        }
        Spacer(Modifier.width(2.dp))
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = if (selectedIndex == 2) "To" else "Category",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall
            )
            SpecificationSelectionButton(
                text = if (selectedIndex == 2) {
                    if (toAccount != "") toAccount else "Account"
                } else {
                    if (category != "") category else "Category"
                },
                icon = if (selectedIndex == 2) ImageVector.vectorResource(R.drawable.baseline_wallet)
                else ImageVector.vectorResource(R.drawable.baseline_label),
                onClick = { secondOnClick(if (selectedIndex == 2) 1 else 0) }
            )
        }
    }
}

@Composable
private fun SpecificationSelectionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String = ""
) {
    OutlinedButton(onClick = onClick, shape = RoundedCornerShape(16.dp)) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = icon, contentDescription = contentDescription)
            Spacer(Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun AddScreenPreview() {
    SpendTheme {
        AddScreen(
            navHostController = rememberNavController()
        )
    }
}