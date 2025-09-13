package com.example.spend.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.Navigator
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.spend.R
import com.example.spend.ui.navigation.Routes
import com.example.spend.ui.theme.SpendTheme
import com.example.spend.ui.viewmodel.SetupViewModel
import com.example.spend.validateCurrency

@Composable
fun SetupScreen(
    navHostController: NavHostController,
    viewModel: SetupViewModel = hiltViewModel(),
) {
    Scaffold(
        topBar = { AppTopBar(title = stringResource(R.string.setup)) }
    ) { innerPadding ->
        val isFirstLogin by viewModel.isFirstLogin.collectAsState()

        if (!isFirstLogin) {
            navHostController.navigate(Routes.HomeScreen)
        } else {
            val initialAccount by viewModel.initialAccount.collectAsState()
            val initialAmount by viewModel.initialAmount.collectAsState()

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    OutlinedTextField(
                        value = initialAccount,
                        onValueChange = { value ->
                            if (value.trim() != "")
                                viewModel.updateInitialAccount(value)
                        },
                        label = {
                            Text(
                                text = stringResource(R.string.account),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = initialAmount,
                        onValueChange = { value ->
                            if (validateCurrency(value))
                                viewModel.updateInitialAmount(value)
                        },
                        label = {
                            Text(
                                text = stringResource(R.string.amount),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                    )
                    Spacer(Modifier.height(16.dp))
                    if (viewModel.isInputCorrect()) {
                        FloatingActionButton(
                            onClick = {
                                viewModel.updateDetails()
                                viewModel.registerFirstLogin()
                                navHostController.navigate(Routes.HomeScreen)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = stringResource(R.string.done),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun SetupScreenPreview() {
    SpendTheme {
        SetupScreen(navHostController = rememberNavController())
    }
}