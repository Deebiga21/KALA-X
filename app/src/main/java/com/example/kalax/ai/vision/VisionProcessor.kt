package com.example.kalax.ai.vision

import android.graphics.Bitmap

interface VisionProcessor {
    suspend fun removeBackground(inputBitmap: Bitmap): Bitmap
}

class MediaPipeVisionProcessor : VisionProcessor {
    override suspend fun removeBackground(inputBitmap: Bitmap): Bitmap {
        // Mock implementation for MediaPipe Background Removal / Edge Cleanup
        // In reality, you would use ImageSegmenter.createFromFile(...) here
        return inputBitmap 
    }
}
