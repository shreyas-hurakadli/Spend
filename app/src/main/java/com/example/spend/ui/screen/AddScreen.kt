package com.example.spend.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.spend.R
import com.example.spend.ui.theme.SpendTheme
import com.example.spend.ui.viewmodel.AddViewModel
import java.util.Calendar

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
    val selectedIndex = viewModel.selectedIndex
    val amount = viewModel.amount
    val description = viewModel.description
    val answer = viewModel.answer
    val operator = viewModel.operator

    Scaffold { innerPadding ->
        BoxWithConstraints(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 8.dp)
                .fillMaxSize()
        ) {
            val maxWidth = maxWidth
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .height(IntrinsicSize.Min)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(
                            enabled = true,
                            onClick = { navHostController.popBackStack() })
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.cancel)
                        )
                        Text(
                            text = stringResource(R.string.cancel),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(enabled = true, onClick = {})
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = stringResource(R.string.save),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = stringResource(R.string.save),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
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
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    EntrySpecificationUI(selectedIndex = selectedIndex)
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
                        answer = answer.toString(),
                        onValueChange = { viewModel.changeAmount(it) },
                        onBackspaceClick = {
                            if (it.isEmpty()) viewModel.changeAmount("0")
                            else viewModel.changeAmount(it)
                        },
                        maxWidth = maxWidth,
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
                        onDateChange = {},
                        onTimeChange = {}
                    )
                }
            }
        }
    }
}

@Composable
private fun CalculatorUI(
    amount: String,
    answer: String,
    onValueChange: (String) -> Unit,
    onBackspaceClick: (String) -> Unit,
    updateOperator: (String) -> Unit,
    calculateAmount: () -> Unit,
    addToAmount: (String) -> Unit,
    operation: String,
    maxWidth: Dp,
) {
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
                        text = (if (answer == "0.0") "" else answer) + " $operation",
                        fontSize = 16.sp,
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
                IconButton(onClick = { onBackspaceClick(amount.dropLast(n = 1)) }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.outline_backspace),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
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
    addToAmount: (String) -> Unit
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
                    .aspectRatio(1.1f),
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

@Composable
private fun DateTimePicker(onDateChange: () -> Unit, onTimeChange: () -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

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
            Text(text = "16 Sept 2015", style = MaterialTheme.typography.titleMedium)
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
            Text(text = "09:34 AM", style = MaterialTheme.typography.titleMedium)
        }
    }

    if (showTimePicker || showDatePicker) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (showTimePicker) {
                Dialog(onDismissRequest = { showTimePicker = false }) {
                    TimePicker(
                        onConfirm = {
                            showTimePicker = false
                        },
                        onDismiss = { showTimePicker = false }
                    )
                }
            }
            if (showDatePicker) {
                DatePicker(onDateSelected = {}, { showDatePicker = false })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePicker(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = false,
    )

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TimePicker(
                state = timePickerState,
            )
            Button(onClick = onDismiss) {
                Text("Dismiss picker")
            }
            Button(onClick = onConfirm) {
                Text("Confirm selection")
            }
        }
    }
}

@Composable
private fun EntrySpecificationUI(
    selectedIndex: Int,
    modifier: Modifier = Modifier
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
                text = "Account",
                icon = ImageVector.vectorResource(R.drawable.baseline_wallet),
                onClick = {}
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
                text = if (selectedIndex == 2) "Account" else "Category",
                icon = if (selectedIndex == 2) ImageVector.vectorResource(R.drawable.baseline_wallet)
                else ImageVector.vectorResource(R.drawable.baseline_label),
                onClick = {}
            )
        }
    }
}

@Composable
private fun SpecificationSelectionButton(
    text: String,
    icon: ImageVector,
    contentDescription: String = "",
    onClick: () -> Unit,
) {
    OutlinedButton(onClick, shape = RoundedCornerShape(16.dp)) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = icon, contentDescription = contentDescription)
            Spacer(Modifier.width(4.dp))
            Text(text = text, style = MaterialTheme.typography.titleMedium)
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