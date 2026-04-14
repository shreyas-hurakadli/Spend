package com.example.spend.ui.screen.category

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.spend.R
import com.example.spend.ui.data.MAX_CATEGORY_NAME_LENGTH
import com.example.spend.ui.data.icons
import com.example.spend.ui.data.pastelColors
import com.example.spend.ui.screen.AppTopBar
import com.example.spend.ui.screen.SegmentedControl
import com.example.spend.ui.screen.showToast
import com.example.spend.ui.viewmodel.category.CategoryViewModel

@Composable
fun EditCategoryScreen(
    navHostController: NavHostController,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val showToast by viewModel.showToast.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    var editedCategory by remember { mutableStateOf(value = selectedCategory) }

    val context = LocalContext.current

    LaunchedEffect(key1 = showToast) {
        if (showToast && toastMessage.isNotBlank()) {
            showToast(message = toastMessage, context = context)
            viewModel.onToastShown()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(id = R.string.edit_category),
                canNavigateBack = true,
                onBackClick = { navHostController.popBackStack() }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .padding(all = 8.dp)
                .fillMaxSize()
        ) {
            SegmentedControl(
                options = listOf("Income", "Expenses"),
                selectedIndex = if (editedCategory.isExpense) 1 else 0,
                onSegmentSelected = { editedCategory = editedCategory.copy(isExpense = (it == 1)) }
            )
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(55.dp)
                        .background(
                            color = editedCategory.color,
                            shape = RoundedCornerShape(size = 16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    icons[editedCategory.icon]?.let { resourceId ->
                        Icon(
                            imageVector = ImageVector.vectorResource(id = resourceId),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
                Spacer(Modifier.width(4.dp))
                OutlinedTextField(
                    value = editedCategory.name,
                    onValueChange = { input -> editedCategory = editedCategory.copy(name = input) },
                    label = {
                        Text(
                            text = "Enter category name",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    isError = editedCategory.name.length > MAX_CATEGORY_NAME_LENGTH,
                    singleLine = true,
                    shape = RoundedCornerShape(size = 24.dp),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Select Color",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(size = 24.dp)
                    )
                    .padding(all = 8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(state = rememberScrollState())
                ) {
                    pastelColors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .padding(all = 8.dp)
                                .clip(CircleShape)
                                .size(36.dp)
                                .background(color)
                                .border(
                                    width = if (color == editedCategory.color) 2.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.onTertiary,
                                    shape = CircleShape
                                )
                                .clickable(
                                    enabled = true,
                                    onClick = {
                                        editedCategory = editedCategory.copy(color = color)
                                    }
                                )
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Select Icon",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(size = 24.dp)
                    )
                    .padding(all = 8.dp)
            ) {
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(count = 3),
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxHeight(fraction = 0.3f)
                ) {
                    items(
                        items = icons.entries.toList(),
                        key = { it.key }
                    ) { entry ->
                        IconButton(
                            onClick = { editedCategory = editedCategory.copy(icon = entry.key) },
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = entry.value),
                                contentDescription = entry.key,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            OutlinedButton(
                onClick = { viewModel.editCategory(editedCategory) },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.save_changes),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}