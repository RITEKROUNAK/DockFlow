package com.dockflow.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dockflow.data.model.MediaMetadata
import com.dockflow.data.model.PlaybackState

@Composable
fun MusicPlayerSection(
    mediaMetadata: MediaMetadata,
    accentColor: Color,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AlbumArtDisplay(
            albumArt = mediaMetadata.albumArtBitmap,
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = mediaMetadata.title,
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 21.sp),
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(14.dp))

        WaveformProgressBar(
            progress = mediaMetadata.getProgress(),
            currentTime = mediaMetadata.getFormattedPosition(),
            totalTime = mediaMetadata.getFormattedDuration(),
            accentColor = accentColor,
            onSeek = onSeek,
            isPlaying = mediaMetadata.playbackState == PlaybackState.PLAYING
        )

        Spacer(modifier = Modifier.height(1.dp))

        PlaybackControls(
            isPlaying = mediaMetadata.playbackState == PlaybackState.PLAYING,
            accentColor = accentColor,
            onPlayPauseClick = onPlayPauseClick,
            onNextClick = onNextClick,
            onPreviousClick = onPreviousClick
        )
    }
}

@Composable
private fun AlbumArtDisplay(
    albumArt: Bitmap?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (albumArt != null) {
            Image(
                bitmap = albumArt.asImageBitmap(),
                contentDescription = "Album art",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "No album art",
                modifier = Modifier.size(100.dp),
                tint = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun ProgressBar(
    progress: Float,
    currentTime: String,
    totalTime: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = Color.White.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = currentTime,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )
            Text(
                text = totalTime,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun PlaybackControls(
    isPlaying: Boolean,
    accentColor: Color,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    clip = false
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.4f),
                            Color.White.copy(alpha = 0.25f)
                        )
                    )
                )
                .clickable(onClick = onPreviousClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = "Previous",
                modifier = Modifier.size(40.dp),
                tint = Color.White
            )
        }

        Box(
            modifier = Modifier
                .size(84.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    clip = false
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 1f),
                            accentColor.copy(alpha = 0.8f)
                        )
                    )
                )
                .clickable(onClick = onPlayPauseClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                modifier = Modifier.size(42.dp),
                tint = Color.White
            )
        }

        Box(
            modifier = Modifier
                .size(68.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    clip = false
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.4f),
                            Color.White.copy(alpha = 0.25f)
                        )
                    )
                )
                .clickable(onClick = onNextClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Next",
                modifier = Modifier.size(40.dp),
                tint = Color.White
            )
        }
    }
}
