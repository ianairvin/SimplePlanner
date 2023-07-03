package ru.simpleplanner.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.simpleplanner.data.room.Dao
import ru.simpleplanner.data.room.TaskDB
import ru.simpleplanner.domain.entities.Task
import ru.simpleplanner.domain.repository.TaskRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor (
    private val dao: Dao
): TaskRepository {
        override suspend fun insertTask(task: Task){

        val taskDB = TaskDB (
            null,
            task.title,
            task.check,
            if (task.date == null) null else task.date!!.atStartOfDay(ZoneOffset.systemDefault()).toInstant().toEpochMilli(),
            task.makeDateTime!!.atZone(ZoneOffset.systemDefault())
                .toInstant().toEpochMilli(),
            task.note!!,
            task.priority
        )
        dao.insertTask(taskDB)
    }

    override suspend fun updateTask(task: Task){
        val taskDB = TaskDB (
            task.id,
            task.title,
            task.check,
            if (task.date == null) null else task.date!!.atStartOfDay(ZoneOffset.systemDefault()).toInstant().toEpochMilli(),
            task.makeDateTime!!.atZone(ZoneOffset.systemDefault())
                .toInstant().toEpochMilli(),
            task.note!!,
            task.priority
        )
        dao.updateTask(taskDB)
    }

    override suspend fun getOneTask(id: Int): Task {
        val taskDB = dao.getByIdTask(id)
        return Task(
            taskDB.id,
            taskDB.title,
            taskDB.check,
            if (taskDB.date != null) Instant.ofEpochMilli(taskDB.date!!)
                .atZone(ZoneId.systemDefault()).toLocalDate() else null,
            Instant.ofEpochMilli(taskDB.makeDateTime)
                .atZone(ZoneId.systemDefault()).toLocalDateTime(),
            taskDB.note,
            taskDB.priority
        )
    }

    override fun getTasks(period: String): Flow<List<Task>> {
        val listTasksBeforeMapping : Flow<List<TaskDB>>
        if(period == "Today"){
            listTasksBeforeMapping = dao.getTodayTask(LocalDate.now().atStartOfDay().
            atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli())
        } else if (period == "Tomorrow"){
            listTasksBeforeMapping = dao.getTomorrowTask(LocalDate.now().plusDays(1)
                .atStartOfDay().atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli())
        } else if (period == "Week") {
            val dayThroughTheWeek = LocalDate.now().plusDays(6)
            listTasksBeforeMapping = dao.getWeekTask(
                LocalDate.now().plusDays(1).atStartOfDay().atZone(ZoneOffset.systemDefault())
                    .toInstant().toEpochMilli(),
                dayThroughTheWeek.atStartOfDay().atZone(ZoneOffset.systemDefault())
                    .toInstant().toEpochMilli()
            )
        } else if (period == "SomeDay"){
            listTasksBeforeMapping = dao.getSomeDayTask()
        } else {
            listTasksBeforeMapping = dao.getDoneTask(LocalDate.now().atStartOfDay().atZone(ZoneOffset.systemDefault())
                .toInstant().toEpochMilli())
        }

        val listTasksAfterMapping : Flow<List<Task>> = listTasksBeforeMapping.map {
            list -> list.map {
                task -> Task(
                    task.id,
                    task.title,
                    task.check,
                    if (task.date == null) null else Instant.ofEpochMilli(task.date!!)
                    .atZone(ZoneId.systemDefault()).toLocalDate(),
                    null,
                    null,
                    task.priority
                )
            }
        }

        return listTasksAfterMapping
    }

    override suspend fun editStatusTasks(id: Int, check: Boolean) {
        dao.changeStatusTask(id, check)
    }

    override suspend fun deleteTask(id: Int) {
        dao.deleteTask(id)
    }

    override suspend fun getTasksForSpecificDay(date: LocalDate): List<Task> {
        val dateLong = date.atStartOfDay(ZoneOffset.systemDefault()).toInstant().toEpochMilli()
        val listTasksBeforeMapping =
            if(date == LocalDate.now()) {
                dao.getTasksForTodayDay(dateLong)
            } else {
                dao.getTasksForSpecificDay(dateLong)
            }
        val listTasksAfterMapping : List<Task> = listTasksBeforeMapping.map {
                task -> Task(
                        task.id,
                        task.title,
                        task.check,
                        if (task.date == null) null else Instant.ofEpochMilli(task.date!!)
                            .atZone(ZoneId.systemDefault()).toLocalDate(),
                        null,
                        null,
                        task.priority
                    )
                }

        return listTasksAfterMapping
    }
}