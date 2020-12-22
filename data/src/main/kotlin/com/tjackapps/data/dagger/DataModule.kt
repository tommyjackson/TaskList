package com.tjackapps.data.dagger

import android.content.Context
import androidx.room.Room
import com.tjackapps.data.manager.DatabaseManager
import com.tjackapps.data.room.AppDatabase
import com.tjackapps.data.room.GroupDao
import com.tjackapps.data.room.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object DataModule {

    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext context: Context) : AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun providesGroupDao(appDatabase: AppDatabase) : GroupDao {
        return appDatabase.groupDao()
    }

    @Provides
    @Singleton
    fun providesTaskDao(appDatabase: AppDatabase) : TaskDao {
        return appDatabase.taskDao()
    }

    @Provides
    @Singleton
    fun providesDatabaseManager(
        groupDao: GroupDao,
        taskDao: TaskDao
    ) : DatabaseManager {
        return DatabaseManager(groupDao, taskDao)
    }
}