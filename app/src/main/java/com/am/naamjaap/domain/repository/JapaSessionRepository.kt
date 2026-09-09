package com.am.naamjaap.domain.repository

import com.am.naamjaap.domain.model.JapaSession
import kotlinx.coroutines.flow.Flow

interface JapaSessionRepository {
    suspend fun insertSession(session: JapaSession): Long
    fun getSessionsForProfile(profileId: Long): Flow<List<JapaSession>>
    fun getSessionsForDate(epochDay: Long): Flow<List<JapaSession>>
    suspend fun getTotalCountInRange(startEpochDay: Long, endEpochDay: Long): Int
    suspend fun getAllSessionDates(): List<Long>
}