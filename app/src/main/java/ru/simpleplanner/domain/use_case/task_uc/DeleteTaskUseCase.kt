package ru.simpleplanner.domain.use_case.task_uc

import ru.simpleplanner.domain.repository.TaskRepository
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val taskRepository : TaskRepository
) {
    operator suspend fun invoke(id: Int) {
        return taskRepository.deleteTask(id)
    }
}