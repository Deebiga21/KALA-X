package com.example.kalax.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.example.kalax.ai.llm.CatalogInferenceEngine
import com.example.kalax.ai.speech.AudioTranscriber
import com.example.kalax.ai.vision.VisionProcessor
import com.example.kalax.data.local.dao.CatalogDao
import com.example.kalax.data.local.dao.ProfileDao
import com.example.kalax.data.local.entity.CatalogItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

sealed class PipelineProgressState {
    object Idle : PipelineProgressState()
    object ProcessingImage : PipelineProgressState()
    object TranscribingAudio : PipelineProgressState()
    object GeneratingCatalog : PipelineProgressState()
    object SavingProduct : PipelineProgressState()
    data class Completed(val productId: Long) : PipelineProgressState()
    data class Error(val message: String) : PipelineProgressState()
}

class ProductPipelineRepository(
    private val visionProcessor: VisionProcessor,
    private val audioTranscriber: AudioTranscriber,
    private val inferenceEngine: CatalogInferenceEngine,
    private val catalogDao: CatalogDao,
    private val profileDao: ProfileDao
) {
    
    // Step 1: Create session
    suspend fun createProductSession(): Long {
        val newItem = CatalogItem(
            title = "", category = "Uncategorized", description = "", tags = "",
            rawPhotoUri = null, processedPhotoUri = null,
            materialCost = 0.0, labourCost = 0.0, packagingCost = 0.0,
            suggestedPrice = 0.0, readinessScore = 0
        )
        return catalogDao.insertCatalogItem(newItem)
    }

    // Step 2: Process Image
    suspend fun processImage(productId: Long, rawBitmap: Bitmap?, rawUri: String): String {
        val processedImageUri = if (rawBitmap != null) {
            val cleanBitmap = visionProcessor.removeBackground(rawBitmap)
            rawUri // using rawUri as stub for now
        } else {
            rawUri
        }
        val item = catalogDao.getAllCatalogItems().firstOrNull()?.find { it.id == productId }
        item?.let {
            catalogDao.insertCatalogItem(it.copy(rawPhotoUri = rawUri, processedPhotoUri = processedImageUri))
        }
        return processedImageUri
    }

    // Step 3: Transcribe Voice
    suspend fun transcribeAudio(productId: Long, audioUri: Uri?): String {
        val transcription = if (audioUri != null) {
            audioTranscriber.transcribeAudio(audioUri)
        } else "Handcrafted item."
        return transcription
    }

    // Step 4: Generate Catalog
    suspend fun generateCatalog(productId: Long, transcription: String) {
        val profile = profileDao.getProfile().firstOrNull() 
            ?: com.example.kalax.data.local.entity.ArtisanProfile(
                name = "Default", craftType = "General", 
                baseHourlyLaborRate = 100.0, standardPackagingCost = 20.0
            )

        // RAG: inject last 5 corrections as few-shot examples
        val recentCorrections = catalogDao.getRecentCorrections(5)

        val response = inferenceEngine.generateCatalogJson(transcription, profile, recentCorrections)
        val item = catalogDao.getAllCatalogItems().firstOrNull()?.find { it.id == productId }
        item?.let {
            val updated = it.copy(
                title = response.title,
                category = response.category,
                description = response.description,
                tags = response.tags.joinToString(","),
                materialCost = response.cost_breakdown.material,
                labourCost = response.cost_breakdown.labour,
                packagingCost = response.cost_breakdown.packaging,
                suggestedPrice = response.suggested_price,
                readinessScore = response.readiness_score
            )
            catalogDao.insertCatalogItem(updated)
        }
    }
    
    // Additional DAO delegators
    fun observeProduct(productId: Long): Flow<CatalogItem?> = flow {
        catalogDao.getAllCatalogItems().collect { list ->
            emit(list.find { it.id == productId })
        }
    }
    
    suspend fun updateProduct(item: CatalogItem) {
        catalogDao.insertCatalogItem(item)
    }
}
