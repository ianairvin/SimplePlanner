package ru.simpleplanner.domain.use_case.event_uc

import ru.simpleplanner.domain.entities.Event
import ru.simpleplanner.domain.repository.EventRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

class GetOneEventUseCase @Inject constructor(
    private val eventRepository : EventRepository,
) {
    operator fun invoke(id: String, calendarId: String, startDay: LocalDateTime, endDay: LocalDateTime
    ) : Event {
        return eventRepository.getOneEvent(id, calendarId, startDay, endDay)
    }
}