package ru.simpleplanner.data.repository

import android.app.Application
import android.content.ContentValues
import android.database.Cursor
import android.provider.CalendarContract
import android.util.Log
import ru.simpleplanner.domain.entities.Calendar
import ru.simpleplanner.domain.entities.Event
import ru.simpleplanner.domain.repository.CalendarRepository
import ru.simpleplanner.domain.repository.EventRepository
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.TimeZone
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor (
    app: Application
): EventRepository {

    val appContext = app

    override fun getEvents(date: LocalDate, calendarsId: ArrayList<String>): List<Event> {
        if(calendarsId.isNotEmpty()) {
            val events = arrayListOf<Event>()
            val startDay = date.atStartOfDay(ZoneOffset.systemDefault()).toInstant().toEpochMilli()
            val endDay = date.atStartOfDay(ZoneOffset.systemDefault()).plusDays(1)
                .minusSeconds(1).toInstant().toEpochMilli()

            val projection = arrayOf(
                CalendarContract.Events.CALENDAR_ID,
                CalendarContract.Events.CALENDAR_DISPLAY_NAME,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.EVENT_LOCATION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.ALL_DAY,
                CalendarContract.Events.RRULE,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.EVENT_TIMEZONE
            )
            val PROJECTION_ID_INDEX = 0
            val PROJECTION_CALENDAR_NAME_INDEX = 1
            val PROJECTION_TITLE_INDEX = 2
            val PROJECTION_EVENT_LOCATION_INDEX = 3
            val PROJECTION_DTSTART_INDEX = 4
            val PROJECTION_DTEND_INDEX = 5
            val PROJECTION_ALL_DAY_INDEX = 6
            val PROJECTION_RRULE_INDEX = 7
            val PROJECTION_DESCRIPTION_INDEX = 8
            val PROJECTION_TIME_ZONE_INDEX = 9

            val selection1 = "${CalendarContract.Events.DTSTART} >= $startDay" +
                    " AND ${CalendarContract.Events.DTSTART} <= $endDay" +
                    " AND ("

            var selection2 = ""
            val size = calendarsId.size
            for (i in 1..size - 1) {
                selection2 += "${CalendarContract.Events.CALENDAR_ID} = ? OR "
            }
            selection2 += "${CalendarContract.Events.CALENDAR_ID} = ?)"
            val selectionArgs = calendarsId.toTypedArray()
            val cursor = appContext.contentResolver.query(
                CalendarContract.Events.CONTENT_URI,
                projection,
                selection1 + selection2,
                selectionArgs,
                "${CalendarContract.Events.DTSTART} " + "ASC"
            )
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val event = Event(
                        cursor.getString(PROJECTION_ID_INDEX),
                        cursor.getString(PROJECTION_CALENDAR_NAME_INDEX),
                        cursor.getString(PROJECTION_TITLE_INDEX) ?: "No title",
                        cursor.getString(PROJECTION_EVENT_LOCATION_INDEX),
                        cursor.getString(PROJECTION_DTSTART_INDEX).toLong(),
                        cursor.getString(PROJECTION_DTEND_INDEX).toLong(),
                        cursor.getString(PROJECTION_ALL_DAY_INDEX).toInt(),
                        cursor.getString(PROJECTION_RRULE_INDEX),
                        cursor.getString(PROJECTION_DESCRIPTION_INDEX),
                        cursor.getString(PROJECTION_TIME_ZONE_INDEX)
                    )
                    events.add(event)
                }
            }
            cursor?.close()
            return events
        } else {
            return emptyList()
        }
    }

    override fun insertEvent(event: Event): Boolean {
        val values = ContentValues().apply {
            Log.i("qqqqq", event.calendarId)
            put(CalendarContract.Events.CALENDAR_ID, event.calendarId)
            put(CalendarContract.Events.TITLE, event.title)
            put(CalendarContract.Events.EVENT_LOCATION, event.location)
            put(CalendarContract.Events.DTSTART, event.start)
            put(CalendarContract.Events.DTEND, event.end)
            put(CalendarContract.Events.ALL_DAY, event.allDay)
            put(CalendarContract.Events.RRULE, event.repeatRule)
            put(CalendarContract.Events.DESCRIPTION, event.description)
            put(CalendarContract.Events.EVENT_TIMEZONE, event.timeZone)
        }

        val uri = appContext.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        return uri != null
    }
}
