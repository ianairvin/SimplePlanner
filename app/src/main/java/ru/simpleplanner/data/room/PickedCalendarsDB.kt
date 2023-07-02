package ru.simpleplanner.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "picked_calendar")
data class PickedCalendarsDB(
    @PrimaryKey val id: String,
)
