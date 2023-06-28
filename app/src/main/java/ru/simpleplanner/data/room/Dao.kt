package ru.simpleplanner.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek

@Dao
interface Dao {

    @Query("SELECT * FROM task WHERE (date = :dateToday)" +
            " OR (repeatRule = :daily)" +
            " OR (repeatRule = :weekly AND dayOfWeek = :dayOfWeek)" +
            " ORDER BY make_date_time ASC")
    fun getToday(dateToday: Long, dayOfWeek: Int, daily: String = "DAILY", weekly: String = "WEEKLY") : Flow<List<TaskDB>>

    @Query("SELECT * FROM task WHERE date = :dateTomorrow" +
            " OR repeatRule = 'DAILY'" +
            " OR (repeatRule = 'WEEKLY' AND dayOfWeek = :dayOfWeek)" +
            " ORDER BY make_date_time ASC")
    fun getTomorrow(dateTomorrow: Long, dayOfWeek: Int) : Flow<List<TaskDB>>

    @Query("SELECT * FROM task WHERE repeatRule = 'DAILY'" +
            " OR (date > :dateTomorrow" +
            " AND  date <= :dateEndOfWeek)" +
            " OR (repeatRule = 'WEEKLY' AND dayOfWeek IN (:arg))" +
            " ORDER BY make_date_time ASC")
    fun getWeek(dateTomorrow: Long, dateEndOfWeek: Long, arg: Array<Int>) : Flow<List<TaskDB>>

    @Query("SELECT * FROM task WHERE date = 0 ORDER BY make_date_time ASC")
    fun getSomeDay() : Flow<List<TaskDB>>

    @Query("SELECT * FROM task WHERE id = :id")
    suspend fun getById(id: Int) : TaskDB

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: TaskDB)

    @Update
    suspend fun update(task: TaskDB)

    @Query("DELETE FROM task WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("UPDATE task SET `check` = :check WHERE id = :id")
    suspend fun changeStatus(id: Int, check: Boolean)

}