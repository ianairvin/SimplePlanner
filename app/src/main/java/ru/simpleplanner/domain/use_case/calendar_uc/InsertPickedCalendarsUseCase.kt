package ru.simpleplanner.domain.use_case.calendar_uc

import ru.simpleplanner.domain.repository.CalendarRepository
import javax.inject.Inject

class InsertPickedCalendarsUseCase  @Inject constructor(
    private val calendarRepository : CalendarRepository
) {
    suspend operator fun invoke(selectedCalendarsId: List<String>) {
        return calendarRepository.insertPickedCalendars(selectedCalendarsId)
    }
}