package com.example.kalax.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.sin

/**
 * Native Jetpack Compose port of VoicePill React component.
 *
 * Features:
 * - Pill-shaped mic button with animated waveform bars when recording
 * - Slide-to-cancel gesture (drag left to cancel)
 * - Elapsed recording time display
 * - Press scale animation
 * - Simulated waveform reactivity
 */
@Composable
fun VoicePill(
    modifier: Modifier = Modifier,
    isRecording: Boolean,
    elapsedMs: Long = 0,
    onStart: () -> Unit,
    onStop: (reason: String) -> Unit,
    accentColor: Color = Color(0xFFF5F5F5),
    iconColor: Color = Color(0xFFA1A1AA),
    background: Color = Color(0xFF27272A),
    size: Int = 28,
    cancelDistance: Int = 64,
    pressScale: Float = 0.95f,
    barCount: Int = 5,
    reach: Int = 8
) {
    var isPressed by remember { mutableStateOf(false) }
    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    val cancelDistPx = with(LocalDensity.current) { cancelDistance.dp.toPx() }.coerceAtLeast(1f)

    // Waveform bar animation
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    // Pulsing ring around mic when recording
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    // Time formatting
    val seconds = (elapsedMs / 1000).toInt()
    val timeString = "%d:%02d".format(seconds / 60, seconds % 60)

    // Scale animation
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) pressScale else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "press_scale"
    )

    // Slide-to-cancel progress
    val slideProgress = (abs(dragOffsetX) / cancelDistPx).coerceIn(0f, 1f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Pulse ring (only when recording)
            if (isRecording) {
                Box(
                    modifier = Modifier
                        .size((size * 3.5f + reach * 2).dp)
                        .scale(pulseScale)
                        .graphicsLayer { alpha = pulseAlpha }
                        .clip(CircleShape)
                        .background(Color.Red.copy(alpha = 0.15f))
                )
            }

            // Main pill
            Surface(
                modifier = Modifier
                    .scale(animatedScale)
                    .then(
                        if (isRecording) {
                            Modifier
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = { onStop("completed") }
                                    )
                                }
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = { dragOffsetX = 0f },
                                        onDrag = { change, offset ->
                                            change.consume()
                                            dragOffsetX += offset.x
                                            // Only cancel on LEFT drag (negative X)
                                            if (dragOffsetX < -cancelDistPx) {
                                                onStop("cancelled")
                                                dragOffsetX = 0f
                                            }
                                        },
                                        onDragEnd = { dragOffsetX = 0f },
                                        onDragCancel = { dragOffsetX = 0f }
                                    )
                                }
                        } else Modifier
                    ),
                shape = RoundedCornerShape(50),
                color = if (isRecording) Color.Red.copy(alpha = 0.15f) else background,
                tonalElevation = 2.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .then(
                            if (isRecording) Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
                            else Modifier
                                .size((size * 3.5f).dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            isPressed = true
                                            tryAwaitRelease()
                                            isPressed = false
                                        },
                                        onTap = { onStart() }
                                    )
                                }
                        )
                ) {
                    if (isRecording) {
                        // Recording dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Waveform bars
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            for (i in 0 until barCount) {
                                val barHeight = (reach * (0.3f + 0.7f * abs(sin(wavePhase + i * 1.2f)))).dp
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(barHeight)
                                        .clip(RoundedCornerShape(1.5.dp))
                                        .background(accentColor.copy(alpha = 0.8f))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Timer
                        Text(
                            text = timeString,
                            color = accentColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        // Idle state — mic icon
                        Icon(
                            Icons.Outlined.Mic,
                            contentDescription = "Record",
                            tint = iconColor,
                            modifier = Modifier.size(size.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Labels
        if (isRecording) {
            // Slide to cancel hint
            Text(
                text = if (slideProgress > 0.3f) "Release to cancel" else "◁ Slide to cancel",
                color = accentColor.copy(alpha = 0.5f - slideProgress * 0.3f),
                fontSize = 12.sp
            )
        } else {
            Text(
                text = "Tap to start recording",
                color = iconColor.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }
    }
}
