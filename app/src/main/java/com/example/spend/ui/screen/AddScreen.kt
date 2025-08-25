package com.example.spend.ui.screen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.spend.R
import com.example.spend.ui.theme.SpendTheme
import com.example.spend.ui.viewmodel.AddViewModel
import com.example.spend.validateCurrency

val categories = listOf("Food", "Essentials", "Entertainment", "Education")

@Composable
fun AddScreen(
    navHostController: NavHostController,
    viewModel: AddViewModel = hiltViewModel()
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    val amount by viewModel.amount.collectAsState()
    val uiState = viewModel.uiState.collectAsState()
    val showSnackBar by viewModel.showSnackBar.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.add_entry),
                canNavigateBack = true,
                onBackClick = { navHostController.popBackStack() }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.value.category,
                    onValueChange = {},
                    label = {
                        Text(
                            text = "Category",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    trailingIcon = {
                        IconButton(onClick = { isExpanded = !isExpanded }) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                tint = MaterialTheme.colorScheme.onBackground,
                                contentDescription = null
                            )
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.baseline_label),
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = null
                        )
                    },
                    singleLine = true,
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                DropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false },
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            onClick = {
                                viewModel.updateTag(category)
                                isExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = {
                    isError = !validateCurrency(it)
                    viewModel.updateAmount(it)

                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                ),
                label = {
                    Text(
                        text = "Bill",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                isError = isError,
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                leadingIcon = { Text("₹", color = MaterialTheme.colorScheme.onBackground) },
                singleLine = true,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.value.description,
                onValueChange = {
                    if (!validateCurrency(it))
                    viewModel.updateDescription(it)
                },
                label = {
                    Text(
                        text = "Description (Optional)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp
                ),
                singleLine = false,
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.baseline_pencil),
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!isError && amount != "") {
                viewModel.updateBill(amount.toDouble())
                FloatingActionButton(
                    onClick = {
                        keyboardController?.hide()
                        viewModel.updateDate()
                        viewModel.updateAmount("")
                        viewModel.insertData()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.add_entry)
                    )
                }
            }

            if (showSnackBar) {
                SnackBarMessage(
                    message = stringResource(R.string.transaction_success_message),
                    modifier = Modifier
                )
            }
        }
    }
}

@Preview
@Composable
private fun AddScreenPreview() {
    SpendTheme {
        AddScreen(
            navHostController = rememberNavController()
        )
    }
}