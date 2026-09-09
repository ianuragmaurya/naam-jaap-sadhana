package com.am.naamjaap.di

import android.content.Context
import androidx.room.Room
import com.am.naamjaap.data.local.NaamJaapDatabase
import com.am.naamjaap.data.local.dao.DailyCountDao
import com.am.naamjaap.data.local.dao.JapaSessionDao
import com.am.naamjaap.data.local.dao.MantraProfileDao
import com.am.naamjaap.data.local.datastore.UserPreferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideNaamJaapDatabase(
        @ApplicationContext context: Context
    ): NaamJaapDatabase {
        return Room.databaseBuilder(
            context,
            NaamJaapDatabase::class.java,
            "naam_jaap_database")
           // .addMigrations(MIGRATION_2_3) jab app me kuchh update karenge releted DB to hum migrate karna hoga
            .build()
    }

    @Provides
    fun provideMantraProfileDao(database: NaamJaapDatabase): MantraProfileDao {
        return database.mantraProfileDao()
    }

    @Provides
    fun provideJapaSessionDao(database: NaamJaapDatabase): JapaSessionDao {
        return database.japaSessionDao()
    }

    @Provides
    fun provideDailyCountDao(database: NaamJaapDatabase): DailyCountDao {
        return database.dailyCountDao()

    }
}