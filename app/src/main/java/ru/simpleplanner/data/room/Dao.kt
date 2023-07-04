package ru.simpleplanner.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Query("SELECT * FROM task WHERE (`check` = 0 AND date < :dateToday)" +
            " OR (date = :dateToday)" +
            " ORDER BY make_date_time ASC")
    fun getTodayTask(dateToday: Long) : Flow<List<TaskDB>>

    @Query("SELECT * FROM task WHERE date = :dateTomorrow" +
            " ORDER BY make_date_time ASC")
    fun getTomorrowTask(dateTomorrow: Long) : Flow<List<TaskDB>>

    @Query("SELECT * FROM task WHERE (date > :dateTomorrow" +
            " AND  date <= :dateEndOfWeek)" +
            " ORDER BY make_date_time ASC")
    fun getWeekTask(dateTomorrow: Long, dateEndOfWeek: Long) : Flow<List<TaskDB>>

    @Query("SELECT * FROM task WHERE date IS NULL ORDER BY make_date_time ASC")
    fun getSomeDayTask() : Flow<List<TaskDB>>

    @Query("SELECT * FROM task WHERE (`check` = 1 AND date < :dateNow) ORDER BY make_date_time ASC")
    fun getDoneTask(dateNow: Long) : Flow<List<TaskDB>>

    @Query("SELECT * FROM task WHERE date = :date AND date > :dateToday" +
            " ORDER BY make_date_time ASC")
    suspend fun getTasksForSpecificDay(date: Long, dateToday: Long) : List<TaskDB>

    @Query("SELECT * FROM task WHERE date = :date" +
            " OR (`check` = 0 AND date < :date)" +
            " ORDER BY make_date_time ASC")
    suspend fun getTasksForTodayDay(date: Long) : List<TaskDB>

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

    @Query("SELECT numberOfRepeats FROM timer WHERE id = 1")
    suspend fun getNumberOfRepeats() : Int

    @Insert
    suspend fun insertPickedCalendarsId(pickedCalendar: PickedCalendarsDB)

    @Query("SELECT * FROM picked_calendar")
    suspend fun getPickedCalendars() : List<PickedCalendarsDB>

    @Query("DELETE FROM picked_calendar")
    suspend fun deleteAllPickedCalendarsId()

    @Update
    suspend fun updateOpenSectionTask(section: OpenSectionTaskDB)

    @Query("SELECT * FROM open_section_task")
    suspend fun getOpenSectionTask() : OpenSectionTaskDB

}