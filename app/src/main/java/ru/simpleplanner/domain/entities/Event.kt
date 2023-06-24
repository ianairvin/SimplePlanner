package ru.simpleplanner.domain.entities

import java.time.LocalDateTime

data class Event(
    var calendarId: String,
    var calendarDisplayName: String,
    var title: String,
    var location: String?,
    var start: LocalDateTime,
    var end: LocalDateTime,
    var allDay: Int,
    var repeatRule: String?,
    var description: String?,
    var timeZone: String,
    var id: String
)
