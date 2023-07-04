package ru.simpleplanner.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "open_section_task")
data class OpenSectionTaskDB (
        @PrimaryKey val id: Int,
        val today: Boolean,
        val tomorrow: Boolean,
        val week: Boolean,
        val someDay: Boolean,
        val doneTask: Boolean
)