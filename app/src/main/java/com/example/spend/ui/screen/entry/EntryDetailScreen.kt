package com.example.spend.ui.screen.entry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.spend.R
import com.example.spend.epochSecondsToDate
import com.example.spend.getFormattedAmount
import com.example.spend.ui.navigation.Routes
import com.example.spend.ui.screen.AppTopBar
import com.example.spend.ui.screen.DialogBox
import com.example.spend.ui.viewmodel.entry.EntryDetailViewModel

@Composable
fun EntryDetailScreen(
    navHostController: NavHostController,
    viewModel: EntryDetailViewModel = hiltViewModel()
) {
    val entry by viewModel.entry.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()
    val account by viewModel.account.collectAsState(initial = null)

    var showDialogBox by remember { mutableStateOf(value = false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.transaction_detail),
                canNavigateBack = true,
                onBackClick = { navHostController.popBackStack() },
                actions = {
                    IconButton(onClick = { showDialogBox = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = stringResource(R.string.delete)
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
                .fillMaxSize()
                .padding(all = 8.dp)
                .verticalScroll(state = rememberScrollState())
        ) {
            if (entry == null || account == null) {
                CircularProgressIndicator()
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxHeight(fraction = 0.2f)
                ) {
                    BasicText(
                        text = "$currencySymbol " + getFormattedAmount(
                            value = entry?.entry?.amount ?: 0.00
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
                    )
                }
                Spacer(modifier = Modifier.height(height = 16.dp))
                DetailRow(
                    icon = ImageVector.vectorResource(id = R.drawable.baseline_wallet),
                    detail = stringResource(R.string.account),
                    information = account?.name ?: "",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(height = 8.dp))
                DetailRow(
                    icon = ImageVector.vectorResource(id = R.drawable.baseline_category),
                    detail = stringResource(R.string.category),
                    information = entry?.name ?: "",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(height = 8.dp))
                DetailRow(
                    icon = Icons.Default.DateRange,
                    detail = stringResource(R.string.date),
                    information = epochSecondsToDate(
                        epochSeconds = entry?.entry?.epochSeconds
                            ?: (System.currentTimeMillis() / 1000L)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(height = 8.dp))
                entry?.entry?.description?.let {
                    DetailRow(
                        icon = ImageVector.vectorResource(id = R.drawable.note),
                        detail = stringResource(R.string.description),
                        information = if (it.length <= 30) it else "",
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (it.length > 30) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Start
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(weight = 1f))
                OutlinedButton(
                    onClick = {
                        navHostController.navigate(
                            Routes.EditTransactionScreen(
                                id = entry?.entry?.id ?: -1L
                            )
                        )
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.edit_transaction),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
        if (showDialogBox) {
            DialogBox(
                onDismissRequest = { showDialogBox = false },
                onConfirmation = {
                    viewModel.deleteTransaction()
                    navHostController.popBackStack()
                },
                dialogTitle = stringResource(id = R.string.delete_transaction),
                dialogText = stringResource(id = R.string.transaction_delete_message),
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

@Composable
fun DetailRow(
    icon: ImageVector,
    detail: String,
    information: String,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
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
        Spacer(Modifier.width(width = 8.dp))
        Text(
            text = detail,
            color = Color.Gray,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.weight(weight = 1f))
        Text(
            text = information,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}