package com.am.naamjaap.domain.usecase

import com.am.naamjaap.data.backup.BackupRepository
import javax.inject.Inject

class ImportBackupUseCase @Inject constructor(
    private val backupRepository: BackupRepository
) {
    suspend operator fun invoke(jsonString: String) = backupRepository.importFromJson(jsonString)
}