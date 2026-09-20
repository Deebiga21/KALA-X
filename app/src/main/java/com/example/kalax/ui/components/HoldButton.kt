package com.example.kalax.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

@Composable
fun HoldButton(
    modifier: Modifier = Modifier,
    text: String,
    doneLabel: String = "Done",
    backgroundColor: Color = Color(0xFF27272A),
    fillColor: Color = Color(0xFF84CC16),
    textColor: Color = Color(0xFFF5F5F5),
    fillTextColor: Color = Color(0xFFFFFFFF),
    radius: Dp = 14.dp,
    holdTime: Long = 2000L,
    releaseTime: Int = 200,
    pressScale: Float = 0.97f,
    wave: Boolean = true,
    glow: Boolean = true,
    resetAfter: Long = 1200L,
    onHold: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isPressed by remember { mutableStateOf(false) }
    var isDone by remember { mutableStateOf(false) }
    
    val fillProgress = remember { Animatable(0f) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed && !isDone) pressScale else 1f,
        animationSpec = tween(150),
        label = "scale"
    )

    // Wave animation state
    val waveOffset = remember { Animatable(0f) }
    LaunchedEffect(isPressed) {
        if (isPressed && wave) {
            waveOffset.animateTo(
                targetValue = 100f,
                animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                    animation = tween(1000, easing = androidx.compose.animation.core.LinearEasing)
                )
            )
        } else {
            waveOffset.snapTo(0f)
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            val result = fillProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(holdTime.toInt(), easing = androidx.compose.animation.core.LinearEasing)
            )
            if (result.endReason == androidx.compose.animation.core.AnimationEndReason.Finished) {
                isDone = true
                isPressed = false
                onHold()
                
                if (resetAfter > 0) {
                    delay(resetAfter)
                    isDone = false
                    fillProgress.snapTo(0f)
                }
            }
        } else if (!isDone) {
            fillProgress.animateTo(
                targetValue = 0f,
                animationSpec = tween(releaseTime)
            )
        }
    }

    val shape = RoundedCornerShape(radius)

    Box(
        modifier = modifier
            .scale(scale)
            .then(if (glow && fillProgress.value > 0f && !isDone) Modifier.shadow(
                elevation = (10f * fillProgress.value).dp, 
                shape = shape, 
                spotColor = fillColor, 
                ambientColor = fillColor
            ) else Modifier)
            .clip(shape)
            .background(backgroundColor)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        if (!isDone) {
                            isPressed = true
                            try {
                                awaitRelease()
                            } finally {
                                isPressed = false
                            }
                        }
                    }
                )
            }
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            },
        contentAlignment = Alignment.Center
    ) {
        // Base text
        Text(
            text = if (isDone) doneLabel else text,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp)
        )

        // Fill layer
        if (fillProgress.value > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithCache {
                        val path = Path()
                        val fillWidth = size.width * fillProgress.value
                        
                        if (wave && fillProgress.value < 1f && fillProgress.value > 0f) {
                            path.moveTo(0f, 0f)
                            path.lineTo(fillWidth, 0f)
                            // Draw vertical wave
                            val waveAmp = 6.dp.toPx()
                            val step = size.height / 20
                            for (i in 0..20) {
                                val y = i * step
                                val waveShift = sin((y / size.height) * Math.PI * 4 + waveOffset.value) * waveAmp
                                path.lineTo(fillWidth + waveShift.toFloat(), y)
                            }
                            path.lineTo(0f, size.height)
                            path.close()
                        } else {
                            path.addRect(Rect(0f, 0f, fillWidth, size.height))
                        }

                        onDrawWithContent {
                            clipPath(path) {
                                drawRect(fillColor)
                            }
                        }
                    }
            ) {
                // Filled text
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isDone) doneLabel else text,
                        color = fillTextColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp)
                    )
                }
            }
        }
    }
}
