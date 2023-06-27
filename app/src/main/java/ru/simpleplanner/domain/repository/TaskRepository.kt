package ru.simpleplanner.domain.repository

import ru.simpleplanner.domain.entities.Task

interface TaskRepository {
    fun getTasks(period: String) : List<Task>

    fun editStatusTasks(id: Int)
}