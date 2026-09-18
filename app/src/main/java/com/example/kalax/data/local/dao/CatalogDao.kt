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

    @Query("SELECT * FROM catalog_item WHERE status = :status ORDER BY id DESC")
    fun getByStatus(status: String): Flow<List<CatalogItem>>

    @Query("SELECT * FROM catalog_item WHERE id = :id")
    suspend fun getById(id: Long): CatalogItem?

    @Query("UPDATE catalog_item SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String): Int

    @Query("SELECT COUNT(*) FROM catalog_item WHERE status = 'Draft'")
    fun getDraftCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM catalog_item WHERE status = 'Published'")
    fun getPublishedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM catalog_item")
    fun getTotalCount(): Flow<Int>

    @Query("DELETE FROM catalog_item WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatalogItem(item: CatalogItem): Long
    
    @Insert
    suspend fun insertCorrection(correction: ArtisanCorrection): Long
    
    @Query("SELECT * FROM artisan_correction")
    fun getAllCorrections(): Flow<List<ArtisanCorrection>>

    @Query("SELECT * FROM artisan_correction ORDER BY id DESC LIMIT :limit")
    suspend fun getRecentCorrections(limit: Int): List<ArtisanCorrection>

    @Query("SELECT * FROM artisan_correction WHERE isSynced = 0")
    suspend fun getUnsyncedCorrections(): List<ArtisanCorrection>

    @Query("UPDATE artisan_correction SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markCorrectionsAsSynced(ids: List<Long>): Int
}
