package com.example.lab1.Lab06

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lab1.Lab06.data.AppViewModelProvider
import com.example.lab1.Lab06.data.ListViewModel
import com.example.lab1.Lab06.data.TodoTask
import com.example.lab1.Lab06.data.TodoTaskForm
import com.example.lab1.Lab06.data.TodoTaskUiState
import com.example.lab1.R
import kotlinx.coroutines.launch


@Composable
fun ListScreen(
    navController: NavController,
    viewModel: ListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val listUiState by viewModel.listUiState.collectAsState()
    Scaffold(
        topBar = {
            AppTopBar(
                navController = navController,
                title = "List",
                showBackIcon = false,
                route = "list"
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                content = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add task",
                        modifier = Modifier.scale(1.5f)
                    )
                },
                onClick = {
                    navController.navigate("form")
                }
            )
        },
        content = { it ->
            LazyColumn(
                modifier = Modifier.padding(it)
            ) {
                items(items = listUiState.items, key = { it.id }) {
                    ListItem(it, viewModel)
                }
            }
        }
    )
}


@Composable
fun ListItem(item: TodoTask, viewModel: ListViewModel, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(120.dp)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(Modifier.padding(all = 3.dp)) {
            var iconTint1: Color
            var iconTint2: Color
            Column(Modifier.padding(horizontal = 2.dp)) {
                var isDone by remember { mutableStateOf(item.isDone) }
                val coroutineScope = rememberCoroutineScope()
                val iconResourse2 = R.drawable.baseline_done_24
                val iconResourse1 = R.drawable.baseline_not_done_24

                if (isDone) {
                    iconTint1 = Color(0, 0, 0, 100)
                    iconTint2 = Color(0, 200, 0, 255)
                } else {
                    iconTint1 = Color(200, 0, 0, 255)
                    iconTint2 = Color(0, 0, 0, 100)
                }

                Text(text = "Done:", fontSize = 11.sp)

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            isDone = false
                            viewModel.repository.updateItem(
                                item.copy(
                                    isDone = isDone
                                )
                            )
                        }
                    }
                ) {
                    Icon(painter = painterResource(iconResourse1), contentDescription = "isDone", tint = iconTint1)
                }
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            isDone = true
                            viewModel.repository.updateItem(
                                item.copy(
                                    isDone = isDone
                                )
                            )
                        }
                    }
                ) {
                    Icon(painter = painterResource(iconResourse2), contentDescription = "isDone", tint = iconTint2)
                }
            }
            Column(Modifier.padding(horizontal = 5.dp)) {
                Text(text = item.title, fontSize = 18.sp, fontWeight = FontWeight.W500)
                Text(text = " ")
                Text(text = "Priority: ", fontSize = 11.sp)
                Text(text = item.priority.toString())
            }
            Column(Modifier.padding(horizontal = 20.dp)) {
                Text(text = " ")
                Text(text = " ")
                Text(text = "Deadline: ", fontSize = 11.sp)
                Text(text = item.deadline.toString())
            }
        }
    }
}

@Composable
fun TodoTaskInputBody(
    todoUiState: TodoTaskUiState,
    onItemValueChange: (TodoTaskForm) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TodoTaskInputForm(
            item = todoUiState.todoTask,
            onValueChange = onItemValueChange,
            modifier = modifier
        )
    }
}
