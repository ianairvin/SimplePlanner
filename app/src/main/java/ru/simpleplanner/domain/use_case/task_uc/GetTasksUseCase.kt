package ru.simpleplanner.domain.use_case.task_uc

import kotlinx.coroutines.flow.Flow
import ru.simpleplanner.domain.entities.Task
import ru.simpleplanner.domain.repository.TaskRepository
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val taskRepository : TaskRepository
) {
    operator fun invoke(period: String) : Flow<List<Task>> {
        return taskRepository.getTasks(period)
    }
}