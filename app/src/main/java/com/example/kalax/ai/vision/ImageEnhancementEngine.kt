package com.example.kalax.ai.vision

import android.content.Context
import android.graphics.*
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
 * Offline image enhancement engine.
 * Primary: ML Kit Subject Segmentation (background removal)
 * Fallback: Pure Android bitmap manipulation (contrast + brightness + vignette cleanup)
 *
 * Pipeline:
 *   1. Detect subject (product) in the image
 *   2. Segment and remove background → clean white
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
        try {
            enhanceWithMLKit(inputBitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback: pure Android enhancement (no ML Kit needed)
            enhanceWithFallback(inputBitmap)
        }
    }

    /**
     * Primary path: ML Kit Subject Segmentation
     */
    private suspend fun enhanceWithMLKit(inputBitmap: Bitmap): String {
        // ── Stage 1: Detecting ──
        _currentStage.value = EnhancementStage.DETECTING
        _progress.value = 0.05f

        val segmenterOptions = com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions.Builder()
            .enableForegroundConfidenceMask()
            .build()
        val segmenter = com.google.mlkit.vision.segmentation.subject.SubjectSegmentation.getClient(segmenterOptions)
        val inputImage = com.google.mlkit.vision.common.InputImage.fromBitmap(inputBitmap, 0)

        _progress.value = 0.15f

        // ── Stage 2: Segmenting ──
        _currentStage.value = EnhancementStage.SEGMENTING
        _progress.value = 0.20f

        val result = Tasks.await(segmenter.process(inputImage))
        val confidenceMask: FloatBuffer = result.foregroundConfidenceMask
            ?: throw Exception("No foreground mask returned")

        _progress.value = 0.45f

        val width = inputBitmap.width
        val height = inputBitmap.height

        val foregroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val srcPixels = IntArray(width * height)
        inputBitmap.getPixels(srcPixels, 0, width, 0, 0, width, height)

        confidenceMask.rewind()
        val dstPixels = IntArray(width * height)
        for (i in srcPixels.indices) {
            val confidence = if (confidenceMask.hasRemaining()) confidenceMask.get() else 0f
            if (confidence > 0.5f) {
                val alpha = (confidence * 255).toInt().coerceIn(0, 255)
                val srcColor = srcPixels[i]
                dstPixels[i] = Color.argb(alpha, Color.red(srcColor), Color.green(srcColor), Color.blue(srcColor))
            } else {
                dstPixels[i] = Color.TRANSPARENT
            }
        }
        foregroundBitmap.setPixels(dstPixels, 0, width, 0, 0, width, height)

        _progress.value = 0.60f

        // ── Stage 3: Lighting ──
        _currentStage.value = EnhancementStage.LIGHTING
        _progress.value = 0.65f
        val corrected = autoAdjustLighting(foregroundBitmap)
        _progress.value = 0.75f

        // ── Stage 4: Composite ──
        _currentStage.value = EnhancementStage.COMPOSITING
        _progress.value = 0.80f

        val finalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(finalBitmap)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(corrected, 0f, 0f, null)

        _progress.value = 0.95f

        val path = saveBitmap(finalBitmap)
        foregroundBitmap.recycle()
        corrected.recycle()
        finalBitmap.recycle()
        segmenter.close()

        _progress.value = 1f
        _currentStage.value = null
        return path
    }

    /**
     * Fallback path: Pure Android enhancement without ML Kit.
     * Does contrast/brightness boost + sharpening + edge cleanup.
     */
    private suspend fun enhanceWithFallback(inputBitmap: Bitmap): String {
        // ── Stage 1: Detecting ──
        _currentStage.value = EnhancementStage.DETECTING
        _progress.value = 0.10f

        val width = inputBitmap.width
        val height = inputBitmap.height

        // ── Stage 2: "Segmenting" (in fallback we do edge-aware sharpening) ──
        _currentStage.value = EnhancementStage.SEGMENTING
        _progress.value = 0.30f

        // Sharpen using unsharp mask approach
        val sharpened = sharpenBitmap(inputBitmap)
        _progress.value = 0.50f

        // ── Stage 3: Lighting ──
        _currentStage.value = EnhancementStage.LIGHTING
        _progress.value = 0.60f
        val corrected = autoAdjustLighting(sharpened)
        _progress.value = 0.75f

        // ── Stage 4: Composite ──
        _currentStage.value = EnhancementStage.COMPOSITING
        _progress.value = 0.85f

        // Add a subtle white vignette to clean up edges
        val finalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(finalBitmap)
        canvas.drawBitmap(corrected, 0f, 0f, null)

        // Draw radial vignette to soften background edges
        val vignettePaint = Paint()
        val gradient = RadialGradient(
            width / 2f, height / 2f,
            (Math.max(width, height) * 0.65f),
            intArrayOf(Color.TRANSPARENT, Color.TRANSPARENT, Color.argb(60, 255, 255, 255)),
            floatArrayOf(0f, 0.7f, 1f),
            android.graphics.Shader.TileMode.CLAMP
        )
        vignettePaint.shader = gradient
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), vignettePaint)

        _progress.value = 0.95f

        val path = saveBitmap(finalBitmap)
        sharpened.recycle()
        corrected.recycle()
        finalBitmap.recycle()

        _progress.value = 1f
        _currentStage.value = null
        return path
    }

    /**
     * Sharpen bitmap using convolution kernel.
     */
    private fun sharpenBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        val w = bitmap.width
        val h = bitmap.height
        val result = IntArray(w * h)

        // Unsharp mask kernel: sharpen by boosting center, subtracting neighbors
        for (y in 1 until h - 1) {
            for (x in 1 until w - 1) {
                var r = 0; var g = 0; var b = 0
                val center = pixels[y * w + x]
                val cr = Color.red(center); val cg = Color.green(center); val cb = Color.blue(center)

                // Sample 4 neighbors
                val neighbors = intArrayOf(
                    pixels[(y-1) * w + x], pixels[(y+1) * w + x],
                    pixels[y * w + (x-1)], pixels[y * w + (x+1)]
                )
                var nr = 0; var ng = 0; var nb = 0
                for (n in neighbors) { nr += Color.red(n); ng += Color.green(n); nb += Color.blue(n) }
                nr /= 4; ng /= 4; nb /= 4

                // Sharpen: center + 0.4 * (center - average_neighbors)
                val strength = 0.4f
                r = (cr + (strength * (cr - nr)).toInt()).coerceIn(0, 255)
                g = (cg + (strength * (cg - ng)).toInt()).coerceIn(0, 255)
                b = (cb + (strength * (cb - nb)).toInt()).coerceIn(0, 255)

                result[y * w + x] = Color.argb(Color.alpha(center), r, g, b)
            }
        }
        // Copy edges
        for (x in 0 until w) { result[x] = pixels[x]; result[(h-1)*w+x] = pixels[(h-1)*w+x] }
        for (y in 0 until h) { result[y*w] = pixels[y*w]; result[y*w+w-1] = pixels[y*w+w-1] }

        output.setPixels(result, 0, w, 0, 0, w, h)
        return output
    }

    /**
     * Auto brightness/contrast using ColorMatrix.
     */
    private fun autoAdjustLighting(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()

        val brightness = 12f
        val contrast = 1.18f
        val translate = (1f - contrast) * 128f + brightness

        val cm = ColorMatrix(floatArrayOf(
            contrast, 0f, 0f, 0f, translate,
            0f, contrast, 0f, 0f, translate,
            0f, 0f, contrast, 0f, translate,
            0f, 0f, 0f, 1f, 0f
        ))

        // Also boost saturation slightly
        val satMatrix = ColorMatrix()
        satMatrix.setSaturation(1.15f)
        cm.postConcat(satMatrix)

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
