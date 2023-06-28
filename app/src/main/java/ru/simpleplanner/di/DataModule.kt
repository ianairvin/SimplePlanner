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
import ru.simpleplanner.data.room.DataBase
import ru.simpleplanner.domain.repository.CalendarRepository
import ru.simpleplanner.domain.repository.EventRepository
import ru.simpleplanner.domain.repository.TaskRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Provides
    @Singleton
    fun provideCalendarRepository(app: Application) : CalendarRepository {
        return CalendarRepositoryImpl(appContext = app)
    }

    @Provides
    @Singleton
    fun provideEventRepository(app: Application) : EventRepository {
        return EventRepositoryImpl(appContext = app)
    }

    @Provides
    @Singleton
    fun provideDataBase(app: Application): DataBase {
        return Room.databaseBuilder(
            app,
            DataBase::class.java,
            DataBase.DB_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideTaskRepository(app: Application, db: DataBase) : TaskRepository {
        return TaskRepositoryImpl(db.dao)
    }
}