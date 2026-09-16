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
    object Capturing : PipelineProgressState()
    object ProcessingImage : PipelineProgressState()
    object TranscribingAudio : PipelineProgressState()
    object GeneratingCatalog : PipelineProgressState()
    data class Ready(val item: CatalogItem) : PipelineProgressState()
    data class Error(val message: String) : PipelineProgressState()
}

class ProductPipelineRepository(
    private val visionProcessor: VisionProcessor,
    private val audioTranscriber: AudioTranscriber,
    private val inferenceEngine: CatalogInferenceEngine,
    private val catalogDao: CatalogDao,
    private val profileDao: ProfileDao
) {

    fun processRawProduct(
        rawBitmap: Bitmap?, 
        audioUri: Uri?
    ): Flow<PipelineProgressState> = flow {
        try {
            emit(PipelineProgressState.Capturing)
            
            // 1. Vision Processing
            emit(PipelineProgressState.ProcessingImage)
            val processedImageUri = if (rawBitmap != null) {
                val cleanBitmap = visionProcessor.removeBackground(rawBitmap)
                "mock_processed_uri" // In reality, save cleanBitmap to file and get URI
            } else null

            // 2. Audio Transcription
            emit(PipelineProgressState.TranscribingAudio)
            val transcription = if (audioUri != null) {
                audioTranscriber.transcribeAudio(audioUri)
            } else "Handcrafted item."

            // 3. Catalog Generation
            emit(PipelineProgressState.GeneratingCatalog)
            val profile = profileDao.getProfile().firstOrNull() 
                ?: com.example.kalax.data.local.entity.ArtisanProfile(
                    name = "Default", 
                    craftType = "General", 
                    baseHourlyLaborRate = 100.0, 
                    standardPackagingCost = 20.0
                )
                
            val response = inferenceEngine.generateCatalogJson(transcription, profile)

            // 4. Save to Database
            val catalogItem = CatalogItem(
                title = response.title,
                category = response.category,
                description = response.description,
                tags = response.tags.joinToString(","),
                rawPhotoUri = "mock_raw_uri",
                processedPhotoUri = processedImageUri,
                materialCost = response.cost_breakdown.material,
                labourCost = response.cost_breakdown.labour,
                packagingCost = response.cost_breakdown.packaging,
                suggestedPrice = response.suggested_price,
                readinessScore = response.readiness_score
            )
            
            val id = catalogDao.insertCatalogItem(catalogItem)
            
            emit(PipelineProgressState.Ready(catalogItem.copy(id = id)))
            
        } catch (e: Exception) {
            emit(PipelineProgressState.Error(e.message ?: "Unknown Pipeline Error"))
        }
    }
}
