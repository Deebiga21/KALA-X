package com.example.kalax.di

import android.content.Context
import com.example.kalax.domain.engine.PricingEngine
import com.example.kalax.domain.engine.CommerceReadinessEngine
import com.example.kalax.ai.llm.CatalogInferenceEngine
import com.example.kalax.ai.speech.AudioTranscriber
import com.example.kalax.ai.vision.VisionProcessor
import com.example.kalax.data.local.KalaXDatabase
import com.example.kalax.domain.repository.ProductPipelineRepository
import android.graphics.Bitmap
import android.net.Uri

class DefaultVisionProcessor : VisionProcessor {
    override suspend fun removeBackground(bitmap: Bitmap): Bitmap {
        return bitmap
    }
}

class DefaultAudioTranscriber : AudioTranscriber {
    override suspend fun transcribeAudio(audioUri: Uri): String {
        return "Transcribed audio fallback."
    }
}

interface AppContainer {
    val database: KalaXDatabase
    val pipelineRepository: ProductPipelineRepository
    val pricingEngine: PricingEngine
    val readinessEngine: CommerceReadinessEngine
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val database: KalaXDatabase by lazy {
        KalaXDatabase.getDatabase(context)
    }

    override val pipelineRepository: ProductPipelineRepository by lazy {
        ProductPipelineRepository(
            visionProcessor = DefaultVisionProcessor(),
            audioTranscriber = DefaultAudioTranscriber(),
            inferenceEngine = com.example.kalax.ai.llm.LocalCatalogInferenceEngine(),
            catalogDao = database.catalogDao(),
            profileDao = database.profileDao()
        )
    }

    override val pricingEngine: PricingEngine by lazy { PricingEngine() }
    override val readinessEngine: CommerceReadinessEngine by lazy { CommerceReadinessEngine() }
}
