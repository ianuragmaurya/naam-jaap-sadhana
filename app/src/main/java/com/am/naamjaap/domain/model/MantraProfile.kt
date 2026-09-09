package com.am.naamjaap.domain.model

data class MantraProfile(
    val id: Long = 0,
    val name: String,
    val malaTarget: Int = 108,
    val deityImageUri: String? = null,
    val accentColorHex: String = "#E8A33D",
    val lifetimeCount: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val isDefault: Boolean = false
)