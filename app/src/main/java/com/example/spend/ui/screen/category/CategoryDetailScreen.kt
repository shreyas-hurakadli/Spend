package com.example.spend.ui.screen.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.spend.R
import com.example.spend.getFormattedAmount
import com.example.spend.ui.data.icons
import com.example.spend.ui.navigation.Routes
import com.example.spend.ui.screen.AppTopBar
import com.example.spend.ui.screen.DialogBox
import com.example.spend.ui.screen.NoTransactions
import com.example.spend.ui.screen.TransactionCard
import com.example.spend.ui.viewmodel.category.CategoryDetailViewModel

@Composable
fun CategoryDetailScreen(
    navHostController: NavHostController,
    viewModel: CategoryDetailViewModel = hiltViewModel()
) {
    val category by viewModel.category.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val totalCategoryAmount by viewModel.amount.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()

    var showDialogBox by remember { mutableStateOf(value = false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(id = R.string.category_detail),
                canNavigateBack = true,
                onBackClick = { navHostController.popBackStack() },
                actions = {
                    IconButton(onClick = { showDialogBox = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = stringResource(id = R.string.delete)
                        )
                    }
                }
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
        ) {
            LazyColumn(
                modifier = Modifier.weight(weight = 1f)
            ) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        category?.let { category ->
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .background(
                                        color = category.color,
                                        shape = RoundedCornerShape(size = 16.dp)
                                    )
                                    .size(size = 60.dp)
                            ) {
                                category.icon?.let { icon ->
                                    icons[icon]?.let { resourceId ->
                                        Icon(
                                            imageVector = ImageVector.vectorResource(id = resourceId),
                                            tint = MaterialTheme.colorScheme.onBackground,
                                            contentDescription = null,
                                            modifier = Modifier.size(size = 30.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(height = 8.dp))
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(height = 16.dp))
                            BasicText(
                                text = "$currencySymbol ${getFormattedAmount(value = totalCategoryAmount)}",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                autoSize = TextAutoSize.StepBased(
                                    minFontSize = 24.sp,
                                    maxFontSize = 48.sp
                                ),
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(height = 16.dp))
                            Text(
                                text = stringResource(id = R.string.transactions),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Spacer(modifier = Modifier.height(height = 8.dp))
                        }
                        if (transactions.isEmpty()) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                NoTransactions()
                            }
                        }
                    }
                }
                if (transactions.isNotEmpty()) {
                    items(items = transactions) { entryCategory ->
                        TransactionCard(
                            entryCategory = entryCategory,
                            currencySymbol = currencySymbol,
                            iconTint = Color.Black,
                            showDate = true,
                            clickable = true,
                            onClick = { navHostController.navigate(Routes.EntryDetailScreen(id = entryCategory.entry.id)) }
                        )
                    }
                }
            }
            OutlinedButton(
                onClick = { navHostController.navigate(route = Routes.EditCategoryScreen) },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp)
            ) {
                Text(text = stringResource(id = R.string.edit_category))
            }
        }

        if (showDialogBox) {
            DialogBox(
                onDismissRequest = { showDialogBox = false },
                onConfirmation = {
                    viewModel.deleteCategory()
                    navHostController.popBackStack()
                },
                dialogTitle = stringResource(id = R.string.delete_category),
                dialogText = stringResource(id = R.string.category_delete_message),
                confirmText = {
                    Text(
                        text = stringResource(id = R.string.delete),
                        color = MaterialTheme.colorScheme.error
                    )
                },
                dismissText = { Text(text = stringResource(id = R.string.cancel)) },
            )
        }
    }
}