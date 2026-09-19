package com.example.kalax.ai.vision

import android.content.Context
import android.graphics.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.FloatBuffer
import java.util.UUID

/**
 * Enhancement stage reported to the UI for real-time progress tracking.
 */
enum class EnhancementStage(val label: String, val description: String) {
    DETECTING("Detecting product", "Analyzing the image to find your product..."),
    SEGMENTING("Removing background", "Separating your product from the background..."),
    LIGHTING("Correcting lighting", "Adjusting brightness and contrast..."),
    COMPOSITING("Preparing commerce image", "Creating a clean, professional listing photo...")
}

/**
 * Offline image enhancement engine powered by ML Kit Subject Segmentation.
 * Runs entirely on-device — no internet required.
 *
 * Pipeline:
 *   1. Detect subject (product) in the image
 *   2. Segment and remove background → transparent or clean white
 *   3. Auto-adjust brightness/contrast
 *   4. Composite onto a clean white background
 */
class ImageEnhancementEngine(private val context: Context) {

    private val _currentStage = MutableStateFlow<EnhancementStage?>(null)
    val currentStage: StateFlow<EnhancementStage?> = _currentStage.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    /**
     * Run the full enhancement pipeline on [inputBitmap].
     * Returns the path to the enhanced image file saved in cache.
     */
    suspend fun enhance(inputBitmap: Bitmap): String = withContext(Dispatchers.Default) {

        // ── Stage 1: Detecting product ──────────────────────────────
        _currentStage.value = EnhancementStage.DETECTING
        _progress.value = 0.05f

        val segmenterOptions = SubjectSegmenterOptions.Builder()
            .enableForegroundConfidenceMask()
            .build()
        val segmenter = SubjectSegmentation.getClient(segmenterOptions)
        val inputImage = InputImage.fromBitmap(inputBitmap, 0)

        _progress.value = 0.15f

        // ── Stage 2: Segmenting / Background Removal ────────────────
        _currentStage.value = EnhancementStage.SEGMENTING
        _progress.value = 0.20f

        val result = Tasks.await(segmenter.process(inputImage))
        val confidenceMask: FloatBuffer = result.foregroundConfidenceMask
            ?: run {
                // If segmentation fails, fall back to returning the original
                _progress.value = 1f
                _currentStage.value = null
                return@withContext saveBitmap(inputBitmap)
            }

        _progress.value = 0.45f

        val width = inputBitmap.width
        val height = inputBitmap.height

        // Create foreground-only bitmap with transparent background
        val foregroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val srcPixels = IntArray(width * height)
        inputBitmap.getPixels(srcPixels, 0, width, 0, 0, width, height)

        confidenceMask.rewind()
        val dstPixels = IntArray(width * height)
        for (i in srcPixels.indices) {
            val confidence = if (confidenceMask.hasRemaining()) confidenceMask.get() else 0f
            if (confidence > 0.5f) {
                // Keep the foreground pixel, scale alpha by confidence
                val alpha = (confidence * 255).toInt().coerceIn(0, 255)
                val srcColor = srcPixels[i]
                dstPixels[i] = Color.argb(
                    alpha,
                    Color.red(srcColor),
                    Color.green(srcColor),
                    Color.blue(srcColor)
                )
            } else {
                dstPixels[i] = Color.TRANSPARENT
            }
        }
        foregroundBitmap.setPixels(dstPixels, 0, width, 0, 0, width, height)

        _progress.value = 0.60f

        // ── Stage 3: Lighting correction ────────────────────────────
        _currentStage.value = EnhancementStage.LIGHTING
        _progress.value = 0.65f

        val correctedForeground = autoAdjustLighting(foregroundBitmap)

        _progress.value = 0.75f

        // ── Stage 4: Composite onto clean white background ──────────
        _currentStage.value = EnhancementStage.COMPOSITING
        _progress.value = 0.80f

        val finalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(finalBitmap)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(correctedForeground, 0f, 0f, null)

        _progress.value = 0.95f

        // Save to cache
        val path = saveBitmap(finalBitmap)

        // Cleanup
        foregroundBitmap.recycle()
        correctedForeground.recycle()
        finalBitmap.recycle()
        segmenter.close()

        _progress.value = 1f
        _currentStage.value = null

        path
    }

    /**
     * Simple auto-brightness/contrast adjustment using a ColorMatrix.
     */
    private fun autoAdjustLighting(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()

        // Slightly increase brightness and contrast
        val brightness = 10f
        val contrast = 1.15f
        val translate = (1f - contrast) * 128f + brightness

        val cm = ColorMatrix(
            floatArrayOf(
                contrast, 0f, 0f, 0f, translate,
                0f, contrast, 0f, 0f, translate,
                0f, 0f, contrast, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            )
        )

        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return output
    }

    private fun saveBitmap(bitmap: Bitmap): String {
        val file = File(context.cacheDir, "enhanced_${UUID.randomUUID()}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file.absolutePath
    }
}
