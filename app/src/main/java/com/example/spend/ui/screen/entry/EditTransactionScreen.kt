package com.example.spend.ui.screen.entry

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.spend.R
import com.example.spend.data.room.entry.Entry
import com.example.spend.epochSecondsToDate
import com.example.spend.getFormattedAmount
import com.example.spend.isCurrencyAppropriate
import com.example.spend.ui.screen.AccountBottomSheet
import com.example.spend.ui.screen.AppTopBar
import com.example.spend.ui.screen.CategoryBottomSheet
import com.example.spend.ui.screen.DatePicker
import com.example.spend.ui.screen.showToast
import com.example.spend.ui.viewmodel.entry.EditEntryViewModel

@Composable
fun EditTransactionScreen(
    navHostController: NavHostController,
    viewModel: EditEntryViewModel = hiltViewModel()
) {
    val entryCategory by viewModel.entry.collectAsState()
    val account by viewModel.account.collectAsState(initial = null)
    val category by viewModel.category.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()

    var editedEntry by remember(key1 = entryCategory) { mutableStateOf(value = entryCategory?.entry ?: Entry()) }
    var selectedCategory by remember(key1 = category) { mutableStateOf(value = entryCategory?.name ?: "") }
    var selectedAccount by remember(key1 = account) { mutableStateOf(value = account?.name ?: "") }

    var showDatePicker by remember { mutableStateOf(value = false) }
    var showAccountBottomSheet by remember { mutableStateOf(value = false) }
    var showCategoryBottomSheet by remember { mutableStateOf(value = false) }
    var isEditingAmount by remember { mutableStateOf(value = false) }
    var amountInput by remember(key1 = entryCategory) {
        mutableStateOf(
            value = getFormattedAmount(
                value = entryCategory?.entry?.amount ?: 0.00
            )
        )
    }
    var isEditingDescription by remember { mutableStateOf(value = false) }
    var editedDescription by remember { mutableStateOf(value = editedEntry.description) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val showToast by viewModel.showToast.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(key1 = showToast) {
        if (showToast && toastMessage.isNotBlank()) {
            showToast(message = toastMessage, context = context)
            viewModel.onToastShow()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(id = R.string.edit_transaction),
                canNavigateBack = true,
                onBackClick = { navHostController.popBackStack() }
            )
        },
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .padding(all = 8.dp)
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
        ) {
            if (isEditingAmount) {
                LaunchedEffect(key1 = Unit) {
                    focusRequester.requestFocus()
                }
                AutoSizeBasicTextField(
                    value = amountInput,
                    currencySymbol = currencySymbol,
                    onValueChange = { input ->
                        val reducedInput = if (input.length > 2) input.removeRange(
                            startIndex = 0,
                            endIndex = 2
                        ) else ""
                        if (reducedInput.isCurrencyAppropriate()) {
                            amountInput = reducedInput
                            editedEntry = editedEntry.copy(amount = amountInput.toDouble())
                        } else {
                            viewModel.showToast(message = "Invalid amount input")
                        }
                    },
                    baseStyle = MaterialTheme.typography.displayMedium.copy(
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    maxFontSize = 48.sp,
                    minFontSize = 16.sp,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    focusRequester = focusRequester,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                BasicText(
                    text = "$currencySymbol " + getFormattedAmount(
                        value = editedEntry.amount
                    ),
                    style = MaterialTheme.typography.displayMedium.copy(
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = 16.sp,
                        maxFontSize = 48.sp
                    ),
                    modifier = Modifier.clickable(
                        enabled = true,
                        onClick = {
                            amountInput = getFormattedAmount(value = editedEntry.amount)
                            isEditingAmount = true
                        }
                    )
                )
            }
            Spacer(modifier = Modifier.height(height = 16.dp))
            EditableDetailTile(
                title = stringResource(id = R.string.account),
                currentValue = if (selectedAccount != "") selectedAccount else (account?.name
                    ?: ""),
                icon = ImageVector.vectorResource(id = R.drawable.baseline_wallet),
                action = {
                    IconButton(
                        onClick = {
                            if ((entryCategory?.entry?.categoryId ?: 0L) in 3..4) {
                                viewModel.showToast(message = "Account of transfer transactions cannot be edited")
                            } else {
                                showAccountBottomSheet = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                }
            )
            EditableDetailTile(
                title = stringResource(id = R.string.category),
                currentValue = selectedCategory,
                icon = ImageVector.vectorResource(id = R.drawable.baseline_category),
                action = {
                    IconButton(
                        onClick = {
                            if ((entryCategory?.entry?.categoryId ?: 0L) in 3..4) {
                                viewModel.showToast(message = "Category of transfer transactions cannot be edited")
                            } else {
                                showCategoryBottomSheet = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                }
            )
            EditableDetailTile(
                title = stringResource(id = R.string.date),
                currentValue = epochSecondsToDate(
                    epochSeconds = editedEntry.epochSeconds
                ),
                icon = Icons.Default.DateRange,
                action = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null
                        )
                    }
                }
            )
            if (isEditingDescription) {
                LaunchedEffect(key1 = Unit) {
                    focusRequester.requestFocus()
                }
            }
            EditableDetailTile(
                title = stringResource(id = R.string.description),
                currentValue = if (isEditingDescription) editedDescription else editedEntry.description,
                icon = ImageVector.vectorResource(id = R.drawable.baseline_summarize),
                isEditable = isEditingDescription,
                onValueChange = { input ->
                    if (viewModel.validateDescription(input)) {
                        editedDescription = input
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        isEditingDescription = false
                        editedEntry = editedEntry.copy(description = editedDescription.trim())
                        keyboardController?.hide()
                    }
                ),
                focusRequester = focusRequester,
                action = {
                    IconButton(onClick = { isEditingDescription = true }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_pencil),
                            contentDescription = null
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.weight(weight = 1f))
            OutlinedButton(
                onClick = {
                    viewModel.editTransaction(
                        editedEntry = editedEntry,
                    )
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
                    onDismiss = { showDatePicker = false },
                    onDateSelected = {
                        editedEntry =
                            editedEntry.copy(
                                epochSeconds = it?.div(other = 1000L) ?: editedEntry.epochSeconds
                            )
                    }
                )
            }

            if (showAccountBottomSheet) {
                AccountBottomSheet(
                    accounts = accounts,
                    currencySymbol = currencySymbol,
                    onSelect = {
                        selectedAccount = it.name
                        editedEntry = editedEntry.copy(accountId = it.id)
                        showAccountBottomSheet = false
                    },
                    onDismiss = { showAccountBottomSheet = false }
                )
            }

            if (showCategoryBottomSheet) {
                CategoryBottomSheet(
                    categories = categories,
                    onSelect = {
                        selectedCategory = it.name
                        editedEntry = editedEntry.copy(categoryId = it.id)
                        showCategoryBottomSheet = false
                    },
                    onDismiss = { showCategoryBottomSheet = false }
                )
            }
        }
    }
}

@Composable
fun EditableDetailTile(
    title: String,
    currentValue: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isEditable: Boolean = false,
    onValueChange: (String) -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester? = null,
    action: @Composable (() -> Unit) = {}
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .wrapContentWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = RoundedCornerShape(size = 16.dp)
                )
                .size(size = 48.dp)
        ) {
            Icon(
                imageVector = icon,
                tint = MaterialTheme.colorScheme.onBackground,
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.width(width = 8.dp))
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.weight(weight = 1f)
        ) {
            Text(
                text = title,
                color = Color.Gray,
                style = MaterialTheme.typography.labelSmall
            )
            if (isEditable) {
                focusRequester?.let {
                    BasicTextField(
                        value = currentValue,
                        onValueChange = onValueChange,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        keyboardOptions = keyboardOptions,
                        keyboardActions = keyboardActions,
                        modifier = Modifier.focusRequester(focusRequester = it)
                    )
                }
            } else {
                Text(
                    text = currentValue,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        action()
    }
}


@Composable
private fun AutoSizeBasicTextField(
    value: String,
    currencySymbol: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    baseStyle: TextStyle = TextStyle.Default,
    maxFontSize: TextUnit = 48.sp,
    minFontSize: TextUnit = 16.sp,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    val textMeasurer = rememberTextMeasurer()

    BoxWithConstraints(modifier = modifier) {
        val scope = this
        val density = LocalDensity.current
        val availableWidthPx =
            with(receiver = density) { scope.minWidth.toPx() }.toInt() - with(receiver = density) { 16.dp.toPx() }.toInt()

        val computedFontSize: TextUnit = remember(key1 = value, key2 = availableWidthPx) {
            var size = maxFontSize
            if (value.isNotEmpty()) {
                while (size > minFontSize) {
                    val measured = textMeasurer.measure(
                        text = AnnotatedString(text = value),
                        style = baseStyle.copy(fontSize = size),
                        maxLines = 1,
                        softWrap = false,
                    )
                    if (measured.size.width <= availableWidthPx) break
                    val reduced = TextUnit(value = size.value * 0.9f, size.type)
                    size = if (reduced.value > minFontSize.value) reduced else minFontSize
                }
            }
            size
        }

        BasicTextField(
            value = "$currencySymbol $value",
            onValueChange = onValueChange,
            textStyle = baseStyle.copy(fontSize = computedFontSize),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.Center) {
                    innerTextField()
                }
            }
        )
    }
}
