package ru.simpleplanner.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.simpleplanner.domain.entities.Task
import java.time.LocalDate

interface TaskRepository {
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun getOneTask(id: Int) : Task
    fun getTasks(period: String) : Flow<List<Task>>
    suspend fun editStatusTasks(id: Int, check: Boolean)
    suspend fun deleteTask(id: Int)

    suspend fun getTasksForSpecificDay(date: LocalDate) : List<Task>

    suspend fun updateOpenSectionTask(openSection: List<Boolean>)

    suspend fun getOpenSectionTask() : List<Boolean>
}