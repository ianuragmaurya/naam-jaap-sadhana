package com.am.naamjaap.data.repository

import com.am.naamjaap.data.local.dao.JapaSessionDao
import com.am.naamjaap.data.local.entity.JapaSessionEntity
import com.am.naamjaap.domain.model.JapaSession
import com.am.naamjaap.domain.repository.JapaSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class JapaSessionRepositoryImpl @Inject constructor(
    private val dao: JapaSessionDao
) : JapaSessionRepository {

    override suspend fun insertSession(session: JapaSession): Long {
        return dao.insertSession(session.toEntity())
    }

    override fun getSessionsForProfile(profileId: Long): Flow<List<JapaSession>> {
        return dao.getSessionsForProfile(profileId).map { list -> list.map { it.toDomain() } }
    }

    override fun getSessionsForDate(epochDay: Long): Flow<List<JapaSession>> {
        return dao.getSessionsForDate(epochDay).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getTotalCountInRange(startEpochDay: Long, endEpochDay: Long): Int {
        return dao.getTotalCountInRange(startEpochDay, endEpochDay)
    }

    override suspend fun getAllSessionDates(): List<Long> {
        return dao.getAllSessionDates()
    }
}

// ---------- Mappers ----------
private fun JapaSessionEntity.toDomain() = JapaSession(
    id = id,
    mantraProfileId = mantraProfileId,
    count = count,
    malaCompletions = malaCompletions,
    startedAt = startedAt,
    endedAt = endedAt,
    dateEpochDay = dateEpochDay
)

private fun JapaSession.toEntity() = JapaSessionEntity(
    id = id,
    mantraProfileId = mantraProfileId,
    count = count,
    malaCompletions = malaCompletions,
    startedAt = startedAt,
    endedAt = endedAt,
    dateEpochDay = dateEpochDay
)