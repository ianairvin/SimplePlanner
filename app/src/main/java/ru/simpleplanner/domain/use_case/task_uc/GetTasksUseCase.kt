package ru.simpleplanner.domain.use_case.event_uc

import ru.simpleplanner.domain.entities.Task
import ru.simpleplanner.domain.repository.TaskRepository
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val taskRepository : TaskRepository
) {
    operator fun invoke(period: String) : List<Task> {
        return taskRepository.getTasks(period)
    }
}