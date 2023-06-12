package ru.simpleplanner.domain.repository

import ru.simpleplanner.domain.entities.Calendar

interface CalendarRepository {
    fun getCalendars(permissionsGranted: Boolean) : List<Calendar>
}