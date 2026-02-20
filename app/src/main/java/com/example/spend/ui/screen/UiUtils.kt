package com.example.spend.ui.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.spend.R
import com.example.spend.data.dto.EntryCategory
import com.example.spend.data.room.account.Account
import com.example.spend.data.room.category.Category
import com.example.spend.getFormattedAmount
import com.example.spend.longToDate
import com.example.spend.ui.accountIcons
import com.example.spend.ui.icons
import com.example.spend.ui.navigation.Routes
import kotlinx.coroutines.launch
import java.util.Calendar

data class NavigationIcon(
    @DrawableRes val baseLineIcon: Int,
    @DrawableRes val outlinedIcon: Int,
    val route: Routes,
    val contentDescription: String?
)

private val navigationIcon = listOf(
    NavigationIcon(
        baseLineIcon = R.drawable.baseline_home,
        outlinedIcon = R.drawable.outline_home,
        route = Routes.HomeScreen,
        contentDescription = "Home"
    ),
    NavigationIcon(
        baseLineIcon = R.drawable.baseline_entries,
        outlinedIcon = R.drawable.outline_entries,
        route = Routes.EntryScreen,
        contentDescription = "Transactions"
    ),
    NavigationIcon(
        baseLineIcon = R.drawable.baseline_wallet,
        outlinedIcon = R.drawable.baseline_wallet,
        route = Routes.AccountScreen,
        contentDescription = "Accounts"
    ),
    NavigationIcon(
        baseLineIcon = R.drawable.coin,
        outlinedIcon = R.drawable.coin,
        route = Routes.BudgetScreen,
        contentDescription = "Budgets"
    ),
    NavigationIcon(
        baseLineIcon = R.drawable.baseline_summarize,
        outlinedIcon = R.drawable.outline_summarize,
        route = Routes.ExpensesScreen,
        contentDescription = "Summary"
    ),
    NavigationIcon(
        baseLineIcon = R.drawable.baseline_settings,
        outlinedIcon = R.drawable.outline_settings,
        route = Routes.SettingsScreen,
        contentDescription = "Settings"
    ),
)

@Composable
fun NoTransactions(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(all = 32.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.no_transactions),
            tint = Color.Gray,
            contentDescription = null,
            modifier = Modifier
                .size(size = 150.dp)
        )
    }
    Spacer(Modifier.height(height = 16.dp))
    Text(
        text = stringResource(R.string.no_transactions),
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = stringResource(R.string.no_transactions_home_extended_message),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Light
    )
    Spacer(Modifier.height(height = 16.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    onBackClick: () -> Unit = {},
    hasNavigationDrawer: Boolean = false,
    onNavigationDrawerClick: () -> Unit = {},
    actions: @Composable (RowScope.() -> Unit) = {},
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
        },
        colors = TopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            } else if (hasNavigationDrawer) {
                IconButton(onClick = onNavigationDrawerClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = stringResource(R.string.menu)
                    )
                }
            }
        },
        actions = actions,
        modifier = modifier
    )
}

@Composable
fun AppNavigationDrawer(
    currentScreenIndex: Int,
    navHostController: NavHostController,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit)
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.primary,
                drawerTonalElevation = 0.dp,
                drawerShape = RectangleShape,
                drawerContentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = 0.7f)
            ) {
                LazyColumn(
                    modifier = Modifier.padding(all = 8.dp)
                ) {
                    itemsIndexed(items = navigationIcon) { index, item ->
                        NavigationDrawerItem(
                            label = {
                                Text(
                                    text = item.contentDescription ?: "Default",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            selected = (currentScreenIndex == index),
                            onClick = { navHostController.navigate(item.route) },
                            icon = {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = item.baseLineIcon),
                                    contentDescription = item.contentDescription,
                                    modifier = Modifier.size(size = 24.dp)
                                )
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.background,
                                unselectedContainerColor = Color.Transparent,
                                selectedIconColor = MaterialTheme.colorScheme.onBackground,
                                unselectedIconColor = MaterialTheme.colorScheme.background,
                                selectedTextColor = MaterialTheme.colorScheme.onBackground,
                                unselectedTextColor = MaterialTheme.colorScheme.background
                            )
                        )
                    }
                }
            }
        },
        modifier = modifier,
        scrimColor = Color.Transparent
    ) {
        content()
    }
}

