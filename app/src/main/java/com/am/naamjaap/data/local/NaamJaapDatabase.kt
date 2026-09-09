package com.am.naamjaap.data.local


import androidx.room.Database
import androidx.room.RoomDatabase
import com.am.naamjaap.data.local.dao.DailyCountDao
import com.am.naamjaap.data.local.dao.JapaSessionDao
import com.am.naamjaap.data.local.dao.MantraProfileDao
import com.am.naamjaap.data.local.entity.DailyCountEntity
import com.am.naamjaap.data.local.entity.JapaSessionEntity
import com.am.naamjaap.data.local.entity.MantraProfileEntity

@Database(
    entities = [
        MantraProfileEntity::class,
        JapaSessionEntity::class,
        DailyCountEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class NaamJaapDatabase : RoomDatabase() {
    abstract fun mantraProfileDao(): MantraProfileDao
    abstract fun japaSessionDao(): JapaSessionDao
    abstract fun dailyCountDao(): DailyCountDao
}