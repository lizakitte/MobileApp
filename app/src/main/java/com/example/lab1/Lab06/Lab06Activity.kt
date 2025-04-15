package com.example.lab1.Lab06

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lab1.Lab06.ui.theme.Lab1Theme
import com.example.lab1.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class Lab06Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Lab1Theme {
        Greeting("Android")
    }
}

@Composable
fun ListScreen(navController: NavController) {
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
        content = {
            LazyColumn(modifier = Modifier.padding(it)) {
                items(todoTasks.size) { item ->
                    ListItem(item = todoTasks[item])
                }
            }
        }
    )
}

data class FormState(val title: String, val priority: Priority, val date: LocalDate)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    val datePickerState = rememberDatePickerState()
    var priority by remember { mutableStateOf(Priority.Low) }
    Scaffold(
        topBar = {
            val date = Instant.ofEpochMilli(datePickerState.selectedDateMillis ?: 0)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            AppTopBar(
                navController = navController,
                title = "Form",
                showBackIcon = true,
                route = "form",
                formState = FormState(title, priority, date),
            )
        },
        content = { pad ->
            Column(
                modifier = Modifier.padding(pad), verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") })

                var showPriority by remember { mutableStateOf(false) }

                IconButton(onClick = { showPriority = !showPriority }) {
                    Icon(Icons.Default.Star, contentDescription = "Select priority")
                }

                Row {
                    Text(text = priority.toString())

                    DropdownMenu(
                        expanded = showPriority,
                        onDismissRequest = { showPriority = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Low") },
                            onClick = { priority = Priority.Low; showPriority = false },
                        )
                        DropdownMenuItem(
                            text = { Text("Medium") },
                            onClick = { priority = Priority.Medium; showPriority = false },
                        )
                        DropdownMenuItem(
                            text = { Text("High") },
                            onClick = { priority = Priority.High; showPriority = false },
                        )
                    }
                }

                var showDialog by remember { mutableStateOf(false) }

                if (showDialog) {
                    DatePickerDialog(onDismissRequest = { showDialog = false }, confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Ok")
                        }
                    }, dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                    }) {
                        DatePicker(state = datePickerState)
                    }
                }

                IconButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Select deadline")
                }
            }
        })
}


@Composable
fun MainScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "list") {
        composable("list") { ListScreen(navController = navController) }
        composable("form") { FormScreen(navController = navController) }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Lab1Theme {
        MainScreen(
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    navController: NavController,
    title: String,
    showBackIcon: Boolean,
    route: String,
    formState: FormState? = null,) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        ),
        title = { Text(text = title) },
        navigationIcon = {
            if (showBackIcon) {
                IconButton(onClick = { navController.navigate("list") }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            if (route == "form") {
                OutlinedButton(
                    onClick = {
                        if (formState == null) throw IllegalArgumentException("formState")

                        val todo = TodoTask(
                            title = formState.title,
                            deadline = formState.date,
                            isDone = false,
                            priority = formState.priority,
                        )
                        todoTasks.add(todo)
                        navController.navigate("list")
                    }) {
                    Text(
                        text = "Zapisz", fontSize = 18.sp
                    )
                }
            } else {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "")
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = "")
                }
            }
        }
    )
}

val todoTasks = mutableListOf(
    TodoTask("Programming", LocalDate.of(2024, 4, 18), false, Priority.Low),
    TodoTask("Teaching", LocalDate.of(2024, 5, 12), false, Priority.High),
    TodoTask("Learning", LocalDate.of(2024, 6, 28), true, Priority.Low),
    TodoTask("Cooking", LocalDate.of(2024, 8, 18), false, Priority.Medium),
)

enum class Priority() {
    High, Medium, Low
}

data class TodoTask(
    val title: String,
    val deadline: LocalDate,
    val isDone: Boolean,
    val priority: Priority
)

@Composable
fun ListItem(item: TodoTask, modifier: Modifier = Modifier) {
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
            Column(Modifier.padding(horizontal = 2.dp)) {
                val iconResourse: Int
                val iconTint: Color
                if(item.isDone) {
                    iconResourse = R.drawable.baseline_done_24
                    iconTint = Color(0, 200, 0, 255)
                } else {
                    iconResourse = R.drawable.baseline_not_done_24
                    iconTint = Color(200, 0, 0, 255)
                }
                Icon(painter = painterResource(iconResourse), contentDescription = "isDone", tint = iconTint)
            }
            Column(Modifier.padding(horizontal = 5.dp)) {
                Text(text = item.title, fontSize = 18.sp, fontWeight = FontWeight.W500)
                Text(text = " ")
                Text(text = "Priority: ", fontSize = 10.sp)
                Text(text = item.priority.toString())
            }
            Column(Modifier.padding(horizontal = 20.dp)) {
                Text(text = " ")
                Text(text = " ")
                Text(text = "Deadline: ", fontSize = 10.sp)
                Text(text = item.deadline.toString())
            }
        }
    }
}