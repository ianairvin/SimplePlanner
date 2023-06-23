package ru.simpleplanner.data.repository

import android.app.Application
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.provider.CalendarContract
import android.util.Log
import androidx.core.database.getStringOrNull
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
                CalendarContract.Instances.CALENDAR_ID,
                CalendarContract.Instances.CALENDAR_DISPLAY_NAME,
                CalendarContract.Instances.TITLE,
                CalendarContract.Instances.EVENT_LOCATION,
                CalendarContract.Instances.BEGIN,
                CalendarContract.Instances.END,
                CalendarContract.Instances.ALL_DAY,
                CalendarContract.Instances.RRULE,
                CalendarContract.Instances.DESCRIPTION,
                CalendarContract.Instances.EVENT_TIMEZONE
            )
            val PROJECTION_ID_INDEX = 0
            val PROJECTION_CALENDAR_NAME_INDEX = 1
            val PROJECTION_TITLE_INDEX = 2
            val PROJECTION_EVENT_LOCATION_INDEX = 3
            val PROJECTION_BEGIN_INDEX = 4
            val PROJECTION_END_INDEX = 5
            val PROJECTION_ALL_DAY_INDEX = 6
            val PROJECTION_RRULE_INDEX = 7
            val PROJECTION_DESCRIPTION_INDEX = 8
            val PROJECTION_TIME_ZONE_INDEX = 9

            val selection1 = "${CalendarContract.Instances.BEGIN} >= $startDay" +
                    " AND ${CalendarContract.Instances.BEGIN} <= $endDay" +
                    " AND ("

            var selection2 = ""
            val size = calendarsId.size
            for (i in 1..size - 1) {
                selection2 += "${CalendarContract.Instances.CALENDAR_ID} = ? OR "
            }
            selection2 += "${CalendarContract.Instances.CALENDAR_ID} = ?)"
            val selectionArgs = calendarsId.toTypedArray()

            val eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
                .buildUpon()
            ContentUris.appendId(eventsUriBuilder, startDay)
            ContentUris.appendId(eventsUriBuilder, endDay)
            val eventsUri = eventsUriBuilder.build()

            val cursor = appContext.contentResolver.query(
                eventsUri,
                projection,
                selection1 + selection2,
                selectionArgs,
                "${CalendarContract.Instances.DTSTART} " + "ASC"
            )
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val event = Event(
                        cursor.getString(PROJECTION_ID_INDEX),
                        cursor.getString(PROJECTION_CALENDAR_NAME_INDEX),
                        cursor.getString(PROJECTION_TITLE_INDEX) ?: "No title",
                        cursor.getString(PROJECTION_EVENT_LOCATION_INDEX),
                        cursor.getString(PROJECTION_BEGIN_INDEX).toLong(),
                        cursor.getString(PROJECTION_END_INDEX).toLong(),
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

    override fun insertEvent(event: Event) {
        if (event.repeatRule != "") {
            val rRuleAttribute = event.repeatRule?.split("/")
            val rRule = "FREQ=" + rRuleAttribute!![0] + ";INTERVAL=" + rRuleAttribute[1]
            val duration = "P" + ((event.end - event.start) / 1000).toString() + "S"
            val values = ContentValues().apply {
                put(CalendarContract.Events.CALENDAR_ID, event.calendarId)
                put(CalendarContract.Events.TITLE, event.title)
                put(CalendarContract.Events.EVENT_LOCATION, event.location)
                put(CalendarContract.Events.DTSTART, event.start)
                put(CalendarContract.Events.DURATION, duration)
                put(CalendarContract.Events.ALL_DAY, event.allDay)
                put(CalendarContract.Events.RRULE, rRule)
                put(CalendarContract.Events.DESCRIPTION, event.description)
                put(CalendarContract.Events.EVENT_TIMEZONE, event.timeZone)
            }
            appContext.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        } else {
            val values = ContentValues().apply {
                put(CalendarContract.Events.CALENDAR_ID, event.calendarId)
                put(CalendarContract.Events.TITLE, event.title)
                put(CalendarContract.Events.EVENT_LOCATION, event.location)
                put(CalendarContract.Events.DTSTART, event.start)
                put(CalendarContract.Events.DTEND, event.end)
                put(CalendarContract.Events.ALL_DAY, event.allDay)
                put(CalendarContract.Events.RRULE, "")
                put(CalendarContract.Events.DESCRIPTION, event.description)
                put(CalendarContract.Events.EVENT_TIMEZONE, event.timeZone)
            }
            appContext.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        }
    }
}
