package com.example.kalax.data.api

import retrofit2.http.*
import okhttp3.MultipartBody

// DTOs
data class HomeResponse(val success: Boolean, val data: HomeData)
data class HomeData(val user: UserDto, val stats: StatsDto, val recent_products: List<RecentProductDto>, val recent_activities: List<String>)
data class UserDto(val id: Int, val name: String)
data class StatsDto(val products_created: Int, val published: Int, val drafts: Int, val total_sales: Int, val commerce_score: Int)
data class RecentProductDto(val id: Int, val name: String, val image: String?)

data class ProductsResponse(val success: Boolean, val data: List<ProductDto>)
data class ProductResponse(val success: Boolean, val data: ProductDto)
data class ProductDto(
    val id: Int, val offline_id: String?, val name: String, val category: String, val material: String,
    val original_image: String?, val enhanced_image: String?,
    val original_language: String?, val transcription: String?, val translation: String?,
    val description: String?, val seo_title: String?, val keywords: String?,
    val raw_material_cost: Float, val labour_cost: Float, val packaging_cost: Float, val other_cost: Float, val total_cost: Float,
    val market_min: Float?, val market_max: Float?, val recommended_price: Float?, val pricing_confidence: Int?,
    val commerce_score: Int, val dimensions: String?, val weight: String?, val status: String
)

data class SyncProductRequest(
    val offline_id: String,
    val name: String,
    val category: String,
    val material: String,
    val description: String?,
    val seo_title: String?,
    val keywords: String?,
    val raw_material_cost: Float,
    val labour_cost: Float,
    val packaging_cost: Float,
    val other_cost: Float,
    val total_cost: Float,
    val recommended_price: Float,
    val pricing_confidence: Int,
    val commerce_score: Int,
    val dimensions: String?,
    val status: String
)

data class CreateProductRequest(val name: String, val category: String, val material: String, val location: String? = null, val language: String? = null)
data class UpdateProductRequest(val name: String? = null, val category: String? = null, val material: String? = null, val description: String? = null, val keywords: String? = null, val dimensions: String? = null, val weight: String? = null)

data class BaseResponse(val success: Boolean, val data: Any? = null)

data class ProcessingStatusResponse(val success: Boolean, val data: ProcessingData)
data class ProcessingData(val product_id: Int, val steps: List<ProcessingStep>)
data class ProcessingStep(val step: String, val status: String, val message: String?)

interface KalaApi {
    @GET("api/home/")
    suspend fun getHome(): HomeResponse

    @GET("api/products/")
    suspend fun getProducts(): ProductsResponse

    @POST("api/products/")
    suspend fun createProduct(@Body request: CreateProductRequest): ProductResponse

    @GET("api/products/{id}")
    suspend fun getProduct(@Path("id") id: Int): ProductResponse

    @PUT("api/products/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body request: UpdateProductRequest): ProductResponse

    @Multipart
    @POST("api/products/{id}/image")
    suspend fun uploadImage(@Path("id") id: Int, @Part file: MultipartBody.Part): BaseResponse

    @POST("api/products/{id}/enhance-image")
    suspend fun enhanceImage(@Path("id") id: Int): BaseResponse

    @Multipart
    @POST("api/products/{id}/voice")
    suspend fun processVoice(@Path("id") id: Int, @Query("language") language: String, @Part file: MultipartBody.Part? = null): BaseResponse

    @POST("api/products/{id}/generate-catalog")
    suspend fun generateCatalog(@Path("id") id: Int): BaseResponse

    @POST("api/products/{id}/calculate-price")
    suspend fun calculatePrice(@Path("id") id: Int): BaseResponse

    @POST("api/products/{id}/commerce-score")
    suspend fun getCommerceScore(@Path("id") id: Int): BaseResponse

    @POST("api/products/{id}/publish")
    suspend fun publishProduct(@Path("id") id: Int): BaseResponse

    @GET("api/products/{id}/processing")
    suspend fun getProcessingStatus(@Path("id") id: Int): ProcessingStatusResponse

    @POST("api/sync/products")
    suspend fun syncProducts(@Body products: List<SyncProductRequest>): BaseResponse
}
