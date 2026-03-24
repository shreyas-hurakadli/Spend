package com.example.spend.ui.screen.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.spend.R
import com.example.spend.data.room.category.Category
import com.example.spend.ui.icons
import com.example.spend.ui.navigation.RouteNumbers
import com.example.spend.ui.screen.AppNavigationDrawer
import com.example.spend.ui.screen.AppTopBar
import com.example.spend.ui.viewmodel.category.CategoryViewModel
import kotlinx.coroutines.launch

@Composable
fun CategoryScreen(
    navHostController: NavHostController,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsState()

    val drawerScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    AppNavigationDrawer(
        currentScreenIndex = RouteNumbers.CATEGORY_PAGE.screenNumber,
        navHostController = navHostController,
        drawerState = drawerState,
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = stringResource(id = R.string.categories),
                    hasNavigationDrawer = true,
                    onNavigationDrawerClick = {
                        drawerScope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    },
                )
            }
        ) { innerPadding ->
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .padding(paddingValues = innerPadding)
                    .padding(all = 8.dp)
            ) {
                items(
                    items = categories,
                    key = { it.id }
                ) { category ->
                    CategoryView(
                        category = category,
                        onClick = {
                            viewModel.selectCategory(category = it)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryView(
    category: Category,
    onClick: (Category) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable(enabled = true, onClick = { onClick(category) })
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size = 55.dp)
                .background(color = category.color, shape = RoundedCornerShape(size = 16.dp))
        ) {
            category.icon?.let { icon ->
                icons[icon]?.let { resourceId ->
                    Icon(
                        imageVector = ImageVector.vectorResource(id = resourceId),
                        contentDescription = category.name,
                        modifier = Modifier.size(size = 30.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(width = 8.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelLarge
        )
    }
}