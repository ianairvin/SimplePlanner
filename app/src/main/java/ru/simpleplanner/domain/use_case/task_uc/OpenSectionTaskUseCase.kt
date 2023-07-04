package ru.simpleplanner.domain.use_case.task_uc

import ru.simpleplanner.domain.repository.TaskRepository
import javax.inject.Inject

class OpenSectionTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend fun updateOpenSectionTask(openSection: List<Boolean>){
        taskRepository.updateOpenSectionTask(openSection)
    }

    suspend fun getOpenSectionTask() : List<Boolean>{
        return taskRepository.getOpenSectionTask()
    }
}