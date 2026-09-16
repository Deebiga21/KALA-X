package com.example.kalax.ui.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

@Composable
fun EnhanceScreen(
    viewModel: ProductViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val draft by viewModel.draft.collectAsState()
    val pipelineState by viewModel.pipelineState.collectAsState()
    val isDone = pipelineState == "ImageProcessed" || pipelineState == "TranscribingAudio" || pipelineState == "GeneratingCatalog" || pipelineState == "CatalogGenerated"
    val isProcessing = pipelineState == "ProcessingImage"

    val progress = when {
        isDone -> 1f
        isProcessing -> 0.6f
        else -> 0f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020617))
            .safeDrawingPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Enhancing Your Image", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("From raw to professional", color = Color.Gray, fontSize = 12.sp)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Before", color = Color.Gray, fontSize = 14.sp)
                Text("After", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Original Image
                Box(modifier = Modifier.weight(1f).aspectRatio(1f).clip(RoundedCornerShape(16.dp)).background(Color.DarkGray)) {
                    if (draft.imageUri != null) {
                        AsyncImage(
                            model = draft.imageUri,
                            contentDescription = "Original",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                
                // Enhanced Image
                Box(modifier = Modifier.weight(1f).aspectRatio(1f).clip(RoundedCornerShape(16.dp)).background(Color(0xFF1E293B))) {
                    if (isDone && draft.enhancedImageUri != null) {
                        AsyncImage(
                            model = draft.enhancedImageUri,
                            contentDescription = "Enhanced",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (!isDone) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF06B6D4))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Progress text
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ProgressItem("Detecting product", progress >= 0.3f)
                ProgressItem("Removing background", progress >= 0.6f)
                ProgressItem("Correcting lighting", progress >= 0.9f)
                ProgressItem("Preparing commerce image", progress >= 1f)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onNext,
                enabled = isDone,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF06B6D4),
                    disabledContainerColor = Color(0xFF1E293B)
                )
            ) {
                Text("Use Enhanced Image", color = if (isDone) Color.Black else Color.Gray, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { viewModel.enhanceImage() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = isDone
            ) {
                Text("Try Again", color = if (isDone) Color.White else Color.Gray)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun ProgressItem(text: String, isDone: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        if (isDone) {
            Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = Color(0xFF06B6D4), modifier = Modifier.size(20.dp))
        } else {
            Box(modifier = Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(strokeWidth = 2.dp, color = Color.Gray, modifier = Modifier.size(12.dp))
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, color = if (isDone) Color.White else Color.Gray, fontSize = 14.sp)
    }
}
