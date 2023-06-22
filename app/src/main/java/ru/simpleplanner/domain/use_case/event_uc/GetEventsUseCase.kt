package ru.simpleplanner.domain.use_case.event_uc

import ru.simpleplanner.domain.entities.Event
import ru.simpleplanner.domain.repository.EventRepository
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject

class GetEventsUseCase @Inject constructor(
        private val eventRepository : EventRepository,
    ) {
    operator fun invoke(date: LocalDate, calendarsId: ArrayList<String>) : List<Event> {
        return eventRepository.getEvents(date, calendarsId)
    }
}