package ru.simpleplanner.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timer")
data class TimerDB(
    @PrimaryKey val id: Int,
    val timeWork: Long,
    val timeShortBreak: Long,
    val timeLongBreak: Long,
    val numberOfRepeats: Int
)
