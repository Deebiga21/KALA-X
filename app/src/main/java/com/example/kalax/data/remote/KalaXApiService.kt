package com.example.kalax.data.remote

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import com.example.kalax.data.local.entity.CatalogItem

data class ProductResponse(
    val id: Long,
    val title: String? = null,
    val category: String? = null,
    val description: String? = null,
    val tags: String? = null,
    val rawPhotoUri: String? = null,
    val processedPhotoUri: String? = null,
    val materialCost: Double = 0.0,
    val labourCost: Double = 0.0,
    val packagingCost: Double = 0.0,
    val otherCost: Double = 0.0,
    val suggestedPrice: Double = 0.0,
    val readinessScore: Int = 0,
    val dimensions: String? = null,
    val transcription: String? = null,
    val status: String? = null
)

fun ProductResponse.toCatalogItem(): CatalogItem {
    return CatalogItem(
        id = this.id,
        title = this.title ?: "",
        category = this.category ?: "",
        description = this.description ?: "",
        tags = this.tags ?: "",
        rawPhotoUri = this.rawPhotoUri,
        processedPhotoUri = this.processedPhotoUri,
        materialCost = this.materialCost,
        labourCost = this.labourCost,
        packagingCost = this.packagingCost,
        otherCost = this.otherCost,
        suggestedPrice = this.suggestedPrice,
        readinessScore = this.readinessScore,
        dimensions = this.dimensions ?: "",
        transcription = this.transcription ?: "",
        status = this.status ?: "Draft"
    )
}

data class VoiceRequest(val text: String?, val audioUri: String?)

data class AnalyzeImageRequest(val labels: String)

interface KalaXApiService {
    @POST("/api/products")
    suspend fun createProduct(): ProductResponse

    @Multipart
    @POST("/api/products/{id}/image")
    suspend fun uploadImage(@Path("id") id: Long, @Part image: MultipartBody.Part): ProductResponse

    @POST("/api/products/{id}/analyze-image")
    suspend fun analyzeImage(@Path("id") id: Long, @Body request: AnalyzeImageRequest): ProductResponse

    @POST("/api/products/{id}/enhance-image")
    suspend fun enhanceImage(@Path("id") id: Long): ProductResponse

    @POST("/api/products/{id}/voice")
    suspend fun processVoice(@Path("id") id: Long, @Body request: VoiceRequest): ProductResponse

    @POST("/api/products/{id}/generate-catalog")
    suspend fun generateCatalog(@Path("id") id: Long): ProductResponse

    @POST("/api/products/{id}/pricing")
    suspend fun calculatePricing(@Path("id") id: Long): ProductResponse

    @POST("/api/products/{id}/readiness")
    suspend fun checkReadiness(@Path("id") id: Long): ProductResponse

    @POST("/api/products/{id}/publish")
    suspend fun publishProduct(@Path("id") id: Long): ProductResponse

    @GET("/api/products")
    suspend fun getProducts(): List<ProductResponse>

    @GET("/api/products/{id}")
    suspend fun getProduct(@Path("id") id: Long): ProductResponse
    
    @POST("/api/products")
    suspend fun updateProduct(@Body item: CatalogItem): ProductResponse // using create endpoint for update as well for sync, or just a PUT
}
