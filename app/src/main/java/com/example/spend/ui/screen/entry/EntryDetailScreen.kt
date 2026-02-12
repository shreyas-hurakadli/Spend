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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.spend.R
import com.example.spend.epochSecondsToDate
import com.example.spend.getFormattedAmount
import com.example.spend.getLocalCurrencySymbol
import com.example.spend.ui.navigation.Routes
import com.example.spend.ui.screen.AppTopBar
import com.example.spend.ui.screen.DialogBox
import com.example.spend.ui.theme.SpendTheme
import com.example.spend.ui.viewmodel.entry.EntryViewModel
import kotlinx.coroutines.launch

@Composable
fun EntryDetailScreen(
    navHostController: NavHostController,
    viewModel: EntryViewModel = hiltViewModel()
) {
    val selectedEntry by viewModel.selectedEntry.collectAsState()
    val selectedEntryAccount by viewModel.selectedEntryAccount.collectAsState(initial = null)

    var showDialogBox by rememberSaveable { mutableStateOf(value = false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.transaction_detail),
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
                .fillMaxSize()
                .padding(all = 8.dp)
        ) {
            if (selectedEntry == null) {
                CircularProgressIndicator()
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxHeight(fraction = 0.2f)
                ) {
                    BasicText(
                        text = (getLocalCurrencySymbol()
                            ?: "$") + " " + getFormattedAmount(
                            value = selectedEntry?.entry?.amount ?: 0.00
                        ),
                        style = MaterialTheme.typography.displayMedium.copy(
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        autoSize = TextAutoSize.StepBased(
                            minFontSize = 24.sp,
                            maxFontSize = 48.sp
                        ),
                    )
                }
                DetailRow(
                    icon = ImageVector.vectorResource(id = R.drawable.baseline_wallet),
                    detail = stringResource(R.string.account),
                    information = selectedEntryAccount?.name ?: "",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(height = 8.dp))
                DetailRow(
                    icon = ImageVector.vectorResource(id = R.drawable.baseline_label),
                    detail = stringResource(R.string.category),
                    information = selectedEntry?.name ?: "",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(height = 8.dp))
                DetailRow(
                    icon = Icons.Default.DateRange,
                    detail = stringResource(R.string.date),
                    information = epochSecondsToDate(
                        epochSeconds = selectedEntry?.entry?.epochSeconds
                            ?: (System.currentTimeMillis() / 1000L)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(height = 8.dp))
                DetailRow(
                    icon = ImageVector.vectorResource(id = R.drawable.note),
                    detail = stringResource(R.string.description),
                    information = selectedEntry?.entry?.description ?: "",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.weight(weight = 1f))
                OutlinedButton(
                    onClick = { showDialogBox = true },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.delete_transaction))
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
                dialogText = "Are you sure you want to delete this transaction? This action cannot be undone.",
                confirmText = { Text(text = "Delete", color = MaterialTheme.colorScheme.error) },
                dismissText = { Text(text = "Cancel") },
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
            modifier = Modifier
                .background(color = Color.LightGray, shape = RoundedCornerShape(size = 8.dp))
                .padding(all = 8.dp)
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