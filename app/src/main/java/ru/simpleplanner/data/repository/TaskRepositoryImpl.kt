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

    private val differenceBetweenTwoDaysOfWeek = arrayOf(
        arrayOf(0,1,2,3,4,5,6),
        arrayOf(6,0,1,2,3,4,5),
        arrayOf(5,6,0,1,2,3,4),
        arrayOf(4,5,6,0,1,2,3),
        arrayOf(3,4,5,6,0,1,2),
        arrayOf(2,3,4,5,6,0,1),
        arrayOf(1,2,3,4,5,6,0),
    )

    override suspend fun insertTask(task: Task){
        val taskDB = TaskDB (
            null,
            task.title,
            task.check,
            if (task.date == null) 0 else task.date!!.atStartOfDay(ZoneOffset.systemDefault()).toInstant().toEpochMilli(),
            task.date!!.dayOfWeek.value,
            task.makeDateTime!!.atZone(ZoneOffset.systemDefault())
                .toInstant().toEpochMilli(),
            task.repeatRule!!,
            task.note!!
        )
        dao.insertTask(taskDB)
    }

    override suspend fun updateTask(task: Task){
        val taskDB = TaskDB (
            task.id,
            task.title,
            task.check,
            if (task.date == null) 0 else task.date!!.atStartOfDay(ZoneOffset.systemDefault()).toInstant().toEpochMilli(),
            task.date!!.dayOfWeek.value,
            task.makeDateTime!!.atZone(ZoneOffset.systemDefault())
                .toInstant().toEpochMilli(),
            task.repeatRule!!,
            task.note!!
        )
        dao.updateTask(taskDB)
    }

    override suspend fun getOneTask(id: Int): Task {
        val taskDB = dao.getByIdTask(id)
        val date: LocalDate =
            when(taskDB.repeatRule) {
                "DAILY" -> LocalDate.now()
                "WEEKLY" -> LocalDate.now()
                    .plusDays(differenceBetweenTwoDaysOfWeek[taskDB.dayOfWeek][LocalDate.now().dayOfWeek.value].toLong())
                else -> Instant.ofEpochMilli(taskDB.date)
                        .atZone(ZoneId.systemDefault()).toLocalDate()
            }
        return Task(
            taskDB.id,
            taskDB.title,
            taskDB.check,
            date,
            Instant.ofEpochMilli(taskDB.makeDateTime)
                .atZone(ZoneId.systemDefault()).toLocalDateTime(),
            taskDB.repeatRule,
            taskDB.note
        )
    }

    override fun getTasks(period: String): Flow<List<Task>> {
        val listTasksBeforeMapping : Flow<List<TaskDB>>
        if(period == "Today"){
            listTasksBeforeMapping = dao.getTodayTask(LocalDate.now().atStartOfDay().
            atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli(),
                LocalDate.now().dayOfWeek.value)
        } else if (period == "Tomorrow"){
            listTasksBeforeMapping = dao.getTomorrowTask(LocalDate.now().plusDays(1)
                .atStartOfDay().atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli(),
                LocalDate.now().plusDays(1).dayOfWeek.value)
        } else if (period == "Week") {
            val dayThroughTheWeek = LocalDate.now().plusDays(6)
            val daysOfWeek = when(LocalDate.now().dayOfWeek.value){
                1 -> arrayOf(3,4,5,6,7)
                2 -> arrayOf(4,5,6,7,1)
                3 -> arrayOf(5,6,7,1,2)
                4 -> arrayOf(6,7,1,2,3)
                5 -> arrayOf(7,1,2,3,4)
                6 -> arrayOf(1,2,3,4,5)
                7 -> arrayOf(2,3,4,5,6)
                else -> {
                    arrayOf(0)
                }
            }
            listTasksBeforeMapping = dao.getWeekTask(
                LocalDate.now().plusDays(1).atStartOfDay().atZone(ZoneOffset.systemDefault())
                    .toInstant().toEpochMilli(),
                dayThroughTheWeek.atStartOfDay().atZone(ZoneOffset.systemDefault())
                    .toInstant().toEpochMilli(),
                daysOfWeek
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
                    Instant.ofEpochMilli(task.date)
                    .atZone(ZoneId.systemDefault()).toLocalDate(),
                    null,
                    null,
                    null
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
        val dayOfWeekInt = date.dayOfWeek.value
        val listTasksBeforeMapping = dao.getTasksForSpecificDay(dateLong, dayOfWeekInt)
        val listTasksAfterMapping : List<Task> = listTasksBeforeMapping.map {
                task -> Task(
                        task.id,
                        task.title,
                        task.check,
                        Instant.ofEpochMilli(task.date)
                            .atZone(ZoneId.systemDefault()).toLocalDate(),
                        null,
                        null,
                        null
                    )
                }

        return listTasksAfterMapping
    }
}