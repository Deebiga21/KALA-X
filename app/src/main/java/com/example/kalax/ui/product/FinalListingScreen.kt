package com.example.kalax.ui.product

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

// ─── Platform Adapter Definitions ─────────────────────────────────────

data class PlatformAdapter(
    val name: String,
    val emoji: String,
    val description: String,
    val formatDescription: (ProductDraft) -> String
)

private val platformAdapters = listOf(
    PlatformAdapter(
        name = "WhatsApp",
        emoji = "💬",
        description = "Short, conversational, punchy",
        formatDescription = { draft ->
            buildString {
                append("🛍 *${draft.name.ifEmpty { "Handcrafted Product" }}*\n\n")
                append("${draft.description.take(200)}\n\n")
                if (draft.recommendedPrice > 0) append("💰 Price: ₹${draft.recommendedPrice}\n")
                append("📦 Material: ${draft.material.ifEmpty { "Premium Quality" }}\n\n")
                append("📩 DM to order! Limited stock.")
            }
        }
    ),
    PlatformAdapter(
        name = "Instagram",
        emoji = "📸",
        description = "Emoji-rich, hashtag-heavy caption",
        formatDescription = { draft ->
            buildString {
                append("✨ ${draft.name.ifEmpty { "Handcrafted Product" }} ✨\n\n")
                append("${draft.description.take(300)}\n\n")
                if (draft.recommendedPrice > 0) append("💵 ₹${draft.recommendedPrice}\n\n")
                append("🔗 Link in bio to order!\n\n")
                val tags = draft.keywords.split(",").map { it.trim() }.filter { it.isNotEmpty() }.take(8).joinToString(" ") { "#${it.replace(" ", "").replace("#", "")}" }
                append(tags)
                append("\n#handmade #artisan #shoplocal #madewithlove #smallbusiness")
            }
        }
    ),
    PlatformAdapter(
        name = "Amazon/Flipkart",
        emoji = "📦",
        description = "Structured bullet points, SEO-optimized",
        formatDescription = { draft ->
            buildString {
                append("${draft.name.ifEmpty { "Handcrafted Product" }}\n\n")
                append("PRODUCT HIGHLIGHTS:\n")
                append("• Material: ${draft.material.ifEmpty { "Premium Quality" }}\n")
                if (draft.dimensions.isNotEmpty()) append("• Dimensions: ${draft.dimensions}\n")
                append("• Handcrafted with care by skilled artisans\n")
                append("• Perfect for gifting and home décor\n\n")
                append("DESCRIPTION:\n")
                append(draft.description)
                if (draft.recommendedPrice > 0) append("\n\nMRP: ₹${draft.recommendedPrice}")
            }
        }
    ),
    PlatformAdapter(
        name = "Meesho",
        emoji = "🛒",
        description = "Value-focused, reseller-friendly",
        formatDescription = { draft ->
            buildString {
                append("${draft.name.ifEmpty { "Handcrafted Product" }}\n\n")
                append("${draft.description.take(250)}\n\n")
                append("Key Features:\n")
                append("✅ Handmade & Unique\n")
                append("✅ ${draft.material.ifEmpty { "Premium Quality Material" }}\n")
                append("✅ Perfect for gifting\n")
                if (draft.recommendedPrice > 0) append("\nPrice: ₹${draft.recommendedPrice}")
            }
        }
    )
)

// ─── Image Asset Sizes ────────────────────────────────────────────────

data class ImageAsset(
    val label: String,
    val platformHint: String,
    val width: Int,
    val height: Int
)

private val imageAssets = listOf(
    ImageAsset("1:1 Square", "Instagram, Facebook", 1080, 1080),
    ImageAsset("4:3 Standard", "Catalogs, Meesho", 1200, 900),
    ImageAsset("16:9 Banner", "Twitter, FB Cover", 1200, 675)
)

@Composable
fun FinalListingScreen(
    viewModel: ProductViewModel,
    onBack: () -> Unit,
    onSaveDraft: () -> Unit,
    onPublish: () -> Unit
) {
    val draft by viewModel.draft.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    var isPublishing by remember { mutableStateOf(false) }
    var showExportSheet by remember { mutableStateOf(false) }
    var showAssetSheet by remember { mutableStateOf(false) }
    var copiedPlatform by remember { mutableStateOf<String?>(null) }
    var generatingAssets by remember { mutableStateOf(false) }

    // Magic Link URL
    val shareUrl = "http://192.168.31.59:8000/share/1" // In production, use real product ID

    LaunchedEffect(isPublishing) {
        if (isPublishing) {
            delay(1500)
            viewModel.publishDraft()
            onPublish()
        }
    }

    LaunchedEffect(copiedPlatform) {
        if (copiedPlatform != null) {
            delay(2000)
            copiedPlatform = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        // ── Header ──
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Final Preview", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // ── Product Card ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Box(modifier = Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.onSurfaceVariant)) {
                        val displayImage = draft.enhancedImageUri ?: draft.imageUri
                        if (displayImage != null) {
                            AsyncImage(
                                model = java.io.File(displayImage),
                                contentDescription = "Product Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(draft.name.ifEmpty { "Product Name" }, color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text("₹${draft.recommendedPrice}", color = MaterialTheme.colorScheme.primary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("${draft.category} • ${draft.material}", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(draft.description.ifEmpty { "Description here..." }, color = Color.DarkGray, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Magic Link Card ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🔗 Magic Link", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
                    Text("Share a beautiful product page instantly", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = shareUrl,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(shareUrl))
                            copiedPlatform = "link"
                        }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Check out my product! 🛍\n\n${draft.name}\n$shareUrl")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share via"))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                    ) {
                        Icon(Icons.Outlined.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share via WhatsApp", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Smart Platform Adapters ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🎯 Export to Platform", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
                    Text("One-tap formatted listing for each platform", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    platformAdapters.forEach { adapter ->
                        val formattedText = adapter.formatDescription(draft)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(formattedText))
                                    copiedPlatform = adapter.name
                                }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(adapter.emoji, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(adapter.name, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
                                Text(adapter.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            AnimatedContent(targetState = copiedPlatform == adapter.name, label = "copy") { isCopied ->
                                if (isCopied) {
                                    Text("Copied! ✓", fontSize = 12.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                                } else {
                                    Text("Copy", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                        if (adapter != platformAdapters.last()) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Platform Image Assets ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📐 Image Assets", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
                    Text("Auto-sized for each platform", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    imageAssets.forEach { asset ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📐", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(asset.label, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
                                Text("${asset.width}×${asset.height} • ${asset.platformHint}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            generatingAssets = true
                            scope.launch {
                                val imagePath = draft.enhancedImageUri ?: draft.imageUri
                                if (imagePath != null) {
                                    generateImageAssets(context, imagePath, imageAssets)
                                }
                                generatingAssets = false
                                // Share all generated assets
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND_MULTIPLE
                                    type = "image/*"
                                    val uris = ArrayList<Uri>()
                                    imageAssets.forEach { asset ->
                                        val file = File(context.cacheDir, "asset_${asset.width}x${asset.height}.png")
                                        if (file.exists()) {
                                            uris.add(androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.provider", file))
                                        }
                                    }
                                    putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Image Assets"))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !generatingAssets,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (generatingAssets) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generating...", color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Text("Generate & Share All Sizes", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Bottom Action Row ──
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(
                onClick = { onSaveDraft() },
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text("Save Draft", color = MaterialTheme.colorScheme.onBackground)
            }
            Button(
                onClick = { isPublishing = true },
                modifier = Modifier.weight(1f).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isPublishing) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Publish Now", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─── Image Asset Generator ────────────────────────────────────────────

private suspend fun generateImageAssets(context: Context, imagePath: String, assets: List<ImageAsset>) {
    withContext(Dispatchers.Default) {
        val sourceBitmap = BitmapFactory.decodeFile(imagePath) ?: return@withContext

        for (asset in assets) {
            val targetW = asset.width
            val targetH = asset.height

            // Create white canvas at target size
            val result = Bitmap.createBitmap(targetW, targetH, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(result)
            canvas.drawColor(android.graphics.Color.WHITE)

            // Scale source to fit inside target (contain, centered)
            val scaleX = targetW.toFloat() / sourceBitmap.width
            val scaleY = targetH.toFloat() / sourceBitmap.height
            val scale = minOf(scaleX, scaleY) * 0.85f // 85% fill, leaving padding

            val scaledW = (sourceBitmap.width * scale).toInt()
            val scaledH = (sourceBitmap.height * scale).toInt()
            val left = (targetW - scaledW) / 2f
            val top = (targetH - scaledH) / 2f

            val destRect = RectF(left, top, left + scaledW, top + scaledH)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            canvas.drawBitmap(sourceBitmap, null, destRect, paint)

            // Save to cache
            val outFile = File(context.cacheDir, "asset_${targetW}x${targetH}.png")
            FileOutputStream(outFile).use { out ->
                result.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            result.recycle()
        }
        sourceBitmap.recycle()
    }
}
