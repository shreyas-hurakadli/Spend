package com.example.spend.ui.screen

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spend.LongToDate
import com.example.spend.R
import com.example.spend.data.room.Entry

data class NavigationIcon(
    @DrawableRes val baseLineIcon: Int,
    @DrawableRes val outlinedIcon: Int,
    val contentDescription: String?
)

val navigationIcon = listOf<NavigationIcon>(
    NavigationIcon(
        baseLineIcon = R.drawable.baseline_home,
        outlinedIcon = R.drawable.outline_home,
        contentDescription = "Home"
    ),
    NavigationIcon(
        baseLineIcon = R.drawable.baseline_insert_chart,
        outlinedIcon = R.drawable.outlined_insert_chart,
        contentDescription = "Expenses"
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
            containerColor = Color.White,
            scrolledContainerColor = Color.White,
            navigationIconContentColor = Color(0xFF8C6EFF),
            titleContentColor = Color.Black,
            actionIconContentColor = Color(0xFF8C6EFF)
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcons: List<NavigationIcon> = navigationIcon
) {
    BottomAppBar(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 8.dp,
        containerColor = Color(0xFFF5F5F5)
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val n = navigationIcons.size
            for (i in 0 until n) {
                if (i == currentScreenIndex) {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = ImageVector.vectorResource(navigationIcons[i].baseLineIcon),
                            contentDescription = navigationIcons[i].contentDescription,
                            tint = Color(0xFF8C6EFF),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                } else {
                    IconButton(onClick = onClick) {
                        Icon(
                            imageVector = ImageVector.vectorResource(navigationIcons[i].outlinedIcon),
                            contentDescription = navigationIcons[i].contentDescription,
                            tint = Color(0xFF8C6EFF),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SegmentedControl(
    selectedIndex: Int,
    onSegmentSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("Daily", "Weekly", "Monthly")
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF353839), shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        options.forEachIndexed { index, text ->
            val isSelected = selectedIndex == index
            Text(
                text = text,
                color = if (isSelected) Color.Black else Color.White,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (isSelected) Color.White else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(vertical = 8.dp)
                    .clickable { onSegmentSelected(index) },
            )
        }
    }
}

@Composable
fun InfoBar(
    balance: String,
    expense: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
            .animateContentSize(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.total_balance),
                color = Color(0xFF4CAF50),
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = balance,
                color = Color(0xFF4CAF50),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.total_expense),
                color = Color(0xFFF44336),
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = expense,
                color = Color(0xFFF44336),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
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
        modifier = Modifier.fillMaxWidth(),
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
    modifier: Modifier = Modifier,
    showDate: Boolean = false
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .padding(bottom = 16.dp)
            .background(color = Color.Transparent)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = entry.tag,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.padding(4.dp))
                Text(
                    text = entry.description,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray
                )
            }
            Column {
                Text(
                    text = "- ₹" + entry.bill.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
                if (showDate) {
                    Spacer(Modifier.padding(4.dp))
                    Text(
                        text = LongToDate(entry.date),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}
