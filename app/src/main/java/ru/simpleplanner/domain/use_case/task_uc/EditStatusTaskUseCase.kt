package ru.simpleplanner.domain.use_case.task_uc

import ru.simpleplanner.domain.entities.Task
import ru.simpleplanner.domain.repository.TaskRepository
import javax.inject.Inject

class EditStatusTaskUseCase @Inject constructor(
    private val taskRepository : TaskRepository
) {
    operator fun invoke(id: Int) {
        return taskRepository.editStatusTasks(id)
    }
}