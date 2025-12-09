package com.example.spend.ui.screen.entry

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.spend.R
import com.example.spend.ui.screen.AppTopBar
import com.example.spend.ui.theme.SpendTheme

@Composable
fun EntryDetailScreen(
    navHostController: NavHostController
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.transaction_detail),
                canNavigateBack = true,
                onBackClick = { navHostController.popBackStack() },
                actions = {
                    TextButton(onClick = {}) {
                        Text(text = "Edit", color = MaterialTheme.colorScheme.secondary)
                    }
                }
            )
        }
    ) { innerPadding ->

    }
}

@Preview
@Composable
private fun EntryDetailsScreenPreview() {
    SpendTheme {
        EntryDetailScreen(
            navHostController = rememberNavController()
        )
    }
}