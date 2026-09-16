package com.example.kalax.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "catalog_item")
data class CatalogItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String,
    val description: String,
    val tags: String, // Comma separated for simplicity, or TypeConverter can be used
    val rawPhotoUri: String?,
    val processedPhotoUri: String?,
    val materialCost: Double,
    val labourCost: Double,
    val packagingCost: Double,
    val suggestedPrice: Double,
    val readinessScore: Int
)
