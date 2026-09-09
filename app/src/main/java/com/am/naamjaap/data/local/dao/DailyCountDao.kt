package com.am.naamjaap.data.local.dao


import androidx.room.*
import com.am.naamjaap.data.local.entity.DailyCountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyCountDao {

    @Query("SELECT * FROM daily_counts WHERE dateEpochDay = :epochDay AND mantraProfileId = :profileId")
    suspend fun getForDateAndProfile(epochDay: Long, profileId: Long): DailyCountEntity?

    @Query("SELECT * FROM daily_counts WHERE mantraProfileId = :profileId ORDER BY dateEpochDay DESC")
    fun getAllForProfile(profileId: Long): Flow<List<DailyCountEntity>>

    @Query("SELECT * FROM daily_counts WHERE dateEpochDay BETWEEN :startEpochDay AND :endEpochDay ORDER BY dateEpochDay ASC")
    fun getRangeForAllProfiles(startEpochDay: Long, endEpochDay: Long): Flow<List<DailyCountEntity>>

    @Query("DELETE FROM daily_counts WHERE mantraProfileId = :profileId")
    suspend fun deleteForProfile(profileId: Long)


    @Query("SELECT DISTINCT dateEpochDay FROM daily_counts WHERE totalCount > 0 ORDER BY dateEpochDay DESC")
    suspend fun getAllActiveDates(): List<Long>

    @Upsert
    suspend fun upsert(dailyCount: DailyCountEntity): Long
}