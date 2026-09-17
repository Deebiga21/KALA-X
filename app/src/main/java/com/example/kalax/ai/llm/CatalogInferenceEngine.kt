package com.example.kalax.ai.llm

import com.example.kalax.data.local.entity.ArtisanProfile
import com.example.kalax.data.local.entity.ArtisanCorrection
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString

@Serializable
data class CatalogGenerationResponse(
    val title: String = "",
    val category: String = "",
    val description: String = "",
    val tags: List<String> = emptyList(),
    val cost_breakdown: CostBreakdown = CostBreakdown(),
    val suggested_price: Double = 0.0,
    val readiness_score: Int = 0,
    val missing_fields: List<String> = emptyList()
)

@Serializable
data class CostBreakdown(
    val material: Double = 0.0,
    val labour: Double = 0.0,
    val packaging: Double = 0.0
)

interface CatalogInferenceEngine {
    suspend fun generateCatalogJson(
        transcription: String,
        profile: ArtisanProfile,
        recentCorrections: List<ArtisanCorrection> = emptyList()
    ): CatalogGenerationResponse
}

class LocalCatalogInferenceEngine : CatalogInferenceEngine {

    override suspend fun generateCatalogJson(
        transcription: String,
        profile: ArtisanProfile,
        recentCorrections: List<ArtisanCorrection>
    ): CatalogGenerationResponse {
        val prompt = synthesizePrompt(transcription, profile, recentCorrections)
        val loraPath = getLocalLoraAdapterPath()
        val rawLlmOutput = runLocalLlm(prompt, loraPath)
        return parseJsonSafely(rawLlmOutput)
    }

    private fun synthesizePrompt(
        transcription: String,
        profile: ArtisanProfile,
        corrections: List<ArtisanCorrection>
    ): String {
        val fewShotRules = if (corrections.isNotEmpty()) {
            "\n\nBased on the artisan's past preferences:\n" + corrections.joinToString("\n") { c ->
                when (c.correctionType) {
                    "PRICE" -> "- For similar items, the artisan prefers pricing around ₹${c.correctedPrice} instead of ₹${c.originalPrice}."
                    else -> "- The artisan corrected '${c.originalText}' to '${c.correctedText}'. Match this style."
                }
            } + "\nApply these preferences to the new listing."
        } else ""

        return """
            You are an expert E-Commerce assistant. Generate a product listing based on this artisan profile and product description.
            Artisan Craft: ${profile.craftType}
            Base Labor Rate: ${profile.baseHourlyLaborRate}
            Description: $transcription
            $fewShotRules
            
            Return ONLY a JSON object exactly matching this format:
            {
              "title": "String",
              "category": "String",
              "description": "String",
              "tags": ["String"],
              "cost_breakdown": { "material": 0, "labour": 0, "packaging": 0 },
              "suggested_price": 0,
              "readiness_score": 0,
              "missing_fields": ["String"]
            }
        """.trimIndent()
    }

    private fun getLocalLoraAdapterPath(): String? {
        // Check internal storage for a downloaded LoRA adapter file
        // In production: context.filesDir / "lora" / "adapter_latest.bin"
        return null
    }

    private fun runLocalLlm(prompt: String, loraAdapterPath: String?): String {
        // Mock ExecuTorch INT4 inference runner
        // In production: load base model + optional LoRA adapter via ExecuTorch/MediaPipe
        return """
            {
              "title": "Handcrafted Terracotta Pot",
              "category": "Home Decor > Pottery",
              "description": "An exquisite terracotta pot handcrafted with love.",
              "tags": ["Handmade", "Terracotta", "Pottery"],
              "cost_breakdown": { "material": 100.0, "labour": 200.0, "packaging": 50.0 },
              "suggested_price": 450.0,
              "readiness_score": 90,
              "missing_fields": ["dimensions"]
            }
        """.trimIndent()
    }

    private fun parseJsonSafely(rawOutput: String): CatalogGenerationResponse {
        return try {
            val jsonStart = rawOutput.indexOf("{")
            val jsonEnd = rawOutput.lastIndexOf("}") + 1
            if (jsonStart != -1 && jsonEnd > jsonStart) {
                val cleanJson = rawOutput.substring(jsonStart, jsonEnd)
                val jsonParser = Json { ignoreUnknownKeys = true }
                jsonParser.decodeFromString<CatalogGenerationResponse>(cleanJson)
            } else {
                CatalogGenerationResponse()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            CatalogGenerationResponse(title = "Fallback Item", description = "Failed to parse AI output.")
        }
    }
}
