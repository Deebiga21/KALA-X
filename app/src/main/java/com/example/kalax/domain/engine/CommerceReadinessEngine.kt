package com.example.kalax.domain.engine

import com.example.kalax.data.local.entity.CatalogItem

class CommerceReadinessEngine {
    fun calculateScore(item: CatalogItem): Int {
        var score = 0
        
        // Image Quality (20%)
        if (item.processedPhotoUri != null || item.rawPhotoUri != null) {
            score += 20
        }
        
        // Catalog Quality (20%)
        if (item.title.isNotBlank()) score += 10
        if (item.description.isNotBlank()) score += 10
        
        // Pricing (20%)
        if (item.suggestedPrice > 0.0) score += 20
        
        // Category (15%)
        if (item.category.isNotBlank() && item.category != "Uncategorized") score += 15
        
        // Keywords (10%)
        if (item.tags.isNotBlank()) score += 10
        
        // Completeness (15%) - assume other fields like materialCost, labourCost are set
        if (item.materialCost > 0.0 || item.labourCost > 0.0) score += 15
        
        return score
    }
}
