package com.example.kalax.ui.product

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalax.data.api.ApiClient
import com.example.kalax.data.api.SyncProductRequest
import com.example.kalax.engine.EdgeAIEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.util.UUID

data class ProductDraft(
    val id: String = UUID.randomUUID().toString(),
    val imageUri: String? = null,
    val enhancedImageUri: String? = null,
    val name: String = "",
    val category: String = "",
    val material: String = "",
    val description: String = "",
    val keywords: String = "",
    val rawCost: Int = 0,
    val labourCost: Int = 0,
    val packagingCost: Int = 0,
    val otherCost: Int = 0,
    val totalCost: Int = 0,
    val recommendedPrice: Int = 0,
    val score: Int = 0,
    val dimensions: String = "",
    val status: String = "Draft",
    val transcribedText: String = ""
)

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val edgeAIEngine = EdgeAIEngine(application)

    private val _draft = MutableStateFlow(ProductDraft())
    val draft: StateFlow<ProductDraft> = _draft.asStateFlow()

    private val _catalog = MutableStateFlow<List<ProductDraft>>(emptyList())
    val catalog: StateFlow<List<ProductDraft>> = _catalog.asStateFlow()

    init {
        viewModelScope.launch {
            edgeAIEngine.initialize()
        }
        loadCatalog()
    }

    fun loadCatalog() {
        viewModelScope.launch {
            try {
                val res = ApiClient.api.getProducts()
                if (res.success) {
                    val drafts = res.data.map { dto ->
                        ProductDraft(
                            id = dto.offline_id ?: dto.id.toString(),
                            imageUri = dto.original_image?.let { "http://192.168.31.59:8000$it" },
                            enhancedImageUri = dto.enhanced_image?.let { "http://192.168.31.59:8000$it" },
                            name = dto.name,
                            category = dto.category,
                            material = dto.material,
                            description = dto.description ?: "",
                            keywords = dto.keywords ?: "",
                            rawCost = dto.raw_material_cost.toInt(),
                            totalCost = dto.total_cost.toInt(),
                            recommendedPrice = dto.recommended_price?.toInt() ?: 0,
                            score = dto.commerce_score,
                            dimensions = dto.dimensions ?: "",
                            status = dto.status
                        )
                    }
                    _catalog.value = drafts
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun createDraft(name: String, category: String, material: String) {
        _draft.update {
            it.copy(id = UUID.randomUUID().toString(), name = name, category = category, material = material)
        }
    }

    fun updateDraft(update: (ProductDraft) -> ProductDraft) {
        _draft.update(update)
    }

    fun uploadImage(file: File) {
        // In Edge mode, we just store the URI locally until sync
        _draft.update { it.copy(imageUri = file.absolutePath) }
    }
    
    fun enhanceImage() {
        viewModelScope.launch {
            delay(1500) // Simulate Edge Processing (MediaPipe)
            _draft.update { it.copy(enhancedImageUri = it.imageUri) } // Stub: use original as enhanced
        }
    }

    fun processVoice(language: String) {
        viewModelScope.launch {
            delay(1000) // Simulate local Whisper.cpp
            _draft.update { it.copy(transcribedText = "This is a handmade bamboo basket.") }
        }
    }

    fun generateCatalog() {
        viewModelScope.launch {
            val responseJson = edgeAIEngine.generateCatalogOffline(
                _draft.value.transcribedText,
                _draft.value.category,
                _draft.value.rawCost
            )
            try {
                val json = JSONObject(responseJson)
                _draft.update {
                    it.copy(
                        name = json.optString("seo_title", it.name),
                        description = json.optString("description", it.description),
                        recommendedPrice = json.optInt("recommended_price_inr", it.recommendedPrice)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun calculatePrice() {
        _draft.update {
            val total = it.rawCost + it.labourCost + it.packagingCost + it.otherCost
            it.copy(
                totalCost = total,
                recommendedPrice = if (it.recommendedPrice == 0) total + (total * 0.4).toInt() else it.recommendedPrice
            )
        }
    }
    
    fun getCommerceScore() {
        // Local deterministic scoring
        _draft.update {
            var score = 30
            if (it.name.isNotEmpty()) score += 10
            if (it.description.isNotEmpty()) score += 10
            if (it.enhancedImageUri != null) score += 20
            if (it.recommendedPrice > 0) score += 10
            if (it.dimensions.isNotEmpty()) score += 10
            if (it.keywords.isNotEmpty()) score += 10
            it.copy(score = score)
        }
    }

    fun clearDraft() {
        _draft.value = ProductDraft()
    }

    fun publishDraft() {
        val current = _draft.value.copy(status = "Published")
        _draft.value = current
        syncToCloud(current)
    }

    fun saveDraft() {
        val current = _draft.value.copy(status = "Draft")
        _draft.value = current
        syncToCloud(current)
    }

    private fun syncToCloud(draftItem: ProductDraft) {
        viewModelScope.launch {
            try {
                val req = SyncProductRequest(
                    offline_id = draftItem.id,
                    name = draftItem.name.ifEmpty { "Untitled" },
                    category = draftItem.category.ifEmpty { "Uncategorized" },
                    material = draftItem.material.ifEmpty { "Unknown" },
                    description = draftItem.description,
                    seo_title = draftItem.name,
                    keywords = draftItem.keywords,
                    raw_material_cost = draftItem.rawCost.toFloat(),
                    labour_cost = draftItem.labourCost.toFloat(),
                    packaging_cost = draftItem.packagingCost.toFloat(),
                    other_cost = draftItem.otherCost.toFloat(),
                    total_cost = draftItem.totalCost.toFloat(),
                    recommended_price = draftItem.recommendedPrice.toFloat(),
                    pricing_confidence = 85,
                    commerce_score = draftItem.score,
                    dimensions = draftItem.dimensions,
                    status = draftItem.status
                )
                val res = ApiClient.api.syncProducts(listOf(req))
                if (res.success) {
                    loadCatalog()
                    clearDraft()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Offline mode: just add to local catalog StateFlow to reflect in UI
                _catalog.update { current ->
                    val filtered = current.filter { it.id != draftItem.id }
                    filtered + draftItem
                }
                clearDraft()
            }
        }
    }
}
