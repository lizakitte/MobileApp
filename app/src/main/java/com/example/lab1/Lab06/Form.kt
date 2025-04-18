package com.example.lab1.Lab06

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lab1.Lab06.data.FormViewModel
import com.example.lab1.Lab06.data.FormViewModelProvider
import com.example.lab1.Lab06.data.LocalDateConverter
import com.example.lab1.Lab06.data.Priority
import com.example.lab1.Lab06.data.TodoTaskForm
import kotlinx.coroutines.launch

@Composable
fun FormScreen(
    navController: NavController,
    viewModel: FormViewModel = viewModel(factory = FormViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            AppTopBar(
                navController = navController,
                title = "Form",
                showBackIcon = true,
                route = "form",
                onSaveClick = {
                    coroutineScope.launch {
                        viewModel.save()
                        navController.navigate("list")
                    }
                }
            )
        }
    )
    {
        TodoTaskInputBody(
            todoUiState = viewModel.todoTaskUiState,
            onItemValueChange = viewModel::updateUiState,
            modifier = Modifier.padding(it)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoTaskInputForm(
    item: TodoTaskForm,
    modifier: Modifier = Modifier,
    onValueChange: (TodoTaskForm) -> Unit = {},
    enabled: Boolean = true
) {
    Text("Task name:", style = MaterialTheme.typography.headlineSmall)
    TextField(
        value = item.title,
        onValueChange = {
            onValueChange(item.copy(title = it))
        })
    val datePickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        yearRange = IntRange(2000, 2030),
        initialSelectedDateMillis = item.deadline,
    )
    var showDialog by remember {
        mutableStateOf(false)
    }

    var selectedPriority by remember { mutableStateOf(Priority.Low) }

    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Task priority:", style = MaterialTheme.typography.headlineSmall)
        for (prio in Priority.entries) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedPriority == prio, onClick = {
                        selectedPriority =
                            prio; onValueChange(item.copy(priority = selectedPriority.name))
                    })
                Text(
                    text = prio.name, modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                showDialog = true
            }),
        text = "Due date: ${LocalDateConverter.fromMillis(item.deadline)}",
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.headlineSmall
    )
    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = {
                showDialog = false
            },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    onValueChange(item.copy(deadline = datePickerState.selectedDateMillis!!))
                }) {
                    Text("Pick")
                }
            }
        ) {
            DatePicker(state = datePickerState, showModeToggle = true)
        }
    }
}
