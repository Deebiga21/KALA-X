package com.example.kalax.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "catalog_item")
data class CatalogItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val artisanId: String = "default_artisan",
    val title: String,
    val category: String,
    val description: String,
    val tags: String, // Comma separated for simplicity, or TypeConverter can be used
    val material: String = "",
    val craftType: String = "",
    val productStory: String = "",
    val keywords: String = "",
    val rawPhotoUri: String?,
    val processedPhotoUri: String?,
    val rawAudioTranscript: String = "",
    val translatedText: String = "",
    val materialCost: Double,
    val labourCost: Double,
    val packagingCost: Double,
    val otherCost: Double = 0.0,
    val suggestedPrice: Double,
    val marketMin: Double = 0.0,
    val marketMax: Double = 0.0,
    val recommendedPrice: Double = 0.0,
    val readinessScore: Int,
    val dimensions: String = "",
    val transcription: String = "",
    val status: String = "Draft",
    val createdAt: Long = System.currentTimeMillis()
)
