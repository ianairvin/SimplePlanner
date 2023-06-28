package ru.simpleplanner.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class TaskDB(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val title: String,
    val check: Boolean,
    var date: Long,
    val dayOfWeek: Int,
    @ColumnInfo(name = "make_date_time") val makeDateTime: Long,
    val repeatRule: String,
    val note: String
)