@Composable
fun TransactionCard(
    entryCategory: EntryCategory,
    iconTint: Color,
    currencySymbol: String,
    modifier: Modifier = Modifier,
    showDate: Boolean = false,
    clickable: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = modifier.clickable(enabled = clickable, onClick = onClick)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(50.dp)
                    .background(color = entryCategory.color),
                contentAlignment = Alignment.Center
            ) {
                entryCategory.icon?.let {
                    icons[it]?.let {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = it),
                            tint = iconTint,
                            contentDescription = entryCategory.icon,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = entryCategory.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = (if (entryCategory.entry.isExpense) "- " else "") + currencySymbol + " " + getFormattedAmount(
                            entryCategory.entry.amount
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (showDate) {
                    Row(
                        horizontalArrangement = Arrangement.Absolute.Left,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = longToDate(longDate = entryCategory.entry.epochSeconds),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Thin
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DialogBox(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    confirmText: @Composable (() -> Unit),
    dismissText: @Composable (() -> Unit),
) {
    AlertDialog(
        title = { Text(text = dialogTitle) },
        text = { Text(text = dialogText) },
        containerColor = Color.LightGray,
        textContentColor = MaterialTheme.colorScheme.onBackground,
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(
                onClick = { onConfirmation() }
            ) {
                confirmText()
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() }
            ) {
                dismissText()
            }
        }
    )
}

@Composable
fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSegmentSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(24.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        options.forEachIndexed { index, label ->
            Text(
                text = label,
                color = if (selectedIndex == index) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (selectedIndex == index) MaterialTheme.colorScheme.background else Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(vertical = 8.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onSegmentSelected(index) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountBottomSheet(
    accounts: List<Account>,
    currencySymbol: String,
    onSelect: (Account) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items = accounts) { account ->
                AccountView(
                    account = account,
                    currencySymbol = currencySymbol
                ) {
                    onSelect(account)
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            onDismiss()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountView(
    account: Account,
    currencySymbol: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.wrapContentSize()
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(45.dp)
                        .background(color = account.color),
                    contentAlignment = Alignment.Center
                ) {
                    if (accountIcons[account.icon] != null) {
                        Icon(
                            imageVector = ImageVector.vectorResource(accountIcons[account.icon]!!),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
                Spacer(Modifier.width(2.dp))
                Text(
                    text = account.name,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(
                text = currencySymbol + " " + account.balance.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = if (account.balance >= 0.00) Color.Green else Color.Red
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryBottomSheet(
    categories: List<Category>,
    onSelect: (Category) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(count = 3)
        ) {
            items(items = categories) { category ->
                CategoryView(category = category) {
                    onSelect(category)
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            onDismiss()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryView(
    category: Category,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.wrapContentSize()
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(CircleShape)
                    .size(55.dp)
                    .background(color = category.color),
                contentAlignment = Alignment.Center
            ) {
                if (icons[category.icon] != null) {
                    Icon(
                        imageVector = ImageVector.vectorResource(icons[category.icon]!!),
                        tint = Color.Black,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Text(
                text = category.name,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(IntrinsicSize.Min)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        colors = DatePickerColors(
            containerColor = Color.LightGray,
            titleContentColor = Color.Black,
            headlineContentColor = Color.Black,
            weekdayContentColor = Color.DarkGray,
            subheadContentColor = Color.DarkGray,
            navigationContentColor = Color.Black,
            yearContentColor = Color.Black,
            disabledYearContentColor = Color.Gray,
            currentYearContentColor = Color.Black,
            selectedYearContentColor = Color.White,
            disabledSelectedYearContentColor = Color.Gray,
            selectedYearContainerColor = Color.Black,
            disabledSelectedYearContainerColor = Color.DarkGray,
            dayContentColor = Color.Black,
            disabledDayContentColor = Color.Gray,
            selectedDayContentColor = Color.White,
            disabledSelectedDayContentColor = Color.Gray,
            selectedDayContainerColor = Color.Black,
            disabledSelectedDayContainerColor = Color.DarkGray,
            todayContentColor = Color.Black,
            todayDateBorderColor = Color.Black,
            dayInSelectionRangeContainerColor = Color.DarkGray,
            dayInSelectionRangeContentColor = Color.White,
            dividerColor = Color.Gray,
            dateTextFieldColors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.DarkGray,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.DarkGray,
                cursorColor = Color.Black
            )
        )
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerColors(
                containerColor = Color.LightGray,
                titleContentColor = Color.Black,
                headlineContentColor = Color.Black,
                weekdayContentColor = Color.DarkGray,
                subheadContentColor = Color.DarkGray,
                navigationContentColor = Color.Black,
                yearContentColor = Color.Black,
                disabledYearContentColor = Color.Gray,
                currentYearContentColor = Color.Black,
                selectedYearContentColor = Color.White,
                disabledSelectedYearContentColor = Color.Gray,
                selectedYearContainerColor = Color.Black,
                disabledSelectedYearContainerColor = Color.DarkGray,
                dayContentColor = Color.Black,
                disabledDayContentColor = Color.Gray,
                selectedDayContentColor = Color.White,
                disabledSelectedDayContentColor = Color.Gray,
                selectedDayContainerColor = Color.Black,
                disabledSelectedDayContainerColor = Color.DarkGray,
                todayContentColor = Color.Black,
                todayDateBorderColor = Color.Black,
                dayInSelectionRangeContainerColor = Color.DarkGray,
                dayInSelectionRangeContentColor = Color.White,
                dividerColor = Color.Gray,
                dateTextFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.DarkGray,
                    cursorColor = Color.Black
                )
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(
    onConfirm: (Long?) -> Unit,
    onDismiss: () -> Unit,
    timePickerState: TimePickerState,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(size = 16.dp),
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min),
            color = Color.LightGray
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(all = 16.dp)
            ) {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerColors(
                        clockDialColor = Color.LightGray,
                        selectorColor = Color.Black,
                        containerColor = Color.Gray,
                        periodSelectorBorderColor = Color.Black,
                        clockDialSelectedContentColor = Color.White,
                        clockDialUnselectedContentColor = Color.DarkGray,
                        periodSelectorSelectedContainerColor = Color.Black,
                        periodSelectorUnselectedContainerColor = Color.Transparent,
                        periodSelectorSelectedContentColor = Color.White,
                        periodSelectorUnselectedContentColor = Color.Black,
                        timeSelectorSelectedContainerColor = Color.Black,
                        timeSelectorUnselectedContainerColor = Color.Transparent,
                        timeSelectorSelectedContentColor = Color.White,
                        timeSelectorUnselectedContentColor = Color.Black,
                    )
                )
                Button(onClick = onDismiss) {
                    Text(stringResource(R.string.dismiss_picker))
                }
                Button(
                    onClick = {
                        val selectedHour = timePickerState.hour
                        val selectedMinute = timePickerState.minute
                        val calendar = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, selectedHour)
                            set(Calendar.MINUTE, selectedMinute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        onConfirm(calendar.timeInMillis)
                    }
                ) {
                    Text(stringResource(R.string.confirm_selection))
                }
            }
        }
    }
}