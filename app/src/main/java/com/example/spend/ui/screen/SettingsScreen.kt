package com.example.spend.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.spend.R
import com.example.spend.ui.theme.SpendTheme

@Composable
fun AccountScreen(
    navHostController: NavHostController,
) {
    Scaffold(
        topBar = {
            AppTopBar(title = stringResource(R.string.settings))
        },
        bottomBar = {
            AppBottomBar(
                currentScreenIndex = 2,
                navHostController
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(200.dp),
                    contentDescription = stringResource(R.string.display_picture)
                )
                Text(
                    text = "John Doe",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium
                )

            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                SettingTile(
                    name = "Clear Data",
                    action = {}
                )
                SettingTile(
                    name = "Privacy",
                    action = {
                        IconButton({}) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, "")
                        }
                    }
                )
                SettingTile(
                    name = "Currency",
                    action = {
                        IconButton({}) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, "")
                        }
                    }
                )
                SettingTile(
                    name = "Country",
                    action = {
                        IconButton({}) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, "")
                        }
                    }
                )
                SettingTile(
                    name = "Sync",
                    action = {
                        IconButton({}) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, "")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingTile(
    name: String,
    action: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(text = name, style = MaterialTheme.typography.titleMedium)
        action()
    }
}

@Preview
@Composable
private fun AccountScreenPreview() {
    SpendTheme {
        AccountScreen(navHostController = rememberNavController())
    }
}