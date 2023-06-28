package ru.simpleplanner.domain.use_case.task_uc

import ru.simpleplanner.domain.entities.Task
import ru.simpleplanner.domain.repository.TaskRepository
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator suspend fun invoke(task: Task){
        return taskRepository.updateTask(task)
    }
}