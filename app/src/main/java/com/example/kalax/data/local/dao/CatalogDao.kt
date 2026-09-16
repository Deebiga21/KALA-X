package com.example.kalax.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kalax.data.local.entity.CatalogItem
import com.example.kalax.data.local.entity.ArtisanCorrection
import kotlinx.coroutines.flow.Flow

@Dao
interface CatalogDao {
    @Query("SELECT * FROM catalog_item ORDER BY id DESC")
    fun getAllCatalogItems(): Flow<List<CatalogItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatalogItem(item: CatalogItem): Long
    
    @Insert
    suspend fun insertCorrection(correction: ArtisanCorrection): Long
    
    @Query("SELECT * FROM artisan_correction")
    fun getAllCorrections(): Flow<List<ArtisanCorrection>>
}
