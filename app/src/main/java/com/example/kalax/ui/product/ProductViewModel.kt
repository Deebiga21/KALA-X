package com.example.kalax.ui.product

import android.app.Application
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kalax.KalaXApplication
import com.example.kalax.data.local.entity.CatalogItem
import com.example.kalax.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

sealed class PipelineState {
    object Idle : PipelineState()
    object Capturing : PipelineState()
    object Enhancing : PipelineState()
    object Transcribing : PipelineState()
    object Generating : PipelineState()
    object Pricing : PipelineState()
    object Reviewing : PipelineState()
}

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

fun CatalogItem.toDraft(): ProductDraft = ProductDraft(
    id = this.id.toString(),
    imageUri = this.rawPhotoUri,
    enhancedImageUri = this.processedPhotoUri,
    name = this.title,
    category = this.category,
    material = "",
    description = this.description,
    keywords = this.tags,
    rawCost = this.materialCost.toInt(),
    labourCost = this.labourCost.toInt(),
    packagingCost = this.packagingCost.toInt(),
    otherCost = this.otherCost.toInt(),
    totalCost = (this.materialCost + this.labourCost + this.packagingCost + this.otherCost).toInt(),
    recommendedPrice = this.suggestedPrice.toInt(),
    score = this.readinessScore,
    dimensions = this.dimensions,
    status = this.status,
    transcribedText = this.transcription
)

