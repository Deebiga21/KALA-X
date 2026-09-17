package com.example.kalax.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artisan_correction")
data class ArtisanCorrection(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val catalogItemId: Long,
    val originalText: String,
    val correctedText: String,
    val originalPrice: Double,
    val correctedPrice: Double,
    val correctionType: String, // e.g., "TEXT", "PRICE"
    val isSynced: Boolean = false
)
