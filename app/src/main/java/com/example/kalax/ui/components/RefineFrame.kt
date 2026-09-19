package com.example.kalax.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RefineFrame(
    modifier: Modifier = Modifier,
    status: String, // "working", "done", "error"
    sweep: Boolean = true,
    showStatus: Boolean = true,
    retryLabel: String = "Retry",
    onRetry: () -> Unit = {},
    radius: Int = 16,
    aspectRatio: Float = 4f / 3f,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sweep")
    val sweepPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep_position"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(radius.dp))
            .background(Color(0xFF27272A))
    ) {
        // Main content (image) goes here
        content()

        // Sweep animation overlay
        if (sweep && status == "working") {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        drawContent()
                        val y = size.height * sweepPosition
                        
                        // Draw a sweeping scanner gradient
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.2f), Color.Transparent),
                                startY = y - 60f,
                                endY = y + 60f
                            ),
                            topLeft = Offset(0f, y - 60f),
                            size = Size(size.width, 120f)
                        )
                        // Solid scanner line
                        drawLine(
                            color = Color.White.copy(alpha = 0.6f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 3f
                        )
                    }
            )
        }

        // Status overlay (floating pill)
        if (showStatus) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                if (status == "working") {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Refining...", color = Color.White, fontSize = 12.sp)
                        }
                    }
                } else if (status == "error") {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text("Failed to refine", color = Color(0xFFEF4444), fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            TextButton(
                                onClick = onRetry,
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.height(24.dp).widthIn(min = 60.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(retryLabel, color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
