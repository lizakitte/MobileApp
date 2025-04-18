package com.example.lab1.Lab06.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

enum class Priority() {
    High, Medium, Low
}

@Entity(tableName = "tasks")
data class TodoTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val deadline: LocalDate,
    var isDone: Boolean,
    val priority: Priority
) {
    fun toModel(): TodoTask {
        return TodoTask(
            id = id,
            deadline = deadline,
            isDone = isDone,
            priority = priority,
            title = title
        )
    }

    companion object {
        fun fromModel(model: TodoTask): TodoTaskEntity {
            return TodoTaskEntity(
                id = model.id,
                title = model.title,
                priority = model.priority,
                isDone = model.isDone,
                deadline = model.deadline
            )
        }
    }
}


@Entity(tableName = "tasks")
data class TodoTask(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val deadline: LocalDate = LocalDate.now(),
    var isDone: Boolean = false,
    val priority: Priority = Priority.Low
)