package ru.simpleplanner.domain.entities


data class Event(
    var calendarId: String,
    var name: String,
    var location: String,
    var description: String,
    var color: String,
    var start: String,
    var end: String,
    var repeat: String,
    var allDay: Boolean
)
