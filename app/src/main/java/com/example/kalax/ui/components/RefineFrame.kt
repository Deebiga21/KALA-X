package com.example.kalax.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Native Jetpack Compose port of the RefineFrame React component.
 *
 * Stages (matching the React version):
 *   queued     → blur:4, sat:0.6, scale:1.04, opacity:0.55
 *   generating → blur:1.5, sat:0.8, scale:1.02, opacity:0.85
 *   refining   → blur:0.5, sat:0.95, scale:1.005, opacity:1.0
 *   complete   → blur:0, sat:1.0, scale:1.0, opacity:1.0
 *   error      → blur:2, sat:0.5, scale:1.0, opacity:0.28
 */
@Composable
fun RefineFrame(
    modifier: Modifier = Modifier,
    status: String, // "queued", "generating", "refining", "complete", "error"
    progress: Float = 0f, // 0..1 real progress from engine
    sweep: Boolean = true,
    showStatus: Boolean = true,
    retryLabel: String = "Retry",
    onRetry: () -> Unit = {},
    radius: Int = 16,
    aspectRatio: Float = 4f / 3f,
    content: @Composable BoxScope.() -> Unit
) {
    // Stage properties (matching React STAGES)
    data class StageProps(val blurDp: Float, val saturation: Float, val scale: Float, val opacity: Float)

    val stageProps = when (status) {
        "queued" -> StageProps(12f, 0.6f, 1.04f, 0.55f)
        "generating" -> StageProps(5f, 0.8f, 1.02f, 0.85f)
        "refining" -> StageProps(1.5f, 0.95f, 1.005f, 1f)
        "complete" -> StageProps(0f, 1f, 1f, 1f)
        "error" -> StageProps(6f, 0.5f, 1f, 0.28f)
        else -> StageProps(5f, 0.8f, 1.02f, 0.85f)
    }

    // Animate transitions between stages (like CSS transition with var(--rf-stage))
    val animatedBlur by animateFloatAsState(
        targetValue = stageProps.blurDp,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "blur"
    )
    val animatedScale by animateFloatAsState(
        targetValue = stageProps.scale,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "scale"
    )
    val animatedOpacity by animateFloatAsState(
        targetValue = stageProps.opacity,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "opacity"
    )
    val animatedSaturation by animateFloatAsState(
        targetValue = stageProps.saturation,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "saturation"
    )

    // Sweep glint animation (matches refine-frame-sweep keyframes)
    val isActive = status in listOf("queued", "generating", "refining")
    val infiniteTransition = rememberInfiniteTransition(label = "sweep")
    val sweepPosition by infiniteTransition.animateFloat(
        initialValue = 1.3f,
        targetValue = -1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep_pos"
    )

    // Queued pulsing (matches refine-frame-wait keyframes)
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Spinner rotation
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(radius.dp))
            .background(Color(0xFF27272A))
    ) {
        // ── Media layer (blur + scale + opacity + saturation) ──
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    this.alpha = if (status == "queued") pulseAlpha else animatedOpacity
                    this.scaleX = animatedScale
                    this.scaleY = animatedScale
                }
                .then(
                    if (animatedBlur > 0.5f) Modifier.blur(animatedBlur.dp)
                    else Modifier
                )
        ) {
            content()
        }

        // ── Sweep glint overlay ──
        if (sweep && isActive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        drawContent()
                        val center = size.width * sweepPosition
                        val glintWidth = size.width * 0.24f
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.07f),
                                    Color.White.copy(alpha = 0.14f),
                                    Color.White.copy(alpha = 0.07f),
                                    Color.Transparent
                                ),
                                startX = center - glintWidth,
                                endX = center + glintWidth
                            )
                        )
                    }
            )
        }

        // ── Progress bar at bottom ──
        if (isActive && progress > 0f) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = tween(300, easing = FastOutSlowInEasing),
                    label = "progress_bar"
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .background(Color.White.copy(alpha = 0.6f))
                )
            }
        }

        // ── Status chip (bottom-left, matching refine-frame__chip) ──
        if (showStatus && status != "complete") {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
            ) {
                Surface(
                    color = Color(0xFF27272A).copy(alpha = 0.72f),
                    shape = RoundedCornerShape(13.dp),
                    tonalElevation = 0.dp
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        // Spinner icon (matches refine-frame__mark[data-kind='spin'])
                        if (isActive) {
                            Box(modifier = Modifier.size(13.dp).graphicsLayer { rotationZ = spinAngle }) {
                                CircularProgressIndicator(
                                    modifier = Modifier.fillMaxSize(),
                                    color = Color(0xFFF5F5F5).copy(alpha = 0.8f),
                                    strokeWidth = 1.5.dp
                                )
                            }
                        } else if (status == "error") {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(13.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Label
                        val labelText = when (status) {
                            "queued" -> "Queued"
                            "generating" -> "Generating"
                            "refining" -> "Refining"
                            "complete" -> "Ready"
                            "error" -> "Failed"
                            else -> "Processing"
                        }
                        Text(
                            text = labelText,
                            color = Color(0xFFF5F5F5),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // ── Retry button (center, on error) ──
        if (status == "error") {
            Surface(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF27272A).copy(alpha = 0.85f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onRetry() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFFF5F5F5), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(retryLabel, color = Color(0xFFF5F5F5), fontSize = 13.sp)
                }
            }
        }
    }
}
