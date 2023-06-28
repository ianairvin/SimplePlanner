package ru.simpleplanner.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.simpleplanner.data.room.Dao
import ru.simpleplanner.data.room.TaskDB
import ru.simpleplanner.domain.entities.Task
import ru.simpleplanner.domain.repository.TaskRepository
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor (
    private val dao: Dao
): TaskRepository {

    val differenceBetweenTwoDaysOfWeek = arrayOf(
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
        dao.insert(taskDB)
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
        dao.update(taskDB)
    }

    override suspend fun getOneTask(id: Int): Task {
        val taskDB = dao.getById(id)
        val date: LocalDate =
            if(taskDB.repeatRule == "DAILY") { LocalDate.now() }
            else if (taskDB.repeatRule == "WEEKLY") {
                LocalDate.now()
                    .plusDays(differenceBetweenTwoDaysOfWeek[taskDB.dayOfWeek][LocalDate.now().dayOfWeek.value].toLong())
            } else { Instant.ofEpochMilli(taskDB.date)
                                    .atZone(ZoneId.systemDefault()).toLocalDate() }
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
        var listTasksBeforeMapping : Flow<List<TaskDB>>
        if(period == "Today"){
            listTasksBeforeMapping = dao.getToday(LocalDate.now().atStartOfDay().
            atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli(),
                LocalDate.now().dayOfWeek.value)
        } else if (period == "Tomorrow"){
            listTasksBeforeMapping = dao.getTomorrow(LocalDate.now().plusDays(1)
                .atStartOfDay().atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli(),
                LocalDate.now().plusDays(1).dayOfWeek.value)
        } else if (period == "Week") {
            var sunday = LocalDate.now()
            while(sunday.dayOfWeek != DayOfWeek.SUNDAY){
                sunday = sunday.plusDays(1)
            }
            val dayOfWeek = when(LocalDate.now().dayOfWeek.value){
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
            listTasksBeforeMapping = dao.getWeek(
                LocalDate.now().plusDays(1).atStartOfDay().atZone(ZoneOffset.systemDefault())
                    .toInstant().toEpochMilli(),
                sunday.atStartOfDay().atZone(ZoneOffset.systemDefault())
                    .toInstant().toEpochMilli(),
                dayOfWeek
            )
        } else {
            listTasksBeforeMapping = dao.getSomeDay()
        }

        val listTasksAfterMapping : Flow<List<Task>> = listTasksBeforeMapping.map {
            list -> list.map {
                task -> Task(
                    task.id,
                    task.title,
                    task.check,
                    null,
                    null,
                    null,
                    null
                )
            }
        }

        return listTasksAfterMapping
    }

    override suspend fun editStatusTasks(id: Int, check: Boolean) {
        dao.changeStatus(id, check)
    }

    override suspend fun deleteTask(id: Int) {
        dao.delete(id)
    }
}