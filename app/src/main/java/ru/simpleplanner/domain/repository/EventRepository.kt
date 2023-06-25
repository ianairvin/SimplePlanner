package ru.simpleplanner.domain.repository

import ru.simpleplanner.domain.entities.Event
import java.time.LocalDate
import java.time.LocalDateTime

interface EventRepository {
    fun deleteEvent(id: String)
    fun getEvents(date: LocalDate, calendarsId: ArrayList<String>) : List<Event>
    fun getOneEvent(id: String, calendarId: String, startDay: LocalDateTime, endDay: LocalDateTime
    ) : Event
    fun insertEvent(event: Event)
    fun updateEvent(event: Event)
}