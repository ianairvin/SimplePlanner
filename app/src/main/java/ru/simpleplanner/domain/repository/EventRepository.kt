package ru.simpleplanner.domain.repository

import ru.simpleplanner.domain.entities.Event
import java.time.LocalDate

interface EventRepository {
    fun getEvents(date: LocalDate, calendarsId: ArrayList<String>) : List<Event>
    fun insertEvent(event: Event) : Boolean
}