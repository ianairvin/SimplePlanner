package ru.simpleplanner.data.repository

import android.app.Application
import android.database.Cursor
import android.provider.CalendarContract
import ru.simpleplanner.data.room.Dao
import ru.simpleplanner.data.room.PickedCalendarsDB
import ru.simpleplanner.domain.entities.Calendar
import ru.simpleplanner.domain.repository.CalendarRepository
import javax.inject.Inject

class CalendarRepositoryImpl @Inject constructor (
    private val dao: Dao,
    private val appContext: Application
): CalendarRepository{
    override suspend fun getCalendars() : List<Calendar> {
        val calendars = arrayListOf<Calendar>()
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.CALENDAR_COLOR,
            CalendarContract.Calendars.VISIBLE,
            CalendarContract.Calendars.SYNC_EVENTS
        )

        val PROJECTION_ID_INDEX: Int = 0
        val PROJECTION_CALENDAR_DISPLAY_NAME_INDEX: Int = 1
        val PROJECTION_CALENDAR_COLOR_INDEX: Int = 2
        val PROJECTION_VISIBLE_INDEX: Int = 3
        val PROJECTION_SYNC_EVENTS_INDEX: Int = 4

        val selection = "${CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL} " +
                "= ${CalendarContract.Calendars.CAL_ACCESS_OWNER}"
        val cursor: Cursor? = appContext.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            selection,
            null,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " ASC"
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val calendar = Calendar(
                    cursor.getString(PROJECTION_ID_INDEX),
                    cursor.getString(PROJECTION_CALENDAR_DISPLAY_NAME_INDEX),
                    cursor.getString(PROJECTION_CALENDAR_COLOR_INDEX),
                    cursor.getString(PROJECTION_VISIBLE_INDEX).toInt(),
                    cursor.getString(PROJECTION_SYNC_EVENTS_INDEX)
                )
                if(calendar.visible == 1) {
                    calendars.add(calendar)
                }
            }
        } else {
            return emptyList()
        }
        cursor.close()
        return calendars
    }

    override suspend fun getPickedCalendars(): List<String> {
        val calendarsDB = dao.getPickedCalendars()
        val calendarsId = mutableListOf<String>()
        calendarsDB.forEach {
            calendarsId.add(it.id)
        }
        return calendarsId
    }

    override suspend fun insertPickedCalendars(selectedCalendarsId: List<String>) {
        dao.deleteAllPickedCalendarsId()
        selectedCalendarsId.forEach{
            val calendar = PickedCalendarsDB(
                it
            )
            dao.insertPickedCalendarsId(calendar)
        }
    }
}