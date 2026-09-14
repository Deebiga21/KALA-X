package com.example.kalax.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalax.data.api.ApiClient
import com.example.kalax.data.api.CreateProductRequest
import com.example.kalax.data.api.UpdateProductRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

data class ProductDraft(
    val id: String = "",
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
    val status: String = "Draft"
)

class ProductViewModel : ViewModel() {
    private val _draft = MutableStateFlow(ProductDraft())
    val draft: StateFlow<ProductDraft> = _draft.asStateFlow()

    private val _catalog = MutableStateFlow<List<ProductDraft>>(emptyList())
    val catalog: StateFlow<List<ProductDraft>> = _catalog.asStateFlow()

    init {
        loadCatalog()
    }

    fun loadCatalog() {
        viewModelScope.launch {
            try {
                val res = ApiClient.api.getProducts()
                if (res.success) {
                    val drafts = res.data.map { dto ->
                        ProductDraft(
                            id = dto.id.toString(),
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
        viewModelScope.launch {
            try {
                val req = CreateProductRequest(name, category, material)
                val res = ApiClient.api.createProduct(req)
                if (res.success) {
                    _draft.update {
                        it.copy(id = res.data.id.toString(), name = name, category = category, material = material)
                    }
                    loadCatalog()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateDraft(update: (ProductDraft) -> ProductDraft) {
        _draft.update(update)
    }

    fun uploadImage(file: File) {
        val draftId = _draft.value.id.toIntOrNull() ?: return
        viewModelScope.launch {
            try {
                val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, reqFile)
                val res = ApiClient.api.uploadImage(draftId, body)
                if (res.success) {
                    _draft.update { it.copy(imageUri = "http://192.168.31.59:8000/uploads/products/${draftId}_${file.name}") }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun enhanceImage() {
        val draftId = _draft.value.id.toIntOrNull() ?: return
        viewModelScope.launch {
            try {
                ApiClient.api.enhanceImage(draftId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun processVoice(language: String) {
        val draftId = _draft.value.id.toIntOrNull() ?: return
        viewModelScope.launch {
            try {
                ApiClient.api.processVoice(draftId, language)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun generateCatalog() {
        val draftId = _draft.value.id.toIntOrNull() ?: return
        viewModelScope.launch {
            try {
                ApiClient.api.generateCatalog(draftId)
                // Fetch updated product
                val pRes = ApiClient.api.getProduct(draftId)
                if (pRes.success) {
                    val p = pRes.data
                    _draft.update {
                        it.copy(
                            description = p.description ?: "",
                            keywords = p.keywords ?: "",
                            name = p.name
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun calculatePrice() {
        val draftId = _draft.value.id.toIntOrNull() ?: return
        viewModelScope.launch {
            try {
                ApiClient.api.calculatePrice(draftId)
                val pRes = ApiClient.api.getProduct(draftId)
                if (pRes.success) {
                    val p = pRes.data
                    _draft.update {
                        it.copy(
                            totalCost = p.total_cost.toInt(),
                            recommendedPrice = p.recommended_price?.toInt() ?: 0,
                            score = p.commerce_score
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun getCommerceScore() {
        val draftId = _draft.value.id.toIntOrNull() ?: return
        viewModelScope.launch {
            try {
                ApiClient.api.getCommerceScore(draftId)
                val pRes = ApiClient.api.getProduct(draftId)
                if (pRes.success) {
                    val p = pRes.data
                    _draft.update {
                        it.copy(
                            score = p.commerce_score
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearDraft() {
        _draft.value = ProductDraft()
    }

    fun publishDraft() {
        val draftId = _draft.value.id.toIntOrNull() ?: return
        viewModelScope.launch {
            try {
                ApiClient.api.publishProduct(draftId)
                loadCatalog()
                clearDraft()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveDraft() {
        val draftId = _draft.value.id.toIntOrNull()
        if (draftId != null) {
            viewModelScope.launch {
                try {
                    val req = UpdateProductRequest(
                        name = _draft.value.name,
                        category = _draft.value.category,
                        material = _draft.value.material,
                        description = _draft.value.description,
                        keywords = _draft.value.keywords,
                        dimensions = _draft.value.dimensions,
                        weight = ""
                    )
                    ApiClient.api.updateProduct(draftId, req)
                    loadCatalog()
                    clearDraft()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
