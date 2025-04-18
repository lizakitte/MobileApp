package com.example.lab1.Lab06

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.util.fastMinByOrNull
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.lab1.Lab06.data.AppContainer
import com.example.lab1.Lab06.data.LocalDateConverter
import com.example.lab1.Lab06.data.TodoApplication
import com.example.lab1.Lab06.ui.theme.Lab1Theme
import kotlinx.coroutines.launch
import java.time.LocalDate

const val notificationID = 121
const val channelID = "Lab06 channel"
const val titleExtra = "title"
const val messageExtra = "message"

class Lab06Activity : ComponentActivity() {
    var alarmIntent: PendingIntent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        createNotificationChannel()
        container = (this.application as TodoApplication).container

        scheduleAlarmForClosestTask()

        setContent {
            Lab1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "WSEI Labs",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(getPreferences(MODE_PRIVATE), {
                        scheduleAlarmForClosestTask()
                    })
                }
            }
        }
    }

    private fun createNotificationChannel() {
        val name = "Lab06 channel"
        val descriptionText = "Lab06 is channel for notifications for approaching tasks."
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID , name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        lateinit var container: AppContainer
    }

    fun scheduleAlarmForClosestTask() {
        val tasks = container.todoTaskRepository.getAllAsStream()
        lifecycleScope.launch {
            tasks.collect { tasks ->
                if (tasks.isEmpty()) return@collect

                val closestTask = tasks.minBy { it.deadline }

                val alarmManager = getSystemService(AlarmManager::class.java)
                if (alarmIntent != null) alarmManager.cancel(alarmIntent!!)

                scheduleAlarm(LocalDateConverter.toMillis(closestTask.deadline), closestTask.title)
            }
        }
    }

    fun scheduleAlarm(time: Long, taskName: String) {
        var time = time

        val preferences = getPreferences(MODE_PRIVATE)
        val notificationIntervalHours = preferences.getLong("interval", 4)
        val notificationDaysBefore = preferences.getLong("days", 1)
        val notificationHoursBefore = preferences.getLong("hours", 0)

        val intent = Intent(applicationContext, NotificationBroadcastReceiver::class.java)
        intent.putExtra(titleExtra, "Deadline")
        intent.putExtra(messageExtra, "The deadline for completing the task '$taskName' is approaching")

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        time -= notificationDaysBefore * AlarmManager.INTERVAL_DAY
        time -= notificationHoursBefore * AlarmManager.INTERVAL_HOUR

        val now = LocalDateConverter.toMillis(LocalDate.now())
        if (time < now) time = now + 1000

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            time,
            notificationIntervalHours * AlarmManager.INTERVAL_HOUR,
            pendingIntent,
        )
    }
}