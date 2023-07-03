package ru.simpleplanner.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class TaskDB(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val title: String,
    val check: Boolean,
    var date: Long?,
    @ColumnInfo(name = "make_date_time") val makeDateTime: Long,
    val note: String,
    val priority: Int
)
