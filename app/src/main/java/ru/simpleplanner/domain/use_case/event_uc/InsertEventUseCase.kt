package ru.simpleplanner.domain.use_case.event_uc

import ru.simpleplanner.domain.entities.Event
import ru.simpleplanner.domain.repository.EventRepository
import javax.inject.Inject

class InsertEventUseCase @Inject constructor(
    private val eventRepository : EventRepository,
) {
    operator fun invoke(event: Event) : Boolean {
        return eventRepository.insertEvent(event)
    }
}