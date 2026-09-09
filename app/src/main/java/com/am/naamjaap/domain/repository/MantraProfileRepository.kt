package com.am.naamjaap.domain.repository

import com.am.naamjaap.domain.model.MantraProfile
import kotlinx.coroutines.flow.Flow

interface MantraProfileRepository {
    fun getAllProfiles(): Flow<List<MantraProfile>>
    suspend fun getProfileById(id: Long): MantraProfile?
    suspend fun getDefaultProfile(): MantraProfile?
    suspend fun insertProfile(profile: MantraProfile): Long
    suspend fun updateProfile(profile: MantraProfile)
    suspend fun deleteProfile(profile: MantraProfile)
    suspend fun incrementLifetimeCount(id: Long, increment: Int)
}