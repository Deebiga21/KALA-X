package com.example.kalax.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kalax.data.local.dao.CatalogDao
import com.example.kalax.data.local.dao.ProfileDao
import com.example.kalax.data.local.entity.ArtisanCorrection
import com.example.kalax.data.local.entity.ArtisanProfile
import com.example.kalax.data.local.entity.CatalogItem

@Database(entities = [ArtisanProfile::class, CatalogItem::class, ArtisanCorrection::class], version = 1, exportSchema = false)
abstract class KalaXDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun catalogDao(): CatalogDao
    
    companion object {
        @Volatile
        private var INSTANCE: KalaXDatabase? = null
        
        fun getDatabase(context: Context): KalaXDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KalaXDatabase::class.java,
                    "kalax_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
