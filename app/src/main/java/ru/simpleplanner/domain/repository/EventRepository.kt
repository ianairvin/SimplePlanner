package ru.simpleplanner.domain.repository

import ru.simpleplanner.domain.entities.Event
import java.time.LocalDate
import java.time.LocalDateTime

interface EventRepository {
    suspend fun deleteEvent(id: String)
    suspend fun getEvents(date: LocalDate, calendarsId: List<String>) : List<Event>
    suspend fun getOneEvent(id: String, calendarId: String, startDay: LocalDateTime, endDay: LocalDateTime
    ) : Event
    suspend fun insertEvent(event: Event)
    suspend fun updateEvent(event: Event)
}