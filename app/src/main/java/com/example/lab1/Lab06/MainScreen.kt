package com.example.lab1.Lab06

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.*

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(preferences: SharedPreferences, scheduleNotification: () -> Unit) {
    val navController = rememberNavController()
    //
    val postNotificationPermission =
        rememberPermissionState(permission = android.Manifest.permission.POST_NOTIFICATIONS)
    LaunchedEffect(key1 = true) {
        if (!postNotificationPermission.status.isGranted) {
            postNotificationPermission.launchPermissionRequest()
        }
    }

    NavHost(navController = navController, startDestination = "list") {
        composable(route = "list") { ListScreen(navController = navController) }
        composable("form") { FormScreen(navController = navController, scheduleNotification = scheduleNotification) }
        composable("preferences") { PreferencesScreen(navController = navController, preferences = preferences) }
    }
}


@Composable
fun PreferencesScreen(navController: NavController, preferences: SharedPreferences) {
    Scaffold(
        topBar = {
            AppTopBar(
                navController = navController,
                title = "Settings",
                showBackIcon = true,
                route = "settings",
            )
        },
        content = { pad ->
            Column(modifier = Modifier.padding(pad)) {
                val notifyDaysBefore = preferences.getLong("days", 1)
                val notifyHoursBefore = preferences.getLong("hours", 0)
                val notifyHourInterval = preferences.getLong("interval", 4)

                var days by remember { mutableLongStateOf(notifyDaysBefore) }
                var hours by remember { mutableLongStateOf(notifyHoursBefore) }
                var interval by remember { mutableLongStateOf(notifyHourInterval) }

                OutlinedTextField(
                    value = days.toString(),
                    onValueChange = {
                        if (it.isEmpty()) days = 0
                        else days = it.toLong()

                        with (preferences.edit()) {
                            putLong("days", days)
                            apply()
                        }
                    },
                    label = { Text("Days before deadline") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )

                OutlinedTextField(
                    value = hours.toString(),
                    onValueChange = {
                        if (it.isEmpty()) hours = 0
                        else hours = it.toLong()

                        with (preferences.edit()) {
                            putLong("hours", hours)
                            apply()
                        }
                    },
                    label = { Text("Hours before deadline") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )

                OutlinedTextField(
                    value = interval.toString(),
                    onValueChange = {
                        if (it.isEmpty()) interval = 0
                        else interval = it.toLong()

                        with (preferences.edit()) {
                            putLong("interval", interval)
                            apply()
                        }
                    },
                    label = { Text("Hour notification interval") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
        }
    )
}