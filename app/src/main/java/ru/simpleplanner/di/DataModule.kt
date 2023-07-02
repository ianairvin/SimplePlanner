package ru.simpleplanner.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.simpleplanner.data.repository.CalendarRepositoryImpl
import ru.simpleplanner.data.repository.EventRepositoryImpl
import ru.simpleplanner.data.repository.TaskRepositoryImpl
import ru.simpleplanner.data.repository.TimerRepositoryImpl
import ru.simpleplanner.data.room.Dao
import ru.simpleplanner.data.room.DataBase
import ru.simpleplanner.data.room.InitializeDB
import ru.simpleplanner.domain.repository.CalendarRepository
import ru.simpleplanner.domain.repository.EventRepository
import ru.simpleplanner.domain.repository.TaskRepository
import ru.simpleplanner.domain.repository.TimerRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideDataBase(app: Application): DataBase {
        return Room.databaseBuilder(
            app,
            DataBase::class.java,
            DataBase.DB_NAME
        ).addCallback(InitializeDB()).build()
    }

    @Provides
    @Singleton
    fun provideCalendarRepository(db: DataBase, app: Application) : CalendarRepository {
        return CalendarRepositoryImpl(dao = db.dao, appContext = app)
    }

    @Provides
    @Singleton
    fun provideEventRepository(app: Application) : EventRepository {
        return EventRepositoryImpl(appContext = app)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(db: DataBase) : TaskRepository {
        return TaskRepositoryImpl(db.dao)
    }

    @Provides
    @Singleton
    fun provideTimerRepository(db: DataBase) : TimerRepository {
        return TimerRepositoryImpl(db.dao)
    }
}