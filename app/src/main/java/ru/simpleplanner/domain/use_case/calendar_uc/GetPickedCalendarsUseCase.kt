package ru.simpleplanner.domain.use_case.calendar_uc

import ru.simpleplanner.domain.entities.Calendar
import ru.simpleplanner.domain.repository.CalendarRepository
import javax.inject.Inject

class GetPickedCalendarsUseCase @Inject constructor(
    private val calendarRepository : CalendarRepository
) {
    suspend operator fun invoke(): List<String> {
        return calendarRepository.getPickedCalendars()
    }
}