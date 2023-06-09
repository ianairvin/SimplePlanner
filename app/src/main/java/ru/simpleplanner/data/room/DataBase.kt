package ru.simpleplanner.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TaskDB::class, TimerDB::class, PickedCalendarsDB::class, OpenSectionTaskDB::class],
    version = 1
)

abstract class DataBase : RoomDatabase() {
    abstract val dao: Dao
    companion object {
        const val DB_NAME = "simple_planner_db"
    }
}