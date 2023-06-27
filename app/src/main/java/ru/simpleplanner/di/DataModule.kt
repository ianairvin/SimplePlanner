package ru.simpleplanner.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.simpleplanner.data.repository.CalendarRepositoryImpl
import ru.simpleplanner.data.repository.EventRepositoryImpl
import ru.simpleplanner.data.repository.TaskRepositoryImpl
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
        return CalendarRepositoryImpl(app = app)
    }

    @Provides
    @Singleton
    fun provideEventRepository(app: Application) : EventRepository {
        return EventRepositoryImpl(app = app)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(app: Application) : TaskRepository {
        return TaskRepositoryImpl(app = app)
    }
}