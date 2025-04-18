package com.example.lab1.Lab06.data

import android.app.Application
import android.content.Context
import com.example.lab1.Lab06.NotificationHandler


interface AppContainer {
    val todoTaskRepository: TodoTaskRepository
    val notificationHandler: NotificationHandler
    val dateProvider: CurrentDateProvider
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val todoTaskRepository: TodoTaskRepository by lazy {
        DatabaseTodoTaskRepository(AppDatabase.getInstance(context).taskDao())
    }
    override val notificationHandler: NotificationHandler by lazy {
        NotificationHandler(context)
    }
    override val dateProvider: CurrentDateProvider by lazy {
        DateProvider()
    }
}

class TodoApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this.applicationContext)
    }
}

