package com.example.spend.ui.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.spend.R
import com.example.spend.data.room.Entry
import com.example.spend.getFormattedAmount
import com.example.spend.getLocalCurrencySymbol
import com.example.spend.longToDate
import com.example.spend.ui.navigation.Routes

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
        baseLineIcon = R.drawable.baseline_summarize,
        outlinedIcon = R.drawable.outline_summarize,
        route = Routes.ExpensesScreen,
        contentDescription = "Summary"
    ),
    NavigationIcon(
        baseLineIcon = R.drawable.baseline_settings,
        outlinedIcon = R.drawable.outline_settings,
        route = Routes.AccountScreen,
        contentDescription = "Settings"
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    onBackClick: () -> Unit = {},
    actions: @Composable (RowScope.() -> Unit) = {},
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
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
            }
        },
        actions = actions,
        modifier = modifier
    )
}

@Composable
fun AppBottomBar(
    currentScreenIndex: Int,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    navigationIcons: List<NavigationIcon> = navigationIcon
) {
    BottomAppBar(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 8.dp,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val n = navigationIcons.size
            for (i in 0 until n) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (i == currentScreenIndex) {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = ImageVector.vectorResource(navigationIcons[i].baseLineIcon),
                                contentDescription = navigationIcons[i].contentDescription,
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Text(
                            text = navigationIcons[i].contentDescription!!,
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.labelSmall
                        )
                    } else {
                        IconButton(onClick = { navHostController.navigate(navigationIcons[i].route) }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(navigationIcons[i].outlinedIcon),
                                contentDescription = navigationIcons[i].contentDescription,
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Text(
                            text = navigationIcons[i].contentDescription ?: "Default",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SnackBarMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.primary),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar(message)
        }
    }
}

@Composable
fun TransactionCard(
    entry: Entry,
    icon: ImageVector,
    iconTint: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    showDate: Boolean = false,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ), modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(50.dp)
                    .background(color = backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    tint = iconTint,
                    contentDescription = ""
                )
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
                        entry.category,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = (if (entry.isExpense) "- " else "") + getLocalCurrencySymbol() + " " + getFormattedAmount(
                            entry.amount
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
                            longToDate(entry.epochSeconds),
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
fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSegmentSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(16.dp))
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
