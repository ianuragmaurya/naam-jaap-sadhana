package com.am.naamjaap.data.local.dao


import androidx.room.*
import com.am.naamjaap.data.local.entity.MantraProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MantraProfileDao {

    @Query("SELECT * FROM mantra_profiles ORDER BY createdAt ASC")
    fun getAllProfiles(): Flow<List<MantraProfileEntity>>

    @Query("SELECT * FROM mantra_profiles WHERE id = :id")
    suspend fun getProfileById(id: Long): MantraProfileEntity?

    @Query("SELECT * FROM mantra_profiles WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultProfile(): MantraProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: MantraProfileEntity): Long

    @Update
    suspend fun updateProfile(profile: MantraProfileEntity): Int

    @Delete
    suspend fun deleteProfile(profile: MantraProfileEntity): Int

    @Query("UPDATE mantra_profiles SET lifetimeCount = lifetimeCount + :increment WHERE id = :id")
    suspend fun incrementLifetimeCount(id: Long, increment: Int): Int
}