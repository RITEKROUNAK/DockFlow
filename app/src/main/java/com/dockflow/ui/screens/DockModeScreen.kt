package com.dockflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dockflow.service.MediaSessionService
import com.dockflow.ui.components.BatteryIndicator
import com.dockflow.ui.components.ClockSection
import com.dockflow.ui.components.GlassmorphicBackground
import com.dockflow.ui.components.MusicPlayerSection
import com.dockflow.ui.theme.ThemeManager
import com.dockflow.ui.theme.ThemeType
import com.dockflow.util.ColorExtractor
import kotlinx.coroutines.launch

/**
 * Main dock mode screen with music player and clock
 */
@Composable
fun DockModeScreen(
    themeManager: ThemeManager,
    currentTheme: ThemeType
) {
    LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Observe media metadata from the service
    val mediaMetadata by MediaSessionService.mediaMetadata.collectAsState()
    
    // Extract vibrant color from album art
    val accentColor = remember(mediaMetadata.albumArtBitmap) {
        ColorExtractor.extractVibrantColor(
            mediaMetadata.albumArtBitmap,
            defaultColor = Color(0xFF00F5FF) // Neon cyan default
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Glassmorphic background with blurred album art
        GlassmorphicBackground(
            albumArt = mediaMetadata.albumArtBitmap,
            themeType = currentTheme
        )

        // Main content - Two column layout
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left side - Music Player
            Box(modifier = Modifier.weight(1f)) {
                MusicPlayerSection(
                    mediaMetadata = mediaMetadata,
                    accentColor = accentColor,
                    onPlayPauseClick = {
                        sendPlayPauseCommand()
                    },
                    onNextClick = {
                        sendNextCommand()
                    },
                    onPreviousClick = {
                        sendPreviousCommand()
                    },
                    onSeek = { position ->
                        MediaSessionService.seekTo(position)
                    }
                )
            }

            // Right side - Clock (shifted right)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 40.dp)
            ) {
                ClockSection(
                    onLongPress = {
                        scope.launch {
                            themeManager.cycleTheme()
                        }
                    },
                    accentColor = accentColor
                )
            }
        }
        
        // Battery indicator in top-right corner
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            BatteryIndicator(accentColor = accentColor)
        }
    }
}

/**
 * Helper functions to send media commands
 */
private fun sendPlayPauseCommand() {
    try {
        MediaSessionService.togglePlayPause()
    } catch (e: Exception) {
        // Handle error - service might not be running
    }
}

private fun sendNextCommand() {
    try {
        MediaSessionService.skipToNext()
    } catch (e: Exception) {
        // Handle error
    }
}

private fun sendPreviousCommand() {
    try {
        MediaSessionService.skipToPrevious()
    } catch (e: Exception) {
        // Handle error
    }
}
