package com.example.kalax.ui.product

import androidx.compose.animation.*
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
import com.example.kalax.ai.vision.EnhancementStage

@Composable
fun EnhanceScreen(
    viewModel: ProductViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val draft by viewModel.draft.collectAsState()
    val pipelineState by viewModel.pipelineState.collectAsState()
    val isProcessing = pipelineState is PipelineState.Enhancing
    val isDone = draft.enhancedImageUri != null && draft.enhancedImageUri != draft.imageUri && !isProcessing

    // Real-time stage and progress from the offline engine
    val currentStage by viewModel.enhancementStage.collectAsState()
    val engineProgress by viewModel.enhancementProgress.collectAsState()

    // Map engine stage to RefineFrame status
    val refineStatus = when {
        isDone -> "complete"
        currentStage == EnhancementStage.DETECTING -> "queued"
        currentStage == EnhancementStage.SEGMENTING -> "generating"
        currentStage == EnhancementStage.LIGHTING -> "refining"
        currentStage == EnhancementStage.COMPOSITING -> "refining"
        isProcessing -> "generating"
        else -> if (isDone) "complete" else "queued"
    }

    // Derive which steps are complete based on the real engine stage
    val stageIndex = when (currentStage) {
        EnhancementStage.DETECTING -> 0
        EnhancementStage.SEGMENTING -> 1
        EnhancementStage.LIGHTING -> 2
        EnhancementStage.COMPOSITING -> 3
        null -> if (isDone) 4 else -1
    }

    // Auto-start enhancement when this screen opens
    LaunchedEffect(Unit) {
        if (!isDone && !isProcessing && draft.imageUri != null) {
            viewModel.enhanceImage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Enhancing Your Image", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("From raw to professional", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // RefineFrame — fully synced to real engine stages
            com.example.kalax.ui.components.RefineFrame(
                status = refineStatus,
                progress = engineProgress,
                sweep = isProcessing,
                showStatus = true,
                radius = 16,
                aspectRatio = 4f / 3f,
                onRetry = { viewModel.enhanceImage() }
            ) {
                val currentImageUri = if (isDone && draft.enhancedImageUri != null) draft.enhancedImageUri else draft.imageUri
                
                if (currentImageUri != null) {
                    AsyncImage(
                        model = java.io.File(currentImageUri),
                        contentDescription = "Enhancement Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Real-time progress bar below the frame
            if (isProcessing) {
                LinearProgressIndicator(
                    progress = { engineProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = currentStage?.label ?: "Processing...",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(engineProgress * 100).toInt()}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Progress steps — synced to real engine stages
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                EnhancementStage.entries.forEachIndexed { index, stage ->
                    val isComplete = index < stageIndex
                    val isActive = index == stageIndex && isProcessing
                    EnhancementProgressItem(
                        text = stage.label,
                        description = if (isActive) stage.description else null,
                        isComplete = isComplete,
                        isActive = isActive
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onNext,
                enabled = isDone,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("Use Enhanced Image", color = if (isDone) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { viewModel.enhanceImage() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = isDone
            ) {
                Text("Try Again", color = if (isDone) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun EnhancementProgressItem(text: String, description: String?, isComplete: Boolean, isActive: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        if (isComplete) {
            Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        } else if (isActive) {
            Box(modifier = Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
            }
        } else {
            Box(modifier = Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(0.3f))
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text,
                color = when {
                    isComplete -> MaterialTheme.colorScheme.primary
                    isActive -> MaterialTheme.colorScheme.onBackground
                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f)
                },
                fontSize = 14.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
            )
            if (description != null) {
                Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
            }
        }
    }
}
