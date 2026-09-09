package com.am.naamjaap.data.repository

import com.am.naamjaap.data.local.dao.DailyCountDao
import com.am.naamjaap.data.local.entity.DailyCountEntity
import com.am.naamjaap.domain.model.DailyStat
import com.am.naamjaap.domain.repository.DailyStatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DailyStatRepositoryImpl @Inject constructor(
    private val dao: DailyCountDao
) : DailyStatRepository {

    override suspend fun getForDateAndProfile(epochDay: Long, profileId: Long): DailyStat? {
        return dao.getForDateAndProfile(epochDay, profileId)?.toDomain()
    }

    override fun getAllForProfile(profileId: Long): Flow<List<DailyStat>> {
        return dao.getAllForProfile(profileId).map { list -> list.map { it.toDomain() } }
    }

    override fun getRangeForAllProfiles(startEpochDay: Long, endEpochDay: Long): Flow<List<DailyStat>> {
        return dao.getRangeForAllProfiles(startEpochDay, endEpochDay).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun upsert(dailyStat: DailyStat) {
        dao.upsert(dailyStat.toEntity())
    }

    override suspend fun getAllActiveDates(): List<Long> = dao.getAllActiveDates()
}

// ---------- Mappers ----------
private fun DailyCountEntity.toDomain() = DailyStat(
    dateEpochDay = dateEpochDay,
    mantraProfileId = mantraProfileId,
    totalCount = totalCount,
    malaCompletions = malaCompletions
)

private fun DailyStat.toEntity() = DailyCountEntity(
    dateEpochDay = dateEpochDay,
    mantraProfileId = mantraProfileId,
    totalCount = totalCount,
    malaCompletions = malaCompletions
)