package com.am.naamjaap.data.repository

import com.am.naamjaap.data.local.dao.MantraProfileDao
import com.am.naamjaap.data.local.entity.MantraProfileEntity
import com.am.naamjaap.domain.model.MantraProfile
import com.am.naamjaap.domain.repository.MantraProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MantraProfileRepositoryImpl @Inject constructor(
    private val dao: MantraProfileDao
) : MantraProfileRepository {

    override fun getAllProfiles(): Flow<List<MantraProfile>> {
        return dao.getAllProfiles().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getProfileById(id: Long): MantraProfile? {
        return dao.getProfileById(id)?.toDomain()
    }

    override suspend fun getDefaultProfile(): MantraProfile? {
        return dao.getDefaultProfile()?.toDomain()
    }

    override suspend fun insertProfile(profile: MantraProfile): Long {
        return dao.insertProfile(profile.toEntity())
    }

    override suspend fun updateProfile(profile: MantraProfile) {
        dao.updateProfile(profile.toEntity())
    }

    override suspend fun deleteProfile(profile: MantraProfile) {
        dao.deleteProfile(profile.toEntity())
    }

    override suspend fun incrementLifetimeCount(id: Long, increment: Int) {
        dao.incrementLifetimeCount(id, increment)
    }
}

// ---------- Mappers ----------
private fun MantraProfileEntity.toDomain() = MantraProfile(
    id = id,
    name = name,
    malaTarget = malaTarget,
    deityImageUri = deityImageUri,
    accentColorHex = accentColorHex,
    lifetimeCount = lifetimeCount,
    createdAt = createdAt,
    isDefault = isDefault
)

private fun MantraProfile.toEntity() = MantraProfileEntity(
    id = id,
    name = name,
    malaTarget = malaTarget,
    deityImageUri = deityImageUri,
    accentColorHex = accentColorHex,
    createdAt = createdAt,
    isDefault = isDefault
)