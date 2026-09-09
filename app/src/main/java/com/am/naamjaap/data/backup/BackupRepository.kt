package com.am.naamjaap.data.backup

import com.am.naamjaap.data.local.dao.DailyCountDao
import com.am.naamjaap.data.local.dao.JapaSessionDao
import com.am.naamjaap.data.local.dao.MantraProfileDao
import com.am.naamjaap.data.local.entity.DailyCountEntity
import com.am.naamjaap.data.local.entity.JapaSessionEntity
import com.am.naamjaap.data.local.entity.MantraProfileEntity
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class BackupRepository @Inject constructor(
    private val mantraProfileDao: MantraProfileDao,
    private val japaSessionDao: JapaSessionDao,
    private val dailyCountDao: DailyCountDao
) {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    suspend fun exportToJson(): String {
        val profiles = mantraProfileDao.getAllProfiles().first()
        val allSessionDates = japaSessionDao.getAllSessionDates()

        val sessions = mutableListOf<BackupJapaSession>()
        for (profile in profiles) {
            japaSessionDao.getSessionsForProfile(profile.id).first().forEach { session ->
                sessions.add(
                    BackupJapaSession(
                        mantraProfileId = session.mantraProfileId,
                        count = session.count,
                        malaCompletions = session.malaCompletions,
                        startedAt = session.startedAt,
                        endedAt = session.endedAt,
                        dateEpochDay = session.dateEpochDay
                    )
                )
            }
        }

        val dailyCounts = mutableListOf<BackupDailyCount>()
        if (allSessionDates.isNotEmpty()) {
            val minDay = allSessionDates.min()
            val maxDay = allSessionDates.max()
            for (profile in profiles) {
                dailyCountDao.getRangeForAllProfiles(minDay, maxDay).first().forEach { daily ->
                    if (daily.mantraProfileId == profile.id) {
                        dailyCounts.add(
                            BackupDailyCount(
                                dateEpochDay = daily.dateEpochDay,
                                mantraProfileId = daily.mantraProfileId,
                                totalCount = daily.totalCount,
                                malaCompletions = daily.malaCompletions
                            )
                        )
                    }
                }
            }
        }

        val backup = BackupData(
            exportedAt = System.currentTimeMillis(),
            profiles = profiles.map {
                BackupMantraProfile(it.id, it.name, it.malaTarget, it.lifetimeCount, it.createdAt, it.isDefault)
            },
            sessions = sessions.distinct(),
            dailyCounts = dailyCounts.distinct()
        )

        return json.encodeToString(backup)
    }

    suspend fun importFromJson(jsonString: String) {
        val backup = json.decodeFromString<BackupData>(jsonString)

        // Map old profile IDs -> newly inserted IDs (IDs can't be reused safely
        // across devices since AUTOINCREMENT may already have existing rows).
        val idMap = mutableMapOf<Long, Long>()

        backup.profiles.forEach { profile ->
            val newId = mantraProfileDao.insertProfile(
                MantraProfileEntity(
                    name = profile.name,
                    malaTarget = profile.malaTarget,
                    lifetimeCount = profile.lifetimeCount,
                    createdAt = profile.createdAt,
                    isDefault = profile.isDefault
                )
            )
            idMap[profile.id] = newId
        }

        backup.sessions.forEach { session ->
            val newProfileId = idMap[session.mantraProfileId] ?: return@forEach
            japaSessionDao.insertSession(
                JapaSessionEntity(
                    mantraProfileId = newProfileId,
                    count = session.count,
                    malaCompletions = session.malaCompletions,
                    startedAt = session.startedAt,
                    endedAt = session.endedAt,
                    dateEpochDay = session.dateEpochDay
                )
            )
        }

        backup.dailyCounts.forEach { daily ->
            val newProfileId = idMap[daily.mantraProfileId] ?: return@forEach
            dailyCountDao.upsert(
                DailyCountEntity(
                    dateEpochDay = daily.dateEpochDay,
                    mantraProfileId = newProfileId,
                    totalCount = daily.totalCount,
                    malaCompletions = daily.malaCompletions
                )
            )
        }
    }
}