package ru.simpleplanner.domain.use_case.event_uc

import ru.simpleplanner.domain.repository.EventRepository
import javax.inject.Inject

class DeleteEventUseCase @Inject constructor(
    private val eventRepository : EventRepository,
) {
    operator fun invoke(id: String) {
        return eventRepository.deleteEvent(id)
    }
}