package com.example.kalax.ui.product

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    val status: String = "Draft"
)

class ProductViewModel : ViewModel() {
    private val _draft = MutableStateFlow(ProductDraft())
    val draft: StateFlow<ProductDraft> = _draft.asStateFlow()

    private val _catalog = MutableStateFlow<List<ProductDraft>>(emptyList())
    val catalog: StateFlow<List<ProductDraft>> = _catalog.asStateFlow()

    fun updateDraft(update: (ProductDraft) -> ProductDraft) {
        _draft.update(update)
    }

    fun clearDraft() {
        _draft.value = ProductDraft()
    }

    fun publishDraft() {
        val current = _draft.value.copy(status = "Published")
        _catalog.update { it + current }
        clearDraft()
    }

    fun saveDraft() {
        val current = _draft.value.copy(status = "Draft")
        _catalog.update { it + current }
        clearDraft()
    }
}
