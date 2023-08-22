package ru.simpleplanner.data.room

import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

class InitializeDB : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        db.execSQL("INSERT INTO timer (id, timeWork, timeShortBreak, timeLongBreak, numberOfRepeats) " +
                "values(1, 1500000, 300000, 900000, 0)")
        db.execSQL("INSERT INTO picked_calendar (id) " +
                "values(0)")
        db.execSQL("INSERT INTO open_section_task (id, today, tomorrow, week, someDay, doneTask) " +
                "values(1, 0, 0, 0, 0, 0)")
    }
}