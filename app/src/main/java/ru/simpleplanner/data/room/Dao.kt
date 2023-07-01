package ru.simpleplanner.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Query("SELECT * FROM task WHERE (date = :dateToday)" +
            " OR (repeatRule = :daily)" +
            " OR (repeatRule = :weekly AND dayOfWeek = :dayOfWeek)" +
            " ORDER BY make_date_time ASC")
    fun getTodayTask(dateToday: Long, dayOfWeek: Int, daily: String = "DAILY", weekly: String = "WEEKLY") : Flow<List<TaskDB>>

    @Query("SELECT * FROM task WHERE date = :dateTomorrow" +
            " OR repeatRule = 'DAILY'" +
            " OR (repeatRule = 'WEEKLY' AND dayOfWeek = :dayOfWeek)" +
            " ORDER BY make_date_time ASC")
    fun getTomorrowTask(dateTomorrow: Long, dayOfWeek: Int) : Flow<List<TaskDB>>

    @Query("SELECT * FROM task WHERE repeatRule = 'DAILY'" +
            " OR (date > :dateTomorrow" +
            " AND  date <= :dateEndOfWeek)" +
            " OR (repeatRule = 'WEEKLY' AND dayOfWeek IN (:arg))" +
            " ORDER BY make_date_time ASC")
    fun getWeekTask(dateTomorrow: Long, dateEndOfWeek: Long, arg: Array<Int>) : Flow<List<TaskDB>>

    @Query("SELECT * FROM task WHERE date = 0 ORDER BY make_date_time ASC")
    fun getSomeDayTask() : Flow<List<TaskDB>>

    @Query("SELECT * FROM task WHERE id = :id")
    suspend fun getByIdTask(id: Int) : TaskDB

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(task: TaskDB)

    @Update
    suspend fun updateTask(task: TaskDB)

    @Query("DELETE FROM task WHERE id = :id")
    suspend fun deleteTask(id: Int)

    @Query("UPDATE task SET `check` = :check WHERE id = :id")
    suspend fun changeStatusTask(id: Int, check: Boolean)

    @Update
    suspend fun updateTime(timer: TimerDB)

    @Query("SELECT timeWork FROM timer WHERE id = 1")
    suspend fun getTimeWork() : Long

    @Query("SELECT timeShortBreak FROM timer WHERE id = 1")
    suspend fun getTimeShortBreak() : Long

    @Query("SELECT timeLongBreak FROM timer WHERE id = 1")
    suspend fun getTimeLongBreak() : Long

}