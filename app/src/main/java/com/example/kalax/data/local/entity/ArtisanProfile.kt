package com.example.kalax.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artisan_profile")
data class ArtisanProfile(
    @PrimaryKey
    val id: String = "default_artisan",
    val name: String,
    val craftType: String, // e.g., Terracotta, Handloom
    val baseHourlyLaborRate: Double,
    val standardPackagingCost: Double
)
