package ru.simpleplanner.domain.use_case.task_uc

import kotlinx.coroutines.flow.Flow
import ru.simpleplanner.domain.entities.Task
import ru.simpleplanner.domain.repository.TaskRepository
import java.time.LocalDate
import javax.inject.Inject

class GetTasksForSpecificDay @Inject constructor(
    private val taskRepository : TaskRepository
) {
    suspend operator fun invoke(date: LocalDate) : List<Task>  {
        return taskRepository.getTasksForSpecificDay(date)
    }
}