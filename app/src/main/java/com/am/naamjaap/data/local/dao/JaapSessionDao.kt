package com.am.naamjaap.data.local.dao


import androidx.room.*
import com.am.naamjaap.data.local.entity.JapaSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JapaSessionDao {

    @Insert
    suspend fun insertSession(session: JapaSessionEntity): Long

    @Query("SELECT * FROM japa_sessions WHERE mantraProfileId = :profileId ORDER BY startedAt DESC")
    fun getSessionsForProfile(profileId: Long): Flow<List<JapaSessionEntity>>

    @Query("SELECT * FROM japa_sessions WHERE dateEpochDay = :epochDay")
    fun getSessionsForDate(epochDay: Long): Flow<List<JapaSessionEntity>>

    @Query("SELECT COALESCE(SUM(count), 0) FROM japa_sessions WHERE dateEpochDay BETWEEN :startEpochDay AND :endEpochDay")
    suspend fun getTotalCountInRange(startEpochDay: Long, endEpochDay: Long): Int

    @Query("SELECT DISTINCT dateEpochDay FROM japa_sessions ORDER BY dateEpochDay DESC")
    suspend fun getAllSessionDates(): List<Long>
}