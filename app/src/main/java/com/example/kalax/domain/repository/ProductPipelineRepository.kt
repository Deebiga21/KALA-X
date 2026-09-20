package com.example.kalax.domain.repository

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.android.gms.tasks.Tasks
import com.example.kalax.data.remote.AnalyzeImageRequest
import android.graphics.Bitmap
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
    
    // Step 1: Create session
    suspend fun createProductSession(): Long {
        return try {
            val request = com.example.kalax.data.remote.CreateProductRequest(name = "New Draft")
            val response = apiService.createProduct(request)
            val item = response.toCatalogItem()
            catalogDao.insertCatalogItem(item)
            item.id
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to local if network fails
            val newItem = CatalogItem(
                title = "", category = "Uncategorized", description = "", tags = "",
                rawPhotoUri = null, processedPhotoUri = null,
                materialCost = 0.0, labourCost = 0.0, packagingCost = 0.0,
                suggestedPrice = 0.0, readinessScore = 0
            )
            catalogDao.insertCatalogItem(newItem)
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
        try {
            val text = "Handcrafted item." // Mocking voice text or use audioUri if needed
            val response = apiService.processVoice(productId, VoiceRequest(text = text, audioUri = audioUri?.toString()))
            catalogDao.insertCatalogItem(response.toCatalogItem())
            return response.transcription ?: text
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "Handcrafted item."
    }

    // Step 4: Generate Catalog
    suspend fun generateCatalog(productId: Long, transcription: String) {
        try {
            val request = com.example.kalax.data.remote.GenerateCatalogRequest(transcription)
            val response = apiService.generateCatalog(productId, request)
            catalogDao.insertCatalogItem(response.toCatalogItem())
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
            val response = apiService.calculatePricing(productId, request)
            catalogDao.insertCatalogItem(response.toCatalogItem())
            response.suggestedPrice
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback locally if network fails
            val totalCost = rawCost + laborCost + packagingCost + otherCost
            totalCost * 1.3
        }
    }

    suspend fun checkReadiness(productId: Long): Int {
        return try {
            val response = apiService.checkReadiness(productId)
            catalogDao.insertCatalogItem(response.toCatalogItem())
            response.readinessScore
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    suspend fun publishProduct(productId: Long) {
        try {
            val response = apiService.publishProduct(productId)
            catalogDao.insertCatalogItem(response.toCatalogItem())
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
            val response = apiService.updateProduct(item)
            catalogDao.insertCatalogItem(response.toCatalogItem())
        } catch (e: Exception) {
            e.printStackTrace()
            catalogDao.insertCatalogItem(item)
        }
    }
    
    suspend fun getInsights() = apiService.getInsights()
}
