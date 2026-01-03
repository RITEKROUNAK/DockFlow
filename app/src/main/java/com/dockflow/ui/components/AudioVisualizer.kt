package com.dockflow.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.material3.MaterialTheme
import kotlin.random.Random

/**
 * Audio visualizer with animated bars
 */
@Composable
fun AudioVisualizer(
    isPlaying: Boolean,
    primaryColor: Color,
    modifier: Modifier = Modifier
) {
    val secondaryColor = primaryColor.copy(alpha = 0.7f)
    
    // Number of bars in the visualizer
    val barCount = 40
    
    // Animated heights for each bar - create inside Composable
    val infiniteTransition = rememberInfiniteTransition(label = "visualizer")
    
    val animatedHeights = (0 until barCount).map { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 300 + (index * 20),
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_$index"
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val barWidth = canvasWidth / (barCount * 2)
        val spacing = barWidth

        for (i in 0 until barCount) {
            val x = i * (barWidth + spacing) + barWidth / 2
            val heightMultiplier = if (isPlaying) animatedHeights[i].value else 0.1f
            val barHeight = canvasHeight * heightMultiplier

            // Create gradient for each bar
            val gradient = Brush.verticalGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.8f),
                    secondaryColor.copy(alpha = 0.6f)
                ),
                startY = canvasHeight - barHeight,
                endY = canvasHeight
            )

            drawLine(
                brush = gradient,
                start = Offset(x, canvasHeight - barHeight),
                end = Offset(x, canvasHeight),
                strokeWidth = barWidth,
                cap = StrokeCap.Round
            )
        }
    }
}
