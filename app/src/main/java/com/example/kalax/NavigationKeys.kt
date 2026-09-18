package com.example.kalax

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object Splash : NavKey
@Serializable data object Onboarding : NavKey
@Serializable data object Login : NavKey

@Serializable data object Home : NavKey
@Serializable data object Catalog : NavKey
@Serializable data object Insights : NavKey
@Serializable data object Profile : NavKey
@Serializable data object Main : NavKey

// Product Creation Flow
@Serializable data object Capture : NavKey
@Serializable data object Enhance : NavKey
@Serializable data object VoiceCatalog : NavKey
@Serializable data object Pricing : NavKey
@Serializable data object Readiness : NavKey
@Serializable data object FinalListing : NavKey
@Serializable data class ProductDetail(val productId: String) : NavKey
