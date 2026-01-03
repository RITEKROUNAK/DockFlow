package com.dockflow.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

/**
 * Subtle wavy progress bar with animated wave motion
 */
@Composable
fun WaveformProgressBar(
    progress: Float,
    currentTime: String,
    totalTime: String,
    accentColor: Color,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Animated wavy progress bar
        AnimatedWavyProgressBar(
            progress = progress,
            accentColor = accentColor,
            onSeek = onSeek,
            isPlaying = isPlaying
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Time labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = currentTime,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp
                ),
                color = Color.White.copy(alpha = 0.9f)
            )
            Text(
                text = totalTime,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp
                ),
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun AnimatedWavyProgressBar(
    progress: Float,
    accentColor: Color,
    onSeek: (Float) -> Unit,
    isPlaying: Boolean
) {
    // Animate wave amplitude
    val waveAmplitude by animateDpAsState(
        targetValue = if (isPlaying) 3.dp else 0.dp,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "WaveAmplitude"
    )

    // Continuous wave animation
    val infiniteTransition = rememberInfiniteTransition(label = "WavePhase")
    val phaseShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PhaseShift"
    )

    val waveAmplitudePx = with(LocalDensity.current) { waveAmplitude.toPx() }
    val trackHeightPx = with(LocalDensity.current) { 3.dp.toPx() }
    val waveFrequency = 0.15f // Frequency for gentle waves

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val seekPosition = (offset.x / size.width).coerceIn(0f, 1f)
                    onSeek(seekPosition)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val centerY = size.height / 2f
            val points = 150

            // Helper function to calculate Y position with wave
            fun yAt(x: Float): Float {
                val s = sin(waveFrequency * x + phaseShift)
                return centerY + waveAmplitudePx * s
            }

            // Draw inactive track (full length)
            val inactivePath = Path()
            var prevX = 0f
            var prevY = yAt(prevX)
            inactivePath.moveTo(prevX, prevY)

            for (i in 1..points) {
                val x = (i.toFloat() / points) * width
                val y = yAt(x)
                val midX = (prevX + x) * 0.5f
                val midY = (prevY + y) * 0.5f
                inactivePath.quadraticBezierTo(prevX, prevY, midX, midY)
                prevX = x
                prevY = y
            }
            inactivePath.quadraticBezierTo(prevX, prevY, width, yAt(width))

            drawPath(
                path = inactivePath,
                color = Color.White.copy(alpha = 0.3f),
                style = Stroke(
                    width = trackHeightPx,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Draw active track (progress portion)
            if (progress > 0f) {
                val activePath = Path()
                val progressWidth = width * progress
                val activePoints = (points * progress).toInt().coerceAtLeast(1)

                prevX = 0f
                prevY = yAt(prevX)
                activePath.moveTo(prevX, prevY)

                for (i in 1..activePoints) {
                    val x = (i.toFloat() / points) * width
                    val y = yAt(x)
                    val midX = (prevX + x) * 0.5f
                    val midY = (prevY + y) * 0.5f
                    activePath.quadraticBezierTo(prevX, prevY, midX, midY)
                    prevX = x
                    prevY = y
                }
                activePath.quadraticBezierTo(prevX, prevY, progressWidth, yAt(progressWidth))

                drawPath(
                    path = activePath,
                    color = accentColor,
                    style = Stroke(
                        width = trackHeightPx,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}