class ProductViewModel(
    application: Application,
    private val container: AppContainer
) : AndroidViewModel(application) {

    private val _draft = MutableStateFlow(ProductDraft())
    val draft: StateFlow<ProductDraft> = _draft.asStateFlow()

    private val _catalog = MutableStateFlow<List<ProductDraft>>(emptyList())
    val catalog: StateFlow<List<ProductDraft>> = _catalog.asStateFlow()

    private val _pipelineState = MutableStateFlow<PipelineState>(PipelineState.Idle)
    val pipelineState: StateFlow<PipelineState> = _pipelineState.asStateFlow()

    private val _profile = MutableStateFlow<com.example.kalax.data.local.entity.ArtisanProfile?>(null)
    val profile: StateFlow<com.example.kalax.data.local.entity.ArtisanProfile?> = _profile.asStateFlow()

    private var currentSessionId: Long? = null

    init {
        loadCatalog()
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            container.database.profileDao().getProfile().collect { p ->
                _profile.value = p
            }
        }
    }

    fun loadCatalog() {
        viewModelScope.launch {
            try {
                container.database.catalogDao().getAllCatalogItems().collect { list ->
                    _catalog.value = list.map { it.toDraft() }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun startNewSession() {
        viewModelScope.launch {
            _pipelineState.value = PipelineState.Idle
            _draft.value = ProductDraft()
            currentSessionId = container.pipelineRepository.createProductSession()
        }
    }

    fun createDraft(name: String, category: String, material: String) {
        viewModelScope.launch {
            val id = container.pipelineRepository.createProductSession()
            currentSessionId = id
            syncLocalDraft()
            _draft.update { it.copy(name = name, category = category, material = material) }
        }
    }

    private suspend fun syncLocalDraft() {
        currentSessionId?.let { id ->
            val item = container.pipelineRepository.observeProduct(id).firstOrNull()
            item?.let { dbItem ->
                _draft.value = dbItem.toDraft()
            }
        }
    }

    private fun getCurrentCatalogItem(): CatalogItem {
        val d = _draft.value
        val id = currentSessionId ?: 0L
        return CatalogItem(
            id = id,
            title = d.name,
            category = d.category,
            description = d.description,
            tags = d.keywords,
            rawPhotoUri = d.imageUri,
            processedPhotoUri = d.enhancedImageUri,
            materialCost = d.rawCost.toDouble(),
            labourCost = d.labourCost.toDouble(),
            packagingCost = d.packagingCost.toDouble(),
            otherCost = d.otherCost.toDouble(),
            suggestedPrice = d.recommendedPrice.toDouble(),
            readinessScore = d.score,
            dimensions = d.dimensions,
            transcription = d.transcribedText,
            status = d.status
        )
    }

    fun updateDraft(update: (ProductDraft) -> ProductDraft) {
        _draft.update(update)
        viewModelScope.launch {
            if (currentSessionId != null) {
                container.pipelineRepository.updateProduct(getCurrentCatalogItem())
            }
        }
    }

    fun uploadImage(file: File) {
        val uriStr = file.absolutePath
        _draft.update { it.copy(imageUri = uriStr) }
    }
    
    fun enhanceImage() {
        viewModelScope.launch {
            _pipelineState.value = PipelineState.Enhancing
            val uriStr = _draft.value.imageUri ?: return@launch
            val bitmap = BitmapFactory.decodeFile(uriStr)
            
            val processed = container.pipelineRepository.processImage(currentSessionId ?: 0, bitmap, uriStr)
            _draft.update { it.copy(enhancedImageUri = processed) }
            _pipelineState.value = PipelineState.Idle
        }
    }

    fun processVoiceAndGenerate(language: String) {
        viewModelScope.launch {
            _pipelineState.value = PipelineState.Transcribing
            val transcribed = container.pipelineRepository.transcribeAudio(currentSessionId ?: 0, null)
            _draft.update { it.copy(transcribedText = transcribed) }
            
            _pipelineState.value = PipelineState.Generating
            container.pipelineRepository.generateCatalog(currentSessionId ?: 0, _draft.value.transcribedText)
            syncLocalDraft()
            
            _pipelineState.value = PipelineState.Idle
        }
    }
    
    fun processVoice(language: String) {
        viewModelScope.launch {
            _pipelineState.value = PipelineState.Transcribing
            val transcribed = container.pipelineRepository.transcribeAudio(currentSessionId ?: 0, null)
            _draft.update { it.copy(transcribedText = transcribed) }
            _pipelineState.value = PipelineState.Idle
        }
    }

    fun processSpeechText(text: String) {
        viewModelScope.launch {
            _draft.update { it.copy(transcribedText = text) }
            _pipelineState.value = PipelineState.Generating
            container.pipelineRepository.generateCatalog(currentSessionId ?: 0, text)
            syncLocalDraft()
            _pipelineState.value = PipelineState.Idle
        }
    }

    fun generateCatalog() {
        viewModelScope.launch {
            _pipelineState.value = PipelineState.Generating
            container.pipelineRepository.generateCatalog(currentSessionId ?: 0, _draft.value.transcribedText)
            syncLocalDraft()
            _pipelineState.value = PipelineState.Idle
        }
    }

    fun calculatePrice() {
        viewModelScope.launch {
            _pipelineState.value = PipelineState.Pricing
            val price = container.pipelineRepository.calculatePricing(currentSessionId ?: 0)
            _draft.update { it.copy(recommendedPrice = price.toInt()) }
            _pipelineState.value = PipelineState.Idle
        }
    }
    
    fun getCommerceScore() {
        viewModelScope.launch {
            _pipelineState.value = PipelineState.Reviewing
            val score = container.pipelineRepository.checkReadiness(currentSessionId ?: 0)
            _draft.update { it.copy(score = score) }
            _pipelineState.value = PipelineState.Idle
        }
    }

    fun clearDraft() {
        currentSessionId = null
        _draft.value = ProductDraft()
        _pipelineState.value = PipelineState.Idle
    }

    fun publishDraft() {
        _draft.update { it.copy(status = "Published") }
        viewModelScope.launch {
            if (currentSessionId != null) {
                container.pipelineRepository.publishProduct(currentSessionId!!)
                // The updateProduct is optional here, as publishProduct should sync the state
            }
            clearDraft()
        }
    }

    fun createProfile(name: String, emailOrPhone: String, craftType: String, language: String) {
        viewModelScope.launch {
            val isEmail = emailOrPhone.contains("@")
            val profile = com.example.kalax.data.local.entity.ArtisanProfile(
                id = "default_artisan",
                name = name,
                craftType = craftType,
                baseHourlyLaborRate = 50.0, // Default
                standardPackagingCost = 20.0, // Default
                phone = if (!isEmail) emailOrPhone else "",
                email = if (isEmail) emailOrPhone else "",
                preferredLanguage = language
            )
            container.database.profileDao().insertProfile(profile)
        }
    }

    fun registerArtisan(name: String, craftType: String, emailOrPhone: String = "", language: String = "English") {
        viewModelScope.launch {
            val isEmail = emailOrPhone.contains("@")
            val profile = com.example.kalax.data.local.entity.ArtisanProfile(
                id = "default_artisan",
                name = name,
                craftType = craftType,
                baseHourlyLaborRate = 50.0,
                standardPackagingCost = 20.0,
                phone = if (!isEmail) emailOrPhone else "",
                email = if (isEmail) emailOrPhone else "",
                preferredLanguage = language
            )
            container.database.profileDao().insertProfile(profile)
        }
    }

    fun updateProfile(name: String, language: String) {
        viewModelScope.launch {
            val current = _profile.value ?: return@launch
            val updated = current.copy(name = name, preferredLanguage = language)
            container.database.profileDao().insertProfile(updated)
        }
    }


    fun saveDraft() {
        _draft.update { it.copy(status = "Draft") }
        viewModelScope.launch {
            if (currentSessionId != null) {
                container.pipelineRepository.updateProduct(getCurrentCatalogItem())
            }
            clearDraft()
        }
    }

    fun deleteProduct(id: String) {
        viewModelScope.launch {
            val dbId = id.toLongOrNull() ?: return@launch
            container.database.catalogDao().deleteById(dbId)
        }
    }

    companion object {
        fun provideFactory(app: KalaXApplication): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
                    return ProductViewModel(app, app.container) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
