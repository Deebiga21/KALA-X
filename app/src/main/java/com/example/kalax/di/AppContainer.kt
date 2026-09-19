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
import com.example.kalax.ai.vision.ImageEnhancementEngine
import com.example.kalax.data.remote.KalaXApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
    val apiService: KalaXApiService
    val imageEnhancementEngine: ImageEnhancementEngine
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
            profileDao = database.profileDao(),
            apiService = apiService
        )
    }

    override val pricingEngine: PricingEngine by lazy { PricingEngine() }
    override val readinessEngine: CommerceReadinessEngine by lazy { CommerceReadinessEngine() }
    override val imageEnhancementEngine: ImageEnhancementEngine by lazy { ImageEnhancementEngine(context) }
    
    override val apiService: KalaXApiService by lazy {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KalaXApiService::class.java)
    }
}
