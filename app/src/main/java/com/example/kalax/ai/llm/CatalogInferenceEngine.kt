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
        // Extract product name from prompt/transcription
        // Since we are entirely offline and bypassing a real LLM for the hackathon (without 2GB model), 
        // we use a highly sophisticated heuristic parser to generate a magical response based on their words!
        
        val transcription = prompt.substringAfter("Transcription: \"").substringBefore("\"").trim().lowercase()
        
        var name = "Handcrafted Item"
        var category = "Home & Living"
        var baseMaterial = "High-Quality Material"
        var costRaw = 150.0
        
        if (transcription.contains("bag") || transcription.contains("tote")) {
            name = "Handwoven Eco Tote Bag"
            category = "Accessories > Bags"
            baseMaterial = "Jute & Cotton"
            costRaw = 80.0
        } else if (transcription.contains("pot") || transcription.contains("vase") || transcription.contains("clay")) {
            name = "Artisan Clay Vase"
            category = "Home Decor > Pottery"
            baseMaterial = "Natural Terracotta"
            costRaw = 100.0
        } else if (transcription.contains("jewelry") || transcription.contains("necklace") || transcription.contains("earring")) {
            name = "Boho Statement Jewelry"
            category = "Jewelry > Handmade"
            baseMaterial = "Brass & Beads"
            costRaw = 120.0
        } else if (transcription.isNotBlank() && transcription.length > 5) {
            val words = transcription.split(" ")
            val keyword = words.firstOrNull { it.length > 4 && it != "beautiful" && it != "handcrafted" } ?: "Item"
            name = "Premium Handcrafted ${keyword.replaceFirstChar { it.uppercase() }}"
            baseMaterial = "Sourced Locally"
        }
        
        val desc = "Experience the elegance of this ${name.lowercase()}, carefully handcrafted by skilled artisans. Made from ${baseMaterial.lowercase()}, this piece adds a unique, authentic touch to your life. ${transcription.replaceFirstChar { it.uppercase() }}"
        val price = (costRaw + 150.0 + 40.0) * 1.6

        return """
            {
              "title": "$name",
              "category": "$category",
              "description": "$desc",
              "tags": ["Handmade", "Artisan", "SmallBusiness", "${category.split(">")[0].trim().replace(" ", "")}"],
              "cost_breakdown": { "material": $costRaw, "labour": 150.0, "packaging": 40.0 },
              "suggested_price": $price,
              "readiness_score": 95,
              "missing_fields": []
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
