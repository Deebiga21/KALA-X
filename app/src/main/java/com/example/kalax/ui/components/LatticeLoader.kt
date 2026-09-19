package com.example.kalax.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LatticeLoader(
    modifier: Modifier = Modifier,
    status: String = "working", // "working", "done", "error"
    label: String = "Thinking",
    doneLabel: String = "Done in",
    errorLabel: String = "Failed after",
    elapsedSeconds: Int = 0,
    showTimer: Boolean = true,
    gridSize: Int = 3,
    cellSize: Int = 6,
    gap: Int = 2,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    doneColor: Color = Color(0xFF22C55E),
    errorColor: Color = Color(0xFFEF4444),
    idleOpacity: Float = 0.15f
) {
    // Infinite transition for orbit animation
    val infiniteTransition = rememberInfiniteTransition(label = "lattice")
    val orbitIndex by infiniteTransition.animateValue(
        initialValue = 0,
        targetValue = 8,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbitIndex"
    )

    // The sequence for a 3x3 outer ring orbit
    val orbitSequence = listOf(0, 1, 2, 5, 8, 7, 6, 3)
    val activeCell = orbitSequence.getOrNull(orbitIndex % orbitSequence.size) ?: 0

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        // Grid Graphic
        Column(verticalArrangement = Arrangement.spacedBy(gap.dp)) {
            for (row in 0 until gridSize) {
                Row(horizontalArrangement = Arrangement.spacedBy(gap.dp)) {
                    for (col in 0 until gridSize) {
                        val index = row * gridSize + col
                        val isCenter = index == 4
                        val isActive = status == "working" && index == activeCell
                        
                        val cellColor = when {
                            status == "done" -> doneColor
                            status == "error" -> errorColor
                            isCenter && status == "working" -> activeColor.copy(alpha = 0.5f)
                            isActive -> activeColor
                            else -> activeColor.copy(alpha = idleOpacity)
                        }

                        Box(
                            modifier = Modifier
                                .size(cellSize.dp)
                                .clip(CircleShape)
                                .background(cellColor)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Label Text
        val timeString = if (elapsedSeconds < 60) {
            "${elapsedSeconds}s"
        } else {
            val mins = elapsedSeconds / 60
            val secs = elapsedSeconds % 60
            "${mins}m ${secs}s"
        }
        
        val displayLabel = buildString {
            when (status) {
                "done" -> append(doneLabel)
                "error" -> append(errorLabel)
                else -> append(label)
            }
            if (showTimer) {
                append(" • ")
                append(timeString)
            }
        }
        
        val textColor = when (status) {
            "done" -> doneColor
            "error" -> errorColor
            else -> MaterialTheme.colorScheme.onBackground
        }

        Text(
            text = displayLabel,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
