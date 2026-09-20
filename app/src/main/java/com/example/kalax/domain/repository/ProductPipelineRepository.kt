package com.example.kalax.domain.repository

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.android.gms.tasks.Tasks
import com.example.kalax.data.remote.AnalyzeImageRequest
import android.graphics.Bitmap
import kotlinx.coroutines.flow.firstOrNull
import android.net.Uri
import com.example.kalax.ai.llm.CatalogInferenceEngine
import com.example.kalax.ai.speech.AudioTranscriber
import com.example.kalax.ai.vision.VisionProcessor
import com.example.kalax.data.local.dao.CatalogDao
import com.example.kalax.data.local.dao.ProfileDao
import com.example.kalax.data.local.entity.CatalogItem
import com.example.kalax.data.remote.KalaXApiService
import com.example.kalax.data.remote.VoiceRequest
import com.example.kalax.data.remote.toCatalogItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

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
    private val profileDao: ProfileDao,
    private val apiService: KalaXApiService
) {
    
    // Step 1: Create session (OFFLINE FIRST)
    suspend fun createProductSession(): Long {
        return try {
            val newItem = CatalogItem(
                title = "New Draft", category = "Uncategorized", description = "", tags = "",
                rawPhotoUri = null, processedPhotoUri = null,
                materialCost = 0.0, labourCost = 0.0, packagingCost = 0.0,
                suggestedPrice = 0.0, readinessScore = 0, status = "Draft"
            )
            val localId = catalogDao.insertCatalogItem(newItem)
            
            try {
                val request = com.example.kalax.data.remote.CreateProductRequest(name = "New Draft")
                apiService.createProduct(request)
            } catch (e: Exception) {
                // Ignore backend failure
            }
            
            localId
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    // Step 2: Process Image
    suspend fun processImage(productId: Long, rawBitmap: Bitmap?, rawUri: String): String {
        try {
            var labels = ""
            if (rawBitmap != null) {
                val image = InputImage.fromBitmap(rawBitmap, 0)
                val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
                val labelsList = Tasks.await(labeler.process(image))
                labels = labelsList.joinToString(",") { it.text }
            }

            val file = File(rawUri)
            if (file.exists()) {
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val multipart = MultipartBody.Part.createFormData("image", file.name, requestBody)
                val response = apiService.uploadImage(productId, multipart)
                catalogDao.insertCatalogItem(response.toCatalogItem())

                // analyze image with labels
                if (labels.isNotEmpty()) {
                    val analyzed = apiService.analyzeImage(productId, AnalyzeImageRequest(labels = labels))
                    catalogDao.insertCatalogItem(analyzed.toCatalogItem())
                }
                
                val enhanced = apiService.enhanceImage(productId)
                catalogDao.insertCatalogItem(enhanced.toCatalogItem())
                return enhanced.processedPhotoUri ?: rawUri
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return rawUri
    }

    // Step 3: Transcribe Voice
    suspend fun transcribeAudio(productId: Long, audioUri: Uri?): String {
        return try {
            val text = "Handcrafted item." // Mocking voice text or use audioUri if needed
            try {
                apiService.processVoice(productId, VoiceRequest(text = text, audioUri = audioUri?.toString()))
            } catch(e: Exception) {}
            
            val item = catalogDao.getById(productId)
            if (item != null) {
                catalogDao.insertCatalogItem(item.copy(transcription = text))
            }
            text
        } catch (e: Exception) {
            e.printStackTrace()
            "Handcrafted item."
        }
    }

    // Step 4: Generate Catalog (ON-DEVICE)
    suspend fun generateCatalog(productId: Long, transcription: String) {
        try {
            val defaultProfile = com.example.kalax.data.local.entity.ArtisanProfile(
                name = "Artisan",
                craftType = "General",
                baseHourlyLaborRate = 100.0,
                standardPackagingCost = 20.0
            )
            val profile = profileDao.getProfile().firstOrNull() ?: defaultProfile
            
            // Generate on-device using LocalCatalogInferenceEngine
            val aiResult = inferenceEngine.generateCatalogJson(transcription, profile)
            
            // Get local item and update it
            val currentItem = catalogDao.getById(productId)
            if (currentItem != null) {
                val updatedItem = currentItem.copy(
                    title = aiResult.title,
                    category = aiResult.category,
                    description = aiResult.description,
                    keywords = aiResult.tags.joinToString(", "),
                    materialCost = aiResult.cost_breakdown.material,
                    labourCost = aiResult.cost_breakdown.labour,
                    packagingCost = aiResult.cost_breakdown.packaging,
                    suggestedPrice = aiResult.suggested_price,
                    readinessScore = aiResult.readiness_score
                )
                catalogDao.insertCatalogItem(updatedItem)
                
                // Sync to backend in background if available
                try {
                    apiService.updateProduct(updatedItem)
                } catch (e: Exception) {
                    // Ignore backend sync failure, we are offline-first
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    suspend fun calculatePricing(productId: Long, rawCost: Int, laborCost: Int, packagingCost: Int, otherCost: Int): Double {
        return try {
            val request = com.example.kalax.data.remote.PricingRequest(
                raw_material_cost = rawCost.toFloat(),
                labor_cost = laborCost.toFloat(),
                packaging_cost = packagingCost.toFloat(),
                other_cost = otherCost.toFloat(),
                margin_percentage = 30.0f
            )
            
            var suggestedPrice = (rawCost + laborCost + packagingCost + otherCost) * 1.3
            try {
                val response = apiService.calculatePricing(productId, request)
                suggestedPrice = response.suggestedPrice
            } catch (e: Exception) {}

            val item = catalogDao.getById(productId)
            if (item != null) {
                catalogDao.insertCatalogItem(item.copy(
                    materialCost = rawCost.toDouble(),
                    labourCost = laborCost.toDouble(),
                    packagingCost = packagingCost.toDouble(),
                    otherCost = otherCost.toDouble(),
                    suggestedPrice = suggestedPrice
                ))
            }
            suggestedPrice
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback locally if network fails
            val totalCost = rawCost + laborCost + packagingCost + otherCost
            totalCost * 1.3
        }
    }

    suspend fun checkReadiness(productId: Long): Int {
        return try {
            var score = 85
            try {
                val response = apiService.checkReadiness(productId)
                score = response.readinessScore
            } catch (e: Exception) {}
            
            val item = catalogDao.getById(productId)
            if (item != null) {
                catalogDao.insertCatalogItem(item.copy(readinessScore = score))
            }
            score
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    // Step 5: Publish Product
    suspend fun publishProduct(productId: Long) {
        try {
            val item = catalogDao.getById(productId)
            if (item != null) {
                val publishedItem = item.copy(status = "Published")
                catalogDao.insertCatalogItem(publishedItem)
                try {
                    apiService.publishProduct(productId)
                } catch (e: Exception) {
                    // Ignore backend error
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    // Additional DAO delegators
    fun observeProduct(productId: Long): Flow<CatalogItem?> = flow {
        catalogDao.getAllCatalogItems().collect { list ->
            emit(list.find { it.id == productId })
        }
    }
    
    suspend fun updateProduct(item: CatalogItem) {
        try {
            catalogDao.insertCatalogItem(item)
            try {
                apiService.updateProduct(item)
            } catch (e: Exception) {
                // Ignore backend failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    suspend fun getInsights() = apiService.getInsights()
}
