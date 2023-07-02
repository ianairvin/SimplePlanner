package ru.simpleplanner.domain.repository

import ru.simpleplanner.domain.entities.Calendar

interface CalendarRepository {
    suspend fun getCalendars() : List<Calendar>
    suspend fun getPickedCalendars() : List<String>

    suspend fun insertPickedCalendars(selectedCalendarsId: List<String>)
}