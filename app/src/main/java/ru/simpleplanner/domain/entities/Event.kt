package ru.simpleplanner.domain.entities

import java.time.LocalTime

data class Event(
    var calendarId: String,
    var title: String,
    var location: String?,
    var start: LocalTime,
    var end: LocalTime,
    var allDay: Int,
    var repeatRule: String?,
    var color: String?,
    var description: String?
)
