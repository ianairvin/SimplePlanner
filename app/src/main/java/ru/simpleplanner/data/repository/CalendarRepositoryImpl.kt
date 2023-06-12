package ru.simpleplanner.data.repository

import android.app.Application
import android.database.Cursor
import android.provider.CalendarContract
import android.util.Log
import ru.simpleplanner.domain.entities.Calendar
import ru.simpleplanner.domain.repository.CalendarRepository
import javax.inject.Inject

class CalendarRepositoryImpl @Inject constructor (
    app: Application
): CalendarRepository{

    val appContext = app
    override fun getCalendars(permissionsGranted: Boolean) : ArrayList<Calendar>{
        val calendars = arrayListOf<Calendar>()
        if (permissionsGranted) {
            val projection = arrayOf(
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.CALENDAR_COLOR,
                CalendarContract.Calendars.VISIBLE,
                CalendarContract.Calendars.SYNC_EVENTS,
                CalendarContract.Calendars.CALENDAR_TIME_ZONE
            )

            val PROJECTION_ID_INDEX: Int = 0
            val PROJECTION_CALENDAR_DISPLAY_NAME_INDEX: Int = 1
            val PROJECTION_CALENDAR_COLOR_INDEX: Int = 2
            val PROJECTION_VISIBLE_INDEX: Int = 3
            val PROJECTION_SYNC_EVENTS_INDEX: Int = 4
            val PROJECTION_CALENDAR_TIME_ZONE_INDEX: Int = 4

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
                        cursor.getString(PROJECTION_VISIBLE_INDEX),
                        cursor.getString(PROJECTION_SYNC_EVENTS_INDEX),
                        cursor.getString(PROJECTION_CALENDAR_TIME_ZONE_INDEX)
                    )
                    calendars.add(calendar)
                }
            }
            cursor?.close()
        }
        return calendars
    }
}