package ru.simpleplanner.domain.use_case.task_uc

import kotlinx.coroutines.flow.Flow
import ru.simpleplanner.domain.entities.Task
import ru.simpleplanner.domain.repository.TaskRepository
import javax.inject.Inject

class GetOneTaskUseCase @Inject constructor(
    private val taskRepository : TaskRepository
) {
    operator suspend fun invoke(id: Int) : Task {
        return taskRepository.getOneTask(id)
    }
}