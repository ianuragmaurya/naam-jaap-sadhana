package com.am.naamjaap.domain.repository

import com.am.naamjaap.domain.model.DailyStat
import kotlinx.coroutines.flow.Flow

interface DailyStatRepository {
    suspend fun getForDateAndProfile(epochDay: Long, profileId: Long): DailyStat?
    fun getAllForProfile(profileId: Long): Flow<List<DailyStat>>
    fun getRangeForAllProfiles(startEpochDay: Long, endEpochDay: Long): Flow<List<DailyStat>>
    suspend fun getAllActiveDates(): List<Long>
    suspend fun upsert(dailyStat: DailyStat)
}