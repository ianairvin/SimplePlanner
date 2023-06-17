package ru.simpleplanner.domain.use_case.calendar_uc

import android.util.Log
import ru.simpleplanner.domain.entities.Calendar
import ru.simpleplanner.domain.repository.CalendarRepository
import javax.inject.Inject

class GetCalendarsUseCase @Inject constructor(
    private val calendarRepository : CalendarRepository
    )
{
    operator fun invoke(permissionsGranted: Boolean) : List<Calendar> {
        return calendarRepository.getCalendars(permissionsGranted)
    }
}