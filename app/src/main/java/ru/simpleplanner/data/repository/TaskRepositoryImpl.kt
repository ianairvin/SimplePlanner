package ru.simpleplanner.data.repository

import android.app.Application
import ru.simpleplanner.domain.entities.Task
import ru.simpleplanner.domain.repository.TaskRepository
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor (
    app: Application
): TaskRepository {
    override fun getTasks(period: String): List<Task> {
        val task = Task(
            1,
            "task",
            false,
            LocalDate.now(),
            LocalDateTime.now()
        )
        val list = mutableListOf<Task>(task, task, task)
        if(period == "Today"){
            return list
        } else {
            return emptyList()
        }
    }

    override fun editStatusTasks(id: Int) {

    }
}