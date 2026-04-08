package com.example.spend.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.spend.R
import com.example.spend.ui.navigation.Routes
import com.example.spend.ui.theme.SpendTheme

@Composable
fun IntroductionScreen(
    navHostController: NavHostController
) {
    val localConfiguration = LocalConfiguration.current
    val screenWidth = localConfiguration.screenWidthDp.dp

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 4 })

    Scaffold { innerPadding ->
        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> PagedScreen(
                    screenWidth = screenWidth,
                    icon = ImageVector.vectorResource(id = R.drawable.piggy_bank),
                    title = stringResource(id = R.string.title_1),
                    description = stringResource(id = R.string.title_1_message),
                    index = page + 1,
                    modifier = Modifier.padding(paddingValues = innerPadding),
                ) {
                    Text(
                        text = stringResource(id = R.string.next),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                1 -> PagedScreen(
                    screenWidth = screenWidth,
                    icon = ImageVector.vectorResource(id = R.drawable.goal),
                    title = stringResource(id = R.string.title_2),
                    description = stringResource(id = R.string.title_2_message),
                    index = page + 1,
                    modifier = Modifier.padding(paddingValues = innerPadding)

                ) {
                    Text(
                        text = stringResource(id = R.string.next),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                2 -> PagedScreen(
                    screenWidth = screenWidth,
                    icon = ImageVector.vectorResource(id = R.drawable.chart),
                    title = stringResource(id = R.string.title_3),
                    description = stringResource(id = R.string.title_3_message),
                    index = page + 1,
                    modifier = Modifier.padding(paddingValues = innerPadding)
                ) {
                    Text(
                        text = stringResource(id = R.string.next),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                3 -> PagedScreen(
                    screenWidth = screenWidth,
                    icon = ImageVector.vectorResource(id = R.drawable.shield),
                    title = stringResource(id = R.string.title_4),
                    description = stringResource(id = R.string.title_4_message),
                    index = page + 1,
                    onClick = { navHostController.navigate(Routes.SelectCurrencyScreen) },
                    modifier = Modifier.padding(paddingValues = innerPadding)
                ) {
                    Text(
                        text = stringResource(id = R.string.get_started),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun PagedScreen(
    screenWidth: Dp,
    icon: ImageVector,
    title: String,
    description: String,
    index: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    buttonAction: @Composable (() -> Unit),
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(all = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .weight(weight = 0.6f)
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = if (screenWidth > 360.dp) Modifier
                    .padding(all = 70.dp)
                    .size(size = 180.dp)
                else Modifier
                    .padding(all = 50.dp)
                    .size(size = 150.dp)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .weight(weight = 0.4f)
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(height = 16.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.weight(weight = 1f))
            ScrollIndicator(
                index = index,
                total = 4,
                size = 8.dp,
                height = 8.dp,
                width = 16.dp
            )
            Spacer(modifier = Modifier.height(height = 8.dp))
            if (index == 4) {
                OutlinedButton(
                    onClick = onClick,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    buttonAction()
                }
            }
        }
    }
}

@Preview
@Composable
private fun IntroductionScreenPreview() {
    SpendTheme {
        IntroductionScreen(navHostController = rememberNavController())
    }
}