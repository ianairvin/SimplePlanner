package ru.simpleplanner.domain.use_case.calendar_uc

import ru.simpleplanner.domain.entities.Calendar
import ru.simpleplanner.domain.repository.CalendarRepository
import javax.inject.Inject

class GetCalendarsUseCase @Inject constructor(
    private val calendarRepository : CalendarRepository
    )
{
    suspend operator fun invoke() : List<Calendar> {
        return calendarRepository.getCalendars()
    }
}