package com.example.spend.ui.screen

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.spend.ui.icons
import com.example.spend.ui.pastelColors
import com.example.spend.ui.theme.SpendTheme
import com.example.spend.ui.viewmodel.CreateCategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCategoryScreen(
    navHostController: NavHostController,
    viewModel: CreateCategoryViewModel = hiltViewModel()
) {
    val category by viewModel.category.collectAsState()
    val selectedIndex by viewModel.selectedIndex.collectAsState()
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Create Category",
                canNavigateBack = true,
                onBackClick = { navHostController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                SegmentedControl(
                    options = listOf("Income", "Expenses"),
                    selectedIndex = selectedIndex,
                    onSegmentSelected = { viewModel.changeSelectedIndex() }
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(55.dp)
                            .background(color = category.color),
                        contentAlignment = Alignment.Center
                    ) {
                        if (icons[category.icon] != null) {
                            Icon(
                                imageVector = ImageVector.vectorResource(icons[category.icon]!!),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(4.dp))
                    OutlinedTextField(
                        value = category.name,
                        onValueChange = { viewModel.changeName(name = it) },
                        label = {
                            Text(
                                text = "Enter category name",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
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
                    color = Color.Black
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(8.dp)
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
                                    .padding(8.dp)
                                    .clip(CircleShape)
                                    .size(36.dp)
                                    .background(color)
                                    .border(
                                        width = if (color == category.color) 2.dp else 0.dp,
                                        color = MaterialTheme.colorScheme.onTertiary,
                                        shape = CircleShape
                                    )
                                    .clickable(
                                        enabled = true,
                                        onClick = { viewModel.changeColor(color) }
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
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(8.dp)
                ) {
                    LazyHorizontalGrid(
                        rows = GridCells.Fixed(count = 3),
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxHeight(0.3f)
                    ) {
                        items(items = icons.entries.toList()) { entry ->
                            IconButton(
                                onClick = { viewModel.changeLogo(entry.key) },
                                modifier = Modifier.wrapContentSize()
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(entry.value),
                                    contentDescription = entry.key,
                                    modifier = Modifier.size(24.dp),
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.weight(1f))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { viewModel.clear() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = { viewModel.save() },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Create")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun CreateCategoryScreenPreview() {
    SpendTheme {
        CreateCategoryScreen(navHostController = rememberNavController())
    }
}