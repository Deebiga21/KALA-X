package com.example.kalax.domain.engine

class PricingEngine {
    fun calculatePrice(
        materialCost: Double,
        labourCost: Double,
        packagingCost: Double,
        otherCost: Double
    ): Double {
        val totalCost = materialCost + labourCost + packagingCost + otherCost
        val margin = totalCost * 0.4 // 40% margin
        return totalCost + margin
    }
}
